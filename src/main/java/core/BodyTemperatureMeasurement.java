package core;

public class BodyTemperatureMeasurement extends Measurement<Double> {

    private double importance;

    public double getImportance() {
        return importance;
    }

    public void setImportance(double bodyTemperature) {

        if(bodyTemperature < 33 || bodyTemperature > 36){
            this.importance = 0.6;
        }else{
            this.importance = 0.3;
        }
    }
}
