package global;

import core.BodyTemperatureMeasurement;
import core.LoadShedderType;
import core.Measurement;
import core.SoldierStatusReport;

import java.util.*;

public class RandomGlobalLoadShedder extends GlobalLoadShedder {

    public RandomGlobalLoadShedder(String inputFile, int computationFieldNumber) {
        super(inputFile, computationFieldNumber);
        this.loadShedderType = LoadShedderType.RANDOM;
    }

    @Override
     protected void dropTuples(int valuesDropped, SoldierStatusReport soldierStatusReport){
        int size;
        Random random = new Random();
        for(int counter = 0; counter < valuesDropped; counter++){
            size = soldierStatusReport.getNumberOfValues();
            int randomIndex = random.nextInt(size);
            Measurement<Double> bodyTemperatureMeasurement = (BodyTemperatureMeasurement) soldierStatusReport.getMeasurements().remove(randomIndex);
            soldierStatusReport.setNumberOfValues(soldierStatusReport.getNumberOfValues() - 1);
            soldierStatusReport.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature() - bodyTemperatureMeasurement.getValue());
            this.globalLoadSheddingManager.setStatusReportResults(soldierStatusReport);
        }
    }

}
