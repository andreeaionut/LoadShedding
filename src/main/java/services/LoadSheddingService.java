package services;

import core.*;
import loadshedders.LoadShedder;
import loadshedders.LoadShedderComparator;
import managers.LoadSheddersFactory;
import managers.TimestampLoadSheddersFactory;
import timestamp.LoadShedderTS;
import utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadSheddingService {

    private HashMap<Integer, GlobalResult> globalResults = new HashMap<>();
    private HashMap<Integer, List<GlobalResult>> loadSheddedResultsPerTimestamp = new HashMap<>();
    private HashMap<Integer, GlobalResult> resultsPerPercent = new HashMap<>();
    private HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors;

    private LoadSheddingFinalResult loadSheddingTimestampRandomFinalResult;
    private LoadSheddingFinalResult loadSheddingTimestampSemanticFinalResult;

    private LoadSheddingFinalResult loadSheddingGlobalRandomFinalResult;
    private LoadSheddingFinalResult loadSheddingGlobalSemanticFinalResult;

    private LoadSheddingFinalResult loadSheddingGlobalFinalResult;

    private int previousComputationFieldNumber = -1;

    public static boolean ASC = true;
    public static boolean DESC = false;

    private LoadShedderComparator loadShedderComparator;

    public LoadSheddingService() {
        this.loadShedderComparator = new LoadShedderComparator(this);
        this.comparatorErrors = new HashMap<>();
    }

    public LoadSheddingFinalResult shedLoad(String inputFile, LoadShedderType loadShedderType, Computation computation, int computationFieldNumber){
        if(computation.equals(Computation.GLOBAL)){
            LoadShedder loadShedder = null;
            switch(loadShedderType){
                case RANDOM:
                {
                    if(this.loadSheddingGlobalRandomFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        globalResults = loadShedder.shedLoad();
                        this.loadSheddingGlobalRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingGlobalRandomFinalResult.setLoadSheddedResults(globalResults);
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingGlobalRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingGlobalSemanticFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        globalResults = loadShedder.shedLoad();
                        this.loadSheddingGlobalSemanticFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingGlobalSemanticFinalResult.setLoadSheddedResults(globalResults);
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingGlobalSemanticFinalResult;
                }
            }
        }else{
            switch(loadShedderType){
                case RANDOM:
                {
                    if(this.loadSheddingTimestampRandomFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        LoadShedderTS loadShedderTS = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        this.loadSheddedResultsPerTimestamp = loadShedderTS.shedLoad();
                        this.resultsPerPercent = loadShedderTS.getResultsPerPercent();
                        this.loadSheddingTimestampRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampRandomFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampRandomFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                        this.loadSheddingTimestampRandomFinalResult.setStandardResults(loadShedderTS.getStandardResults());
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingTimestampRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingTimestampSemanticFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        LoadShedderTS loadShedderTS = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        this.loadSheddedResultsPerTimestamp = loadShedderTS.shedLoad();
                        this.resultsPerPercent = loadShedderTS.getResultsPerPercent();
                        this.loadSheddingTimestampSemanticFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampSemanticFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampSemanticFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                        this.loadSheddingTimestampSemanticFinalResult.setStandardResults(loadShedderTS.getStandardResults());
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingTimestampSemanticFinalResult;
                }
            }
        }
        return null;
    }

    public GlobalResult getSuggestedSettings(Computation computationType, LoadShedderType loadShedderType, String minimumValueType, double maximumMeanError, double maximumStddevError){
        LoadSheddingFinalResult loadSheddingFinalResult = this.getLoadSheddingFinalResultByComputationAndType(computationType, loadShedderType);
        Map<Integer, GlobalResult> sortedResults = Utils.sortByComparator(loadSheddingFinalResult.getLoadSheddedResults(), minimumValueType, ASC);
        for (Map.Entry<Integer, GlobalResult> entry : sortedResults.entrySet()) {
            if(maximumMeanError == -1 && maximumStddevError == -1){
                return entry.getValue();
            }
            if(maximumMeanError != -1 && maximumStddevError != -1 && entry.getValue().getMean()<maximumMeanError && entry.getValue().getStandardDeviation()<maximumStddevError){
                return entry.getValue();
            }
            if(maximumMeanError != -1 && maximumStddevError == -1 && entry.getValue().getMean()<maximumMeanError){
                return entry.getValue();
            }
            if(maximumStddevError != -1 && maximumMeanError == -1 && entry.getValue().getStandardDeviation()<maximumStddevError){
                return entry.getValue();
            }
        }
        return null;
    }


    public void compareLoadShedders(Computation computationType, String inputFile, int computationFieldNumber){
        if(this.comparatorErrors.size() == 0)
            this.comparatorErrors = this.loadShedderComparator.compareLoadShedders(computationType, inputFile, computationFieldNumber);
    }

    private LoadSheddingFinalResult getLoadSheddingFinalResultByComputationAndType(Computation computationType, LoadShedderType loadShedderType){
        if(computationType.equals(Computation.GLOBAL)){
            if(loadShedderType.equals(LoadShedderType.RANDOM)){
                return this.loadSheddingGlobalRandomFinalResult;
            }
            return this.loadSheddingGlobalSemanticFinalResult;
        }else{
            if(loadShedderType.equals(LoadShedderType.RANDOM)){
                return this.loadSheddingTimestampRandomFinalResult;
            }
            return this.loadSheddingTimestampSemanticFinalResult;
        }
    }

    public HashMap<LoadShedderType, LoadSheddingFinalResult> getComparatorErrors(){
        return this.comparatorErrors;
    }

    public HashMap<Integer, GlobalResult> getGlobalResults() {
        return globalResults;
    }

    public HashMap<Integer, GlobalResult> getResultsPerPercent() {
        return resultsPerPercent;
    }

    public LoadSheddingFinalResult getLoadSheddingGlobalFinalResult() {
        return loadSheddingGlobalFinalResult;
    }

    public List<GlobalResult> getLoadSheddedResultsPerGivenTimestamp(int timestamp) {
        return this.loadSheddedResultsPerTimestamp.get(timestamp);
    }

    public HashMap<Integer, List<GlobalResult>> getLoadSheddedResultsPerTimestamp() {
        return loadSheddedResultsPerTimestamp;
    }
}
