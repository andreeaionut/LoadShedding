package managers;

import core.LoadShedderType;
import loadshedders.LoadShedder;
import loadshedders.RandomLoadShedder;
import loadshedders.SemanticLoadShedder;

public class LoadSheddersFactory {

    private static LoadSheddersFactory instance;

    private LoadSheddersFactory() {
    }

    public static LoadSheddersFactory getInstance(){
        if(instance == null){
            instance = new LoadSheddersFactory();
        }
        return instance;
    }

    public LoadShedder getLoadShedder(LoadShedderType type, String inputFile){
        if(type.equals(LoadShedderType.RANDOM)){
            return new RandomLoadShedder(inputFile);
        }
        if(type.equals(LoadShedderType.SEMANTIC)){
            return new SemanticLoadShedder(inputFile);
        }
        return null;
    }
}
