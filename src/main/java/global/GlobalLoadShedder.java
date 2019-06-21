package global;

import core.Result;
import core.LoadShedderType;
import core.SoldierStatusReport;
import managers.GlobalLoadSheddingManager;
import utils.Utils;

import java.util.*;

public abstract class GlobalLoadShedder {

    private static int LS_PERCENT_STEP_SIZE = 10;
    private static int MAX_LS_PERCENT = 90;

    GlobalLoadSheddingManager globalLoadSheddingManager;
    protected LoadShedderType loadShedderType;

    private HashMap<Integer, SoldierStatusReport> standardResults;
    private HashMap<Integer, Result> loadSheddedResults;
    private Result result;

    public GlobalLoadShedder(){
        this.standardResults = this.globalLoadSheddingManager.getStandardResults();
    }

    GlobalLoadShedder(String inputFile, int computationFieldNumber){
        this.globalLoadSheddingManager = new GlobalLoadSheddingManager(inputFile, computationFieldNumber);
        this.standardResults = this.globalLoadSheddingManager.getStandardResults();
        this.result = this.globalLoadSheddingManager.getResult();
    }

    protected abstract void dropTuples(int dropPercent, SoldierStatusReport soldierStatusReport);

    public HashMap<Integer, Result> getStandardResult(){
        return this.globalLoadSheddingManager.getStandardResult();
    }

    public HashMap<Integer, Result> shedLoad(){
        HashMap<Integer, Result> errors = new HashMap<>();

        HashMap<Integer, SoldierStatusReport> resultsBeforeLoadShedding;
        HashMap<Integer, SoldierStatusReport> loadSheddedResults;
        int currentLoadSheddingPercent = LS_PERCENT_STEP_SIZE;
        System.out.println("Global standard mean: " + this.result.getMean());

        while(currentLoadSheddingPercent <= MAX_LS_PERCENT){
            loadSheddedResults = new HashMap<>();
            resultsBeforeLoadShedding = Utils.copySoldierStatusReportHashmap(this.standardResults);
            Iterator it = resultsBeforeLoadShedding.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Integer soldierId = (Integer) pair.getKey();
                SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
                int droppedValues = (soldierStatusReport.getMeasurements().size() * currentLoadSheddingPercent) / 100;
                this.dropTuples(droppedValues, soldierStatusReport);
                loadSheddedResults.put(soldierId, soldierStatusReport);
                it.remove();
            }
            long begin = System.currentTimeMillis();

            Result loadSheddedResult = this.globalLoadSheddingManager.calculateGlobalResultFromHashMap(loadSheddedResults);
            //double mean = this.globalLoadSheddingManager.getGlobalMean(loadSheddedResults);
            //double stddev = this.globalLoadSheddingManager.getGlobalStandardDeviation(mean, loadSheddedResults);
            double meanError = Math.abs(loadSheddedResult.getMean() - this.result.getMean())/this.result.getMean();
            double stdDevError = Math.abs(loadSheddedResult.getStandardDeviation() - this.result.getStandardDeviation())/ this.result.getStandardDeviation();

            long end = System.currentTimeMillis();
            long ts = (end - begin);

            //Result loadSheddedResult = new Result();
            loadSheddedResult.setMeanError(meanError);
            loadSheddedResult.setStddevError(stdDevError);
            loadSheddedResult.setLsCalculationTime(ts);
            loadSheddedResult.setLoadSheddingPercent(currentLoadSheddingPercent);
            errors.put(currentLoadSheddingPercent, loadSheddedResult);
            currentLoadSheddingPercent += LS_PERCENT_STEP_SIZE;
        }
        this.loadSheddedResults = errors;
        return errors;
    }

    public LoadShedderType getLoadShedderType() {
        return loadShedderType;
    }

    public void setLoadShedderType(LoadShedderType loadShedderType) {
        this.loadShedderType = loadShedderType;
    }
}
