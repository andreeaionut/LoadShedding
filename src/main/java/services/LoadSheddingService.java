package services;

import core.Computation;
import core.GlobalResult;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import loadshedders.LoadShedder;
import loadshedders.LoadShedderComparator;
import managers.LoadSheddersFactory;
import managers.TimestampLoadSheddersFactory;
import timestamp.LoadShedderTS;
import utils.Utils;

import java.util.HashMap;
import java.util.List;

public class LoadSheddingService {

    private HashMap<Integer, GlobalResult> globalResults = new HashMap<>();
    private HashMap<Integer, GlobalResult> resultsPerPercent = new HashMap<>();
    private HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors;

    private LoadSheddingFinalResult loadSheddingTimestampRandomFinalResult;
    private LoadSheddingFinalResult loadSheddingTimestampSemanticFinalResult;

    private LoadSheddingFinalResult loadSheddingGlobalRandomFinalResult;
    private LoadSheddingFinalResult loadSheddingGlobalSemanticFinalResult;

    private LoadSheddingFinalResult loadSheddingGlobalFinalResult;

    private LoadShedderComparator loadShedderComparator;

    public LoadSheddingService() {
        this.loadShedderComparator = new LoadShedderComparator(this);
        this.comparatorErrors = new HashMap<>();
    }

    public LoadSheddingFinalResult shedLoad(String inputFile, LoadShedderType loadShedderType, Computation computation){
        if(computation.equals(Computation.GLOBAL)){
            switch(loadShedderType){
                case RANDOM:
                {
                    if(this.loadSheddingGlobalRandomFinalResult == null){
                        LoadShedder loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
                        globalResults = loadShedder.shedLoad();
                        this.loadSheddingGlobalRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingGlobalRandomFinalResult.setLoadSheddedResults(globalResults);
                    }
                    return this.loadSheddingGlobalRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingGlobalSemanticFinalResult == null){
                        LoadShedder loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
                        globalResults = loadShedder.shedLoad();
                        this.loadSheddingGlobalSemanticFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingGlobalSemanticFinalResult.setLoadSheddedResults(globalResults);
                    }
                    return this.loadSheddingGlobalSemanticFinalResult;
                }
            }
            //this.loadSheddingGlobalFinalResult.setStandardResults(loadShedder.getStandardResult());
        }else{
            switch(loadShedderType){
                case RANDOM:
                {
                    if(this.loadSheddingTimestampRandomFinalResult == null){
                        LoadShedderTS loadShedderTS = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
                        loadShedderTS.shedLoad();
                        this.resultsPerPercent = loadShedderTS.getResultsPerPercent();
                        this.loadSheddingTimestampRandomFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampRandomFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampRandomFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                    }
                    return this.loadSheddingTimestampRandomFinalResult;
                }
                case SEMANTIC:
                {
                    if(this.loadSheddingTimestampSemanticFinalResult == null){
                        LoadShedderTS loadShedderTS = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
                        loadShedderTS.shedLoad();
                        this.resultsPerPercent = loadShedderTS.getResultsPerPercent();
                        this.loadSheddingTimestampSemanticFinalResult = new LoadSheddingFinalResult();
                        this.loadSheddingTimestampSemanticFinalResult.setLoadShedderType(loadShedderType);
                        this.loadSheddingTimestampSemanticFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(resultsPerPercent));
                    }
                    return this.loadSheddingTimestampSemanticFinalResult;
                }
            }
            //this.loadSheddingTimestampFinalResult.setStandardResults(loadShedderTS.getStandardResults());
        }
        return null;
    }

    public void compareLoadShedders(Computation computationType, String inputFile){
        if(this.comparatorErrors.size() == 0)
            this.comparatorErrors = this.loadShedderComparator.compareLoadShedders(computationType, inputFile);
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
}
