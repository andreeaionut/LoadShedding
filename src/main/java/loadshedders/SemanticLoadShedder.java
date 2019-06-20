package loadshedders;

import core.BodyTemperatureMeasurement;
import core.LoadShedderType;
import core.Measurement;
import core.SoldierStatusReport;

import java.util.HashMap;
import java.util.Random;

public class SemanticLoadShedder extends LoadShedder {

    public SemanticLoadShedder(String inputFile, int computationFieldNumber) {
        super(inputFile, computationFieldNumber);
        this.loadShedderType = LoadShedderType.SEMANTIC;
    }

    private int getSemanticIndex(SoldierStatusReport soldierStatusReport){
        int semanticIndex = 0;
        for(Measurement bodyTemperatureMeasurement : soldierStatusReport.getMeasurements()){
            if(bodyTemperatureMeasurement.getImportance() < 0.5){
                return semanticIndex;
            }
        }
        int size = soldierStatusReport.getMeasurements().size();
        Random random = new Random();
        semanticIndex = random.nextInt(size);
        return semanticIndex;
    }

    @Override
     protected void dropTuples(int dropPercent, SoldierStatusReport soldierStatusReport) {
        for(int counter = 0; counter < dropPercent; counter++){
            int randomIndex = this.getSemanticIndex(soldierStatusReport);
            Measurement<Double> bodyTemperatureMeasurement = (BodyTemperatureMeasurement) soldierStatusReport.getMeasurements().remove(randomIndex);
            soldierStatusReport.setNumberOfValues(soldierStatusReport.getNumberOfValues() - 1);
            soldierStatusReport.setSumOfBodyTemperature(soldierStatusReport.getSumOfBodyTemperature() - bodyTemperatureMeasurement.getValue());
            this.loadSheddingManager.setStatusReportResults(soldierStatusReport);
        }
    }
}
