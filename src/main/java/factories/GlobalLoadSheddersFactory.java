package factories;

import core.LoadShedderType;
import global.GlobalLoadShedder;
import global.RandomGlobalLoadShedder;
import global.SemanticGlobalLoadShedder;

public class GlobalLoadSheddersFactory {

    private static GlobalLoadSheddersFactory instance;

    private GlobalLoadSheddersFactory() {
    }

    public static GlobalLoadSheddersFactory getInstance(){
        if(instance == null){
            instance = new GlobalLoadSheddersFactory();
        }
        return instance;
    }

    public GlobalLoadShedder getLoadShedder(LoadShedderType type, String inputFile, int computationFieldNumber){
        if(type.equals(LoadShedderType.RANDOM)){
            return new RandomGlobalLoadShedder(inputFile, computationFieldNumber);
        }
        if(type.equals(LoadShedderType.SEMANTIC)){
            return new SemanticGlobalLoadShedder(inputFile, computationFieldNumber);
        }
        return null;
    }
}
