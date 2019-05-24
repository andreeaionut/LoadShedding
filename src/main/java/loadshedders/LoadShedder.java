package loadshedders;

import core.GlobalResult;
import core.LoadShedderType;
import core.SoldierStatusReport;
import managers.LoadSheddingManager;

import java.util.*;

public abstract class LoadShedder {

    private static int LS_PERCENT_STEP_SIZE = 10;
    private static int MAX_LS_PERCENT = 90;

    LoadSheddingManager loadSheddingManager;
    private HashMap<Integer, SoldierStatusReport> standardResults;
    private GlobalResult globalResult;
    protected LoadShedderType loadShedderType;

    public LoadShedder(){}

    LoadShedder(String inputFile){
        this.loadSheddingManager = new LoadSheddingManager(inputFile);
        this.standardResults = this.loadSheddingManager.getStandardResults();
        this.globalResult = this.loadSheddingManager.getStandardGlobalResult();
    }

    private void compareResults(int loadSheddingValue, HashMap<Integer, SoldierStatusReport> results){
        Iterator it = results.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReportLS = (SoldierStatusReport) pair.getValue();
            SoldierStatusReport soldierStatusReportStandard = this.standardResults.get(pair.getKey());
            double error = Math.abs(soldierStatusReportStandard.getMean() - soldierStatusReportLS.getMean());
            System.out.println("LS " + loadSheddingValue + " % ID " + soldierStatusReportLS.getId() + " Mean error: " + error);
            it.remove();
        }
    }

    protected abstract void dropTuples(int dropPercent, SoldierStatusReport soldierStatusReport);

    public List<Double> shedLoad(){
        List<Double> errors = new ArrayList<>();

        HashMap<Integer, SoldierStatusReport> resultsBeforeLoadShedding;
        HashMap<Integer, SoldierStatusReport> loadSheddedResults;
        int currentLoadSheddingPercent = LS_PERCENT_STEP_SIZE;
        System.out.println("Global standard mean: " + this.globalResult.getMean());
        while(currentLoadSheddingPercent <= MAX_LS_PERCENT){
            loadSheddedResults = new HashMap<Integer, SoldierStatusReport>();
            resultsBeforeLoadShedding = loadSheddingManager.getStandardResults();
            Iterator it = resultsBeforeLoadShedding.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
                int valuesDropped = Math.round(soldierStatusReport.getNumberOfValues() * currentLoadSheddingPercent / 100);
                this.dropTuples(valuesDropped, soldierStatusReport);
                loadSheddedResults.put((Integer) pair.getKey(), soldierStatusReport);
                it.remove();
            }
            //this.compareResults(currentLoadSheddingPercent, loadSheddedResults);
            GlobalResult loadSheddedGlobalResult = this.loadSheddingManager.calculateGlobalResultFromHashMap(loadSheddedResults);
            double error = Math.abs(this.globalResult.getMean() - loadSheddedGlobalResult.getMean());
            System.out.println("LS " + currentLoadSheddingPercent + " % " + " Mean error: " + error);
            errors.add(error);
            currentLoadSheddingPercent += LS_PERCENT_STEP_SIZE;
        }
        return errors;
    }

    public LoadShedderType getLoadShedderType() {
        return loadShedderType;
    }

    public void setLoadShedderType(LoadShedderType loadShedderType) {
        this.loadShedderType = loadShedderType;
    }
}
