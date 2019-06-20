package core;

public class GlobalResult {
    private double mean;
    private double standardDeviation;

    private double meanError;
    private double stddevError;

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
        switch(valueType){
            case "mean":
                return mean;
            case "stddev":
                return standardDeviation;
            case "meanError":
                return meanError;
            case "stddevError":
                return stddevError;
            case "timeConsumed":
                return lsCalculationTime;
            case "stdTimeConsumed":
                return standardCalculationTime;
            case "bestRatio":
                return 2*this.lsCalculationTime / (this.stddevError + this.meanError);
        }
        return 0;
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

    public double getMeanError() {
        return meanError;
    }

    public void setMeanError(double meanError) {
        this.meanError = meanError;
    }

    public double getStddevError() {
        return stddevError;
    }

    public void setStddevError(double stddevError) {
        this.stddevError = stddevError;
    }

}
