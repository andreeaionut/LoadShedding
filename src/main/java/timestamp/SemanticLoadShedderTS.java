package timestamp;

import core.*;

import java.util.List;
import java.util.Random;

public class SemanticLoadShedderTS extends LoadShedderTS {
    public SemanticLoadShedderTS(String inputFile) {
        super(inputFile);
        this.loadShedderType = LoadShedderType.SEMANTIC;
    }

    private int getSemanticIndex(List<Soldier> soldiers){
        int semanticIndex = 0;
        for(Soldier soldier : soldiers){
            if(soldier.getBodyTemperature() >= 33 && soldier.getBodyTemperature() <= 36){
                return semanticIndex;
            }
        }
        int size = soldiers.size();
        Random random = new Random();
        semanticIndex = random.nextInt(size);
        return semanticIndex;
    }

    @Override
    public void dropTuples(int dropPercent, List<Soldier> soldiers) {
        int valuesDropped = Math.round((soldiers.size() * dropPercent) / 100);
        for(int counter = 0; counter < valuesDropped; counter++){
            int semanticIndex = this.getSemanticIndex(soldiers);
            soldiers.remove(semanticIndex);
        }
    }
}
