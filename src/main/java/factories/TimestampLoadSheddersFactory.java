package factories;

import core.LoadShedderType;
import timestamp.TimestampLoadShedder;
import timestamp.RandomTimestampLoadShedder;
import timestamp.SemanticTimestampLoadShedder;

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

    public TimestampLoadShedder getLoadShedder(LoadShedderType type, String inputFile, int computationFieldNumber){
        if(type.equals(LoadShedderType.RANDOM)){
            return new RandomTimestampLoadShedder(inputFile, computationFieldNumber);
        }
        if(type.equals(LoadShedderType.SEMANTIC)){
            return new SemanticTimestampLoadShedder(inputFile, computationFieldNumber);
        }
        return null;
    }
}
