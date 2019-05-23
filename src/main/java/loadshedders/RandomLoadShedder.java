package loadshedders;

import core.BodyTemperatureMeasurement;
import core.LoadShedderType;
import core.Measurement;
import core.SoldierStatusReport;

import java.util.*;

public class RandomLoadShedder extends LoadShedder {

    public RandomLoadShedder(String inputFile) {
        super(inputFile);
        this.loadShedderType = LoadShedderType.RANDOM;
    }

    void dropTuples(int dropPercent, SoldierStatusReport soldierStatusReport){
        int size;
        Random random = new Random();
        for(int counter = 0; counter < dropPercent; counter++){
            size = soldierStatusReport.getNumberOfValues();
            int randomIndex = random.nextInt(size);
            Measurement<Double> bodyTemperatureMeasurement = (BodyTemperatureMeasurement) soldierStatusReport.getMeasurements().remove(randomIndex);
            soldierStatusReport.setNumberOfValues(soldierStatusReport.getNumberOfValues() - 1);
            soldierStatusReport.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature() - bodyTemperatureMeasurement.getValue());
            this.loadSheddingManager.setStatusReportResults(soldierStatusReport);
        }
    }

}
