package timestamp;

import core.*;

import java.util.List;
import java.util.Random;

public class RandomTimestampLoadShedder extends TimestampLoadShedder {
    public RandomTimestampLoadShedder(String inputFile, int computationFieldNumber) {
        super(inputFile, computationFieldNumber);
        this.loadShedderType = LoadShedderType.RANDOM;
    }

    @Override
    public void dropTuples(int dropPercent, List<Soldier> soldiers) {
        int valuesDropped = Math.round((soldiers.size() * dropPercent) / 100);
        int size;
        Random random = new Random();
        for(int counter = 0; counter < valuesDropped; counter++){
            size = soldiers.size();
            int randomIndex = random.nextInt(size);
            soldiers.remove(randomIndex);
        }
    }
}
