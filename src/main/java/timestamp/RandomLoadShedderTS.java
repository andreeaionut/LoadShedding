package timestamp;

import core.*;
import loadshedders.LoadShedder;

import java.util.List;
import java.util.Random;

public class RandomLoadShedderTS extends LoadShedderTS {
    public RandomLoadShedderTS(String inputFile) {
        super(inputFile);
        this.loadShedderType = LoadShedderType.RANDOM;
    }

    @Override
    public void dropTuples(int dropPercent, List<Soldier> soldiers) {
        int valuesDropped = Math.round(soldiers.size() * dropPercent / 100);
        int size;
        Random random = new Random();
        for(int counter = 0; counter < valuesDropped; counter++){
            size = soldiers.size();
            int randomIndex = random.nextInt(size);
            soldiers.remove(randomIndex);
        }
    }
}
