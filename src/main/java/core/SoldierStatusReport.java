package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoldierStatusReport implements RecordableSoldier {

    private int id;
    private String type;
    private double sumOfBodyTemperature;
    private int numberOfValues;
    private double mean;
    private double standardDeviation;
    private int timestamp;

    private double lsTime;
    private double standardCalculationTime;

    private List<Measurement> measurements;

    public SoldierStatusReport() {
        this.measurements = new ArrayList<Measurement>();
    }

    public static SoldierStatusReport copy(SoldierStatusReport soldierStatusReport) {
        SoldierStatusReport soldierStatusReportCopy = new SoldierStatusReport();
        soldierStatusReportCopy.setId(soldierStatusReport.id);
        soldierStatusReportCopy.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature());
        soldierStatusReportCopy.setNumberOfValues(soldierStatusReport.getNumberOfValues());
        soldierStatusReportCopy.setStandardDeviation(soldierStatusReport.standardDeviation);
        soldierStatusReportCopy.setMean(soldierStatusReport.getMean());
        soldierStatusReportCopy.setMeasurements(new ArrayList<>(soldierStatusReport.getMeasurements()));
        soldierStatusReportCopy.setTimestamp(soldierStatusReport.getTimestamp());
        return soldierStatusReportCopy;
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
        double sum = 0;
        for (Measurement measurement : this.measurements){
            double value = (double) measurement.getValue();
            sum += value;
        }
        return sum / this.measurements.size();
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        double mean = this.getMean();
        double globalStandardDeviation = 0;
        for(Measurement measurement : this.measurements) {
            double value = (double) measurement.getValue();
            globalStandardDeviation += Math.pow(Math.abs(value - mean), 2);
        }
        this.standardDeviation = Math.sqrt(globalStandardDeviation / this.measurements.size());
        return this.standardDeviation;
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

    public double getStandardCalculationTime() {
        return standardCalculationTime;
    }

    public void setStandardCalculationTime(double standardCalculationTime) {
        this.standardCalculationTime = standardCalculationTime;
    }
}
