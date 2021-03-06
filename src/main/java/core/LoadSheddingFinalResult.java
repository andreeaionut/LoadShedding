package core;

import utils.Utils;

import java.util.HashMap;

public class LoadSheddingFinalResult {

    private LoadShedderType loadShedderType;
    private Computation computationType;

    private HashMap<Integer, Result> standardResults;
    private HashMap<Integer, Result> loadSheddedResults;

    public LoadSheddingFinalResult() {
        this.standardResults = new HashMap<>();
        this.loadSheddedResults = new HashMap<>();
    }

    public static LoadSheddingFinalResult copy(LoadSheddingFinalResult value) {
        LoadSheddingFinalResult loadSheddingFinalResult = new LoadSheddingFinalResult();
        loadSheddingFinalResult.setLoadShedderType(value.getLoadShedderType());
        loadSheddingFinalResult.setLoadSheddedResults(Utils.copyGlobalResultsReportHashmap(value.loadSheddedResults));
        loadSheddingFinalResult.setStandardResults(Utils.copyGlobalResultsReportHashmap(value.standardResults));
        loadSheddingFinalResult.setComputationType(value.computationType);
        return loadSheddingFinalResult;
    }


    public HashMap<Integer, Result> getStandardResults() {
        return standardResults;
    }

    public void setStandardResults(HashMap<Integer, Result> standardResults) {
        this.standardResults = standardResults;
    }

    public HashMap<Integer, Result> getLoadSheddedResults() {
        return loadSheddedResults;
    }

    public void setLoadSheddedResults(HashMap<Integer, Result> loadSheddedResults) {
        this.loadSheddedResults = loadSheddedResults;
    }

    public LoadShedderType getLoadShedderType() {
        return loadShedderType;
    }

    public void setLoadShedderType(LoadShedderType loadShedderType) {
        this.loadShedderType = loadShedderType;
    }

    public Computation getComputationType() {
        return computationType;
    }

    public void setComputationType(Computation computationType) {
        this.computationType = computationType;
    }
}
