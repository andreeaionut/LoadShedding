package timestamp;

import core.Result;
import core.LoadShedderType;
import core.Soldier;
import managers.TimestampLoadSheddingManager;
import utils.CpuLoad;
import utils.Utils;

import java.util.*;

public abstract class TimestampLoadShedder {

    private static int LS_PERCENT_STEP_SIZE = 10;
    private static int MAX_LS_PERCENT = 90;

    private TimestampLoadSheddingManager loadSheddingManager;
    private HashMap<Integer, Result> standardResults;
    private HashMap<Integer, List<Result>> loadSheddedResults;
    private HashMap<Integer, List<Soldier>> data;
    protected LoadShedderType loadShedderType;

    public TimestampLoadShedder(String inputFile, int computationFieldNumber){
        this.loadSheddingManager = new TimestampLoadSheddingManager(inputFile, computationFieldNumber);
        this.standardResults = this.loadSheddingManager.createStandardResults();
        this.data = this.loadSheddingManager.getData();
    }

    public abstract void dropTuples(int dropPercent, List<Soldier> soldiers);

    public HashMap<Integer, Result> getStandardResults(){
        return this.standardResults;
    }

    public HashMap<Integer, List<Result>> shedLoad(){
        System.out.println("load shedding...");
        HashMap<Integer, List<Result>> loadSheddedResults = new HashMap<>();
        HashMap<Integer, Result> resultsBeforeLoadShedding = Utils.copyGlobalResultsReportHashmap(this.standardResults);
        Iterator it = resultsBeforeLoadShedding.entrySet().iterator();
        float smoothLoad = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int currentLoadSheddingPercent = LS_PERCENT_STEP_SIZE;
            int timestamp = (int) pair.getKey();
            List<Result> errorsPerTimestamp = new ArrayList<>();
            while(currentLoadSheddingPercent <= MAX_LS_PERCENT){
                List<Soldier> beforeLS = this.data.get(timestamp);
                dropTuples(currentLoadSheddingPercent, beforeLS);
                //long begin = System.currentTimeMillis();
                long lastTime = System.nanoTime();
                long lastThreadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();

                double mean = getMean(beforeLS);
                double meanError = Math.abs(mean - this.standardResults.get(timestamp).getMean())/this.standardResults.get(timestamp).getMean();
                double standardDeviation = this.loadSheddingManager.getStandardDeviation(beforeLS, mean);
                double standardDeviationError = Math.abs(standardDeviation - this.standardResults.get(timestamp).getStandardDeviation())/this.standardResults.get(timestamp).getStandardDeviation();

//                long end = System.currentTimeMillis();
//                long dt = (end - begin);
                long time = System.nanoTime();
                long threadTime = CpuLoad.getInstance().getThreadMXBean().getCurrentThreadCpuTime();
                double dt = (threadTime - lastThreadTime) / (double)(time - lastTime);
                smoothLoad += (dt - smoothLoad) * 0.4;

                Result result = new Result();
                result.setMean(mean);
                result.setMeanError(meanError);
                result.setStandardDeviation(standardDeviation);
                result.setStddevError(standardDeviationError);
                result.setLsCalculationTime(smoothLoad);
                result.setLoadSheddingPercent(currentLoadSheddingPercent);
                errorsPerTimestamp.add(result);
                currentLoadSheddingPercent += LS_PERCENT_STEP_SIZE;
            }
            it.remove();
            loadSheddedResults.put(timestamp, errorsPerTimestamp);
        }
        this.loadSheddedResults = loadSheddedResults;
        return loadSheddedResults;
    }

    public HashMap<Integer, Result> getResultsPerPercent(){
        HashMap<Integer, Result> result = new HashMap<>();
        int currentPercent = LS_PERCENT_STEP_SIZE;
        while(currentPercent <= MAX_LS_PERCENT) {
            double meanSum = 0;
            double meanErrorSum = 0;
            double stdDevSum = 0;
            double stdDevErrorSum = 0;
            int values = 0;
            double timeConsumed = 0;
            for (Map.Entry<Integer, List<Result>> entry : this.loadSheddedResults.entrySet()) {
                meanSum += entry.getValue().get(currentPercent/10 - 1).getMean();
                meanErrorSum += entry.getValue().get(currentPercent/10 - 1).getMeanError();
                stdDevSum += entry.getValue().get(currentPercent/10 - 1).getStandardDeviation();
                stdDevErrorSum += entry.getValue().get(currentPercent/10 - 1).getStddevError();
                timeConsumed += entry.getValue().get(currentPercent/10 - 1).getLsCalculationTime();
                values++;
            }
            double mean = meanSum / values;
            double meanError = meanErrorSum / values;
            double stdDev = stdDevSum / values;
            double stdDevError = stdDevErrorSum / values;
            Result globalResult = new Result();
            globalResult.setMean(mean);
            globalResult.setMeanError(meanError);
            globalResult.setStandardDeviation(stdDev);
            globalResult.setStddevError(stdDevError);
            globalResult.setLsCalculationTime(timeConsumed);
            globalResult.setLoadSheddingPercent(currentPercent);
            result.put(currentPercent, globalResult);
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

    public LoadShedderType getLoadShedderType(){
        return this.loadShedderType;
    }
}
