package managers;

import core.*;
import utils.CpuLoad;
import utils.Utils;

import java.util.*;

public class LoadSheddingManager {

    private List<Soldier> data;
    private HashMap<Integer, SoldierStatusReport> standardResults = new HashMap<Integer, SoldierStatusReport>();
    private GlobalResult globalResult;

    private String inputFile;

    public LoadSheddingManager(String inputFile, int computationFieldNumber) {
        this.inputFile = inputFile;
        this.data = FileManager.getDataFromFile(inputFile, computationFieldNumber);
        createStandardResults();
        this.globalResult = this.getStandardGlobalResult();
    }

    public HashMap<Integer, SoldierStatusReport> getStandardResults(){
        return this.standardResults;
    }

    private void createStandardResults(){
        this.standardResults = this.getResultsFromList(this.data);
    }

    private HashMap<Integer, SoldierStatusReport> getResultsFromList(List<Soldier> soldiers){
        for(Soldier soldier : soldiers){
            SoldierStatusReport soldierStatusReport;
            if(!standardResults.containsKey(soldier.getId())){
                soldierStatusReport = new SoldierStatusReport();
                soldierStatusReport.setId(soldier.getId());
                soldierStatusReport.setNumberOfValues(1);
                //soldierStatusReport.setSumOfBodyTemperature(soldier.getBodyTemperature());
            }else{
                soldierStatusReport = standardResults.get(soldier.getId());
                //soldierStatusReport.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature() + soldier.getBodyTemperature());
                soldierStatusReport.setNumberOfValues(soldierStatusReport.getNumberOfValues() + 1);
            }
            BodyTemperatureMeasurement measurement = new BodyTemperatureMeasurement();
            measurement.setType("Body temperature level");
            double bodyTemperature = soldier.getBodyTemperature();
            measurement.setValue(bodyTemperature);
            measurement.setImportance(bodyTemperature);
            soldierStatusReport.addMeasurement(measurement);
            standardResults.put(soldier.getId(), soldierStatusReport);
        }
        HashMap<Integer, SoldierStatusReport> finalResults = new HashMap<Integer, SoldierStatusReport>();
        Iterator it = this.standardResults.entrySet().iterator();
        float smoothLoad = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            long lastTime = System.nanoTime();
            long lastThreadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();
            this.setStatusReportResults(soldierStatusReport);
            long time = System.nanoTime();
            long threadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();
            double dt = (threadTime - lastThreadTime) / (double)(time - lastTime);
            smoothLoad += (dt - smoothLoad) * 0.4;
            soldierStatusReport.setStandardCalculationTime(smoothLoad);
            finalResults.put(soldierStatusReport.getId(), soldierStatusReport);
            it.remove();
        }
        return finalResults;
    }

    public void setStatusReportResults(SoldierStatusReport soldierStatusReport){
        soldierStatusReport.setMean(soldierStatusReport.getMean());
        double standardDeviation = this.getStandardDeviation(soldierStatusReport.getMeasurements(), soldierStatusReport.getMean());
        soldierStatusReport.setStandardDeviation(standardDeviation);
    }

    private double getStandardDeviation(List<Measurement> measurements, double mean){
        double standardDeviation = 0;
        for (Measurement measurement : measurements) {
            double partialResult = Math.abs((Double)measurement.getValue() - mean);
            standardDeviation += Math.pow(partialResult, 2);
        }
        standardDeviation = Math.sqrt(standardDeviation/measurements.size());
        return standardDeviation;
    }

    public List<Integer> getSoldiersIdsRange(HashMap<Integer, SoldierStatusReport> soldiers){
        HashMap<Integer, SoldierStatusReport> standardResultsCopy = Utils.copySoldierStatusReportHashmap(soldiers);
        Iterator it = standardResultsCopy.entrySet().iterator();
        int lastId = 0;
        int firstId = Integer.MAX_VALUE;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int id = (int) pair.getKey();
            if(id>lastId){
                lastId = id;
            }
            if(id < firstId){
                firstId = id;
            }
            it.remove();
        }
        List<Integer> ids = new ArrayList<>();
        ids.add(firstId);
        ids.add(lastId);
        return ids;
    }

    public double getGlobalMean(HashMap<Integer, SoldierStatusReport> standardResults) {
        double globalSumOfMeans = 0;
        double values = 0;
        HashMap<Integer, SoldierStatusReport> standardResultsCopy = Utils.copySoldierStatusReportHashmap(standardResults);
        Iterator it = standardResultsCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            globalSumOfMeans += soldierStatusReport.getMean();
            values++;
            it.remove();
        }
        return globalSumOfMeans / values;
    }

    public double getGlobalStandardDeviation(double mean, HashMap<Integer, SoldierStatusReport> standardResults) {
        double globalStandardDeviation = 0;
        double values = 0;
        Iterator it = standardResults.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            globalStandardDeviation += Math.pow(Math.abs(soldierStatusReport.getMean() - mean), 2);
            it.remove();
            values++;
        }
        globalStandardDeviation = Math.sqrt(globalStandardDeviation / values);
        return globalStandardDeviation;
    }

    public GlobalResult getStandardGlobalResult() {
        HashMap<Integer, SoldierStatusReport> results = Utils.copySoldierStatusReportHashmap(this.standardResults);
        return this.calculateGlobalResultFromHashMap(results);
    }

    public GlobalResult calculateGlobalResultFromHashMap(HashMap<Integer, SoldierStatusReport> results){
        double mean = this.getGlobalMean(results);
        double standardDeviation = this.getGlobalStandardDeviation(mean, results);
        GlobalResult globalResult = new GlobalResult();
        globalResult.setMean(mean);
        globalResult.setStandardDeviation(standardDeviation);
        return globalResult;
    }

    public HashMap<Integer,GlobalResult> getStandardResult() {
        HashMap<Integer, GlobalResult> result = new HashMap<>();
        HashMap<Integer, SoldierStatusReport> results = Utils.copySoldierStatusReportHashmap(this.standardResults);

        Iterator it = results.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Integer percent = (Integer) pair.getKey();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            GlobalResult globalResult = new GlobalResult();
            globalResult.setLoadSheddingPercent(percent);
            globalResult.setStandardDeviation(soldierStatusReport.getStandardDeviation());
            globalResult.setMean(soldierStatusReport.getMean());
            globalResult.setStandardCalculationTime(soldierStatusReport.getStandardCalculationTime());
            result.put(percent, globalResult);
            it.remove();
        }

        return result;
    }

    public GlobalResult getGlobalResult() {
        return globalResult;
    }
}
