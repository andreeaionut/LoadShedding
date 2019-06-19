package core;

public class GlobalResult {
    private double mean;
    private double standardDeviation;

    private double lsCalculationTime;
    private double standardCalculationTime;
    private int loadSheddingPercent;

    public GlobalResult() {
    }

    public GlobalResult(double mean, double standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getValue(String valueType){
        if(valueType.equals("mean")){
            return mean;
        }
        if(valueType.equals("timeConsumed")){
            return lsCalculationTime;
        }
        if(valueType.equals("stddev")){
            return standardDeviation;
        }
        return 2*this.lsCalculationTime / (this.standardDeviation + this.mean);
    }

    public int getLoadSheddingPercent() {
        return loadSheddingPercent;
    }

    public void setLoadSheddingPercent(int loadSheddingPercent) {
        this.loadSheddingPercent = loadSheddingPercent;
    }

    public double getLsCalculationTime() {
        return lsCalculationTime;
    }

    public void setLsCalculationTime(double lsCalculationTime) {
        this.lsCalculationTime = lsCalculationTime;
    }

    public double getStandardCalculationTime() {
        return standardCalculationTime;
    }

    public void setStandardCalculationTime(double standardCalculationTime) {
        this.standardCalculationTime = standardCalculationTime;
    }
}
