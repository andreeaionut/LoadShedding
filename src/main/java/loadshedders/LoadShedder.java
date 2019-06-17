package loadshedders;

import core.GlobalResult;
import core.LoadShedderType;
import core.SoldierStatusReport;
import managers.LoadSheddingManager;
import utils.Utils;

import java.util.*;

public abstract class LoadShedder {

    private static int LS_PERCENT_STEP_SIZE = 10;
    private static int MAX_LS_PERCENT = 90;

    LoadSheddingManager loadSheddingManager;
    protected LoadShedderType loadShedderType;

    private HashMap<Integer, SoldierStatusReport> standardResults;
    private HashMap<Integer, GlobalResult> loadSheddedResults;
    private GlobalResult globalResult;

    public LoadShedder(){
        this.standardResults = this.loadSheddingManager.getStandardResults();
    }

    LoadShedder(String inputFile){
        this.loadSheddingManager = new LoadSheddingManager(inputFile);
        this.standardResults = this.loadSheddingManager.getStandardResults();
        this.globalResult = this.loadSheddingManager.getGlobalResult();
    }

    protected abstract void dropTuples(int dropPercent, SoldierStatusReport soldierStatusReport);

    public HashMap<Integer, GlobalResult> getStandardResult(){
        return this.loadSheddingManager.getStandardResult();
    }

    public HashMap<Integer, GlobalResult> shedLoad(){
        HashMap<Integer, GlobalResult> errors = new HashMap<>();

        HashMap<Integer, SoldierStatusReport> resultsBeforeLoadShedding;
        HashMap<Integer, SoldierStatusReport> loadSheddedResults;
        int currentLoadSheddingPercent = LS_PERCENT_STEP_SIZE;
        System.out.println("Global standard mean: " + this.globalResult.getMean());

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

            GlobalResult loadSheddedGlobalResult = this.loadSheddingManager.calculateGlobalResultFromHashMap(loadSheddedResults);
            //double mean = this.loadSheddingManager.getGlobalMean(loadSheddedResults);
            //double stddev = this.loadSheddingManager.getGlobalStandardDeviation(mean, loadSheddedResults);
            double meanError = Math.abs(loadSheddedGlobalResult.getMean() - this.globalResult.getMean());
            double stdDevError = Math.abs(loadSheddedGlobalResult.getStandardDeviation() - this.globalResult.getStandardDeviation());

            long end = System.currentTimeMillis();
            long ts = (end - begin);

            //GlobalResult loadSheddedGlobalResult = new GlobalResult();
            loadSheddedGlobalResult.setMean(meanError);
            loadSheddedGlobalResult.setStandardDeviation(stdDevError);
            loadSheddedGlobalResult.setLsCalculationTime(ts);
            loadSheddedGlobalResult.setLoadSheddingPercent(currentLoadSheddingPercent);
            errors.put(currentLoadSheddingPercent, loadSheddedGlobalResult);
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
