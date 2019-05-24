package timestamp;

import core.GlobalResult;
import core.LoadShedderType;
import core.Soldier;
import managers.TimestampLSManager;
import utils.Utils;

import java.util.*;

public abstract class LoadShedderTS {

    private static int LS_PERCENT_STEP_SIZE = 10;
    private static int MAX_LS_PERCENT = 90;

    protected TimestampLSManager loadSheddingManager;
    private HashMap<Integer, GlobalResult> standardResults;
    HashMap<Integer, List<Double>> loadSheddedResults;
    private HashMap<Integer, List<Soldier>> data;
    protected LoadShedderType loadShedderType;

    public LoadShedderTS(String inputFile){
        this.loadSheddingManager = new TimestampLSManager(inputFile);
        this.standardResults = this.loadSheddingManager.getStandardResults();
        this.data = this.loadSheddingManager.getData();
    }

    public abstract void dropTuples(int dropPercent, List<Soldier> soldiers);

    public HashMap<Integer, List<Double>> shedLoad(){
        HashMap<Integer, List<Double>> loadSheddedResults = new HashMap<>();
        HashMap<Integer, GlobalResult> resultsBeforeLoadShedding = this.standardResults;

        Iterator it = resultsBeforeLoadShedding.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int currentLoadSheddingPercent = LS_PERCENT_STEP_SIZE;
            int timestamp = (int) pair.getKey();
            List<Double> errorsPerTimestamp = new ArrayList<>();
            while(currentLoadSheddingPercent <= MAX_LS_PERCENT){
                List<Soldier> beforeLS = this.data.get(timestamp);
                dropTuples(currentLoadSheddingPercent, beforeLS);
                double mean = getMean(beforeLS);
                double error = Math.abs( this.standardResults.get(timestamp).getMean() - mean);
                errorsPerTimestamp.add(error);
                currentLoadSheddingPercent += LS_PERCENT_STEP_SIZE;
            }
            it.remove();
            loadSheddedResults.put(timestamp, errorsPerTimestamp);
        }
        this.loadSheddedResults = loadSheddedResults;
        return loadSheddedResults;
    }

    public HashMap<Integer, Double> getResultsPerPercent(){
        HashMap<Integer, Double> result = new HashMap<>();
        int currentPercent = LS_PERCENT_STEP_SIZE;
        while(currentPercent <= MAX_LS_PERCENT) {
            double sum = 0;
            int values = 0;
            for (Map.Entry<Integer, List<Double>> entry : this.loadSheddedResults.entrySet()) {
                sum += entry.getValue().get(currentPercent/10 - 1);
                values++;
            }
            double meanError = sum / values;
            result.put(currentPercent, meanError);
            currentPercent += LS_PERCENT_STEP_SIZE;
        }
        return result;
    }

    private double getMean(List<Soldier> soldiers) {
        double sum = 0;
        double values = 0;
        for(Soldier soldier : soldiers){
            sum += soldier.getBodyTemperature();
            values ++;
        }
        return sum/values;
    }


}
