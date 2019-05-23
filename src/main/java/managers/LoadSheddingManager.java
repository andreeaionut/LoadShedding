package managers;

import core.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoadSheddingManager {

    private List<Soldier> data;
    private HashMap<Integer, SoldierStatusReport> results = new HashMap<Integer, SoldierStatusReport>();

    private String inputFile;

    public LoadSheddingManager(String inputFile) {
        this.inputFile = inputFile;
        this.data = FileManager.getDataFromFile(inputFile);
    }

    public int getDataSize(){
        if(this.data == null){
            return 0;
        }
        return this.data.size();
    }

    public HashMap<Integer, SoldierStatusReport> getStandardResults(){
        List<Soldier> soldiers = FileManager.getDataFromFile(this.inputFile);
        return this.getResultsFromList(soldiers);
    }

    private HashMap<Integer, SoldierStatusReport> getResultsFromList(List<Soldier> soldiers){
        while(soldiers.size() > 0){
            Soldier soldier = soldiers.get(0);
            SoldierStatusReport soldierStatusReport;
            if(!results.containsKey(soldier.getId())){
                soldierStatusReport = new SoldierStatusReport();
                soldierStatusReport.setId(soldier.getId());
                soldierStatusReport.setNumberOfValues(1);
                soldierStatusReport.setSumOfBodyTemperature(soldier.getBodyTemperature());
            }else{
                soldierStatusReport = results.get(soldier.getId());
                soldierStatusReport.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature() + soldier.getBodyTemperature());
                soldierStatusReport.setNumberOfValues(soldierStatusReport.getNumberOfValues() + 1);
            }
            BodyTemperatureMeasurement measurement = new BodyTemperatureMeasurement();
            measurement.setType("Body temperature level");
            double bodyTemperature = soldier.getBodyTemperature();
            measurement.setValue(bodyTemperature);
            measurement.setImportance(bodyTemperature);
            soldierStatusReport.addMeasurement(measurement);
            results.put(soldier.getId(), soldierStatusReport);
            soldiers.remove(0);
        }
        HashMap<Integer, SoldierStatusReport> finalResults = new HashMap<Integer, SoldierStatusReport>();
        Iterator it = this.results.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            this.setStatusReportResults(soldierStatusReport);
            finalResults.put(soldierStatusReport.getId(), soldierStatusReport);
            it.remove();
        }
        return finalResults;
    }

    public void setStatusReportResults(SoldierStatusReport soldierStatusReport){
        soldierStatusReport.setMean(soldierStatusReport.getSumOfBodyTemperature()/soldierStatusReport.getNumberOfValues());
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

    private double getGlobalMean(HashMap<Integer, SoldierStatusReport> standardResults) {
        double globalSumOfMeans = 0;
        double values = 0;
        Iterator it = standardResults.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SoldierStatusReport soldierStatusReport = (SoldierStatusReport) pair.getValue();
            globalSumOfMeans += soldierStatusReport.getMean();
            it.remove();
            values++;
        }
        return globalSumOfMeans / values;
    }

    private double getGlobalStandardDeviation(double mean, HashMap<Integer, SoldierStatusReport> standardResults) {
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
        globalStandardDeviation = globalStandardDeviation / values;
        return globalStandardDeviation;
    }

    public GlobalResult getStandardGlobalResult() {
        HashMap<Integer, SoldierStatusReport> results = this.getStandardResults();
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

}
