package core;

import java.util.ArrayList;
import java.util.List;

public class SoldierStatusReport implements RecordableSoldier {

    private int id;
    private String type;
    private double sumOfBodyTemperature;
    private int numberOfValues;
    private double mean;
    private double standardDeviation;
    private int timestamp;

    private List<Measurement> measurements;

    public SoldierStatusReport() {
        this.measurements = new ArrayList<Measurement>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSumOfBodyTemperature() {
        return sumOfBodyTemperature;
    }

    public void setSumOfBodyTemperature(double sumOfBodyTemperature) {
        this.sumOfBodyTemperature = sumOfBodyTemperature;
    }

    public int getNumberOfValues() {
        return numberOfValues;
    }

    public void setNumberOfValues(int numberOfValues) {
        this.numberOfValues = numberOfValues;
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

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public void addMeasurement(BodyTemperatureMeasurement bodyTemperatureMeasurement){
        this.measurements.add(bodyTemperatureMeasurement);
    }

    @Override
    public int getRecordableValue() {
        return timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
