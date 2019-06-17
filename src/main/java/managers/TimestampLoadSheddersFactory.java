package managers;

import core.LoadShedderType;
import timestamp.LoadShedderTS;
import timestamp.RandomLoadShedderTS;
import timestamp.SemanticLoadShedderTS;

public class TimestampLoadSheddersFactory {
    private static TimestampLoadSheddersFactory instance;

    private TimestampLoadSheddersFactory() {
    }

    public static TimestampLoadSheddersFactory getInstance(){
        if(instance == null){
            instance = new TimestampLoadSheddersFactory();
        }
        return instance;
    }

    public LoadShedderTS getLoadShedder(LoadShedderType type, String inputFile){
        if(type.equals(LoadShedderType.RANDOM)){
            return new RandomLoadShedderTS(inputFile);
        }
        if(type.equals(LoadShedderType.SEMANTIC)){
            return new SemanticLoadShedderTS(inputFile);
        }
        return null;
    }
}
