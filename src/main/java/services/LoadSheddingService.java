package services;

import core.*;
import global.GlobalLoadShedder;
import factories.GlobalLoadSheddersFactory;
import factories.TimestampLoadSheddersFactory;
import timestamp.TimestampLoadShedder;
import utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadSheddingService {

    private HashMap<Integer, Result> globalResults = new HashMap<>();
    private HashMap<Integer, List<Result>> loadSheddedResultsPerTimestamp = new HashMap<>();
    private HashMap<Integer, Result> resultsPerPercent = new HashMap<>();
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
            GlobalLoadShedder globalLoadShedder = null;
            switch(loadShedderType){
                case RANDOM:
                {
                    if(this.loadSheddingGlobalRandomFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        globalLoadShedder = GlobalLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        globalResults = globalLoadShedder.shedLoad();
                        this.loadSheddingGlobalRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingGlobalRandomFinalResult.setLoadSheddedResults(globalResults);
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingGlobalRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingGlobalSemanticFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        globalLoadShedder = GlobalLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        globalResults = globalLoadShedder.shedLoad();
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
                        TimestampLoadShedder timestampLoadShedder = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        this.loadSheddedResultsPerTimestamp = timestampLoadShedder.shedLoad();
                        this.resultsPerPercent = timestampLoadShedder.getResultsPerPercent();
                        this.loadSheddingTimestampRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampRandomFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampRandomFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                        this.loadSheddingTimestampRandomFinalResult.setStandardResults(timestampLoadShedder.getStandardResults());
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingTimestampRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingTimestampSemanticFinalResult == null || this.previousComputationFieldNumber != computationFieldNumber){
                        TimestampLoadShedder timestampLoadShedder = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile, computationFieldNumber);
                        this.loadSheddedResultsPerTimestamp = timestampLoadShedder.shedLoad();
                        this.resultsPerPercent = timestampLoadShedder.getResultsPerPercent();
                        this.loadSheddingTimestampSemanticFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampSemanticFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampSemanticFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                        this.loadSheddingTimestampSemanticFinalResult.setStandardResults(timestampLoadShedder.getStandardResults());
                    }
                    this.previousComputationFieldNumber = computationFieldNumber;
                    return this.loadSheddingTimestampSemanticFinalResult;
                }
            }
        }
        return null;
    }

    public Result getSuggestedSettings(Computation computationType, LoadShedderType loadShedderType, String minimumValueType, double maximumMeanError, double maximumStddevError){
        LoadSheddingFinalResult loadSheddingFinalResult = this.getLoadSheddingFinalResultByComputationAndType(computationType, loadShedderType);
        Map<Integer, Result> sortedResults = Utils.sortByComparator(loadSheddingFinalResult.getLoadSheddedResults(), minimumValueType, ASC);
        for (Map.Entry<Integer, Result> entry : sortedResults.entrySet()) {
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

    public HashMap<Integer, Result> getGlobalResults() {
        return globalResults;
    }

    public HashMap<Integer, Result> getResultsPerPercent() {
        return resultsPerPercent;
    }

    public LoadSheddingFinalResult getLoadSheddingGlobalFinalResult() {
        return loadSheddingGlobalFinalResult;
    }

    public List<Result> getLoadSheddedResultsPerGivenTimestamp(int timestamp) {
        return this.loadSheddedResultsPerTimestamp.get(timestamp);
    }

    public HashMap<Integer, List<Result>> getLoadSheddedResultsPerTimestamp() {
        return loadSheddedResultsPerTimestamp;
    }
}
