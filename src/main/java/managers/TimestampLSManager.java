package managers;

import core.GlobalResult;
import core.Soldier;
import utils.CpuLoad;
import utils.Utils;

import java.util.*;

public class TimestampLSManager {

    private List<Soldier> data;
    private HashMap<Integer, List<Soldier>> results = new HashMap<>();
    private HashMap<Integer, GlobalResult> standardResults;

    public TimestampLSManager(String inputFile, int computationFieldNumber) {
        this.data = FileManager.getDataFromFile(inputFile, computationFieldNumber);
        this.createTimestampHashMap();
        this.standardResults = createStandardResults();
    }

    private void createTimestampHashMap(){
        this.results.clear();
        for(Soldier soldier : this.data){
            int timestamp = soldier.getTimestamp();
            if(results.containsKey(timestamp)){
                results.get(timestamp).add(soldier);
            }else{
                results.put(timestamp, new ArrayList<>());
                results.get(timestamp).add(soldier);
            }
        }
    }

    public HashMap<Integer, List<Soldier>> getData(){
        return Utils.copySoldiersHashmap(results);
    }

    public double getStandardDeviation(List<Soldier> soldiers, double mean){
        double standardDeviation = 0;
        for (Soldier soldier : soldiers) {
            double partialResult = Math.abs(soldier.getBodyTemperature() - mean);
            standardDeviation += Math.pow(partialResult, 2);
        }
        standardDeviation = Math.sqrt(standardDeviation/soldiers.size());
        return standardDeviation;
    }

    public HashMap<Integer, GlobalResult> createStandardResults(){
        HashMap<Integer, GlobalResult> results = new HashMap<>();
        HashMap<Integer, List<Soldier>> resultsCopy = Utils.copySoldiersHashmap(this.results);
        Iterator it = resultsCopy.entrySet().iterator();
        float smoothLoad = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<Soldier> soldiersPerTimestamp = (List<Soldier>) pair.getValue();
            int timestamp = (int) pair.getKey();
            double meanSum = 0;
            int numberOfValues = 0;
            //long begin = System.currentTimeMillis();
            long lastTime = System.nanoTime();
            long lastThreadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();

            for(Soldier soldier : soldiersPerTimestamp){
                meanSum += soldier.getBodyTemperature();
                numberOfValues++;
            }
            double mean = meanSum/numberOfValues;
            double standardDeviation = this.getStandardDeviation(soldiersPerTimestamp, mean);
//            long end = System.currentTimeMillis();
//            long dt = (end - begin);
            long time = System.nanoTime();
            long threadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();
            double dt = (threadTime - lastThreadTime) / (double)(time - lastTime);
            smoothLoad += (dt - smoothLoad) * 0.4;

            GlobalResult globalResult = new GlobalResult();
            globalResult.setMean(mean);
            globalResult.setStandardDeviation(standardDeviation);
            globalResult.setStandardCalculationTime(smoothLoad);
            results.put((Integer) pair.getKey(), globalResult);
            it.remove();
        }
        return results;
    }

    public HashMap<Integer, GlobalResult> getStandardResults() {
        return standardResults;
    }

    public void setStandardResults(HashMap<Integer, GlobalResult> standardResults) {
        this.standardResults = standardResults;
    }
}
