package services;

import core.LoadShedderType;
import loadshedders.LoadShedder;
import loadshedders.LoadShedderComparator;
import managers.LoadSheddersFactory;
import managers.LoadSheddingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadSheddingService {

    private List<Double> errors = new ArrayList<>();
    private HashMap<LoadShedderType, List<Double>> comparatorErrors;

    public LoadSheddingService() {
    }

    public List<Double> shedLoad(String inputFile, LoadShedderType loadShedderType){
        LoadShedder loadShedder;
        if(loadShedderType.equals(LoadShedderType.RANDOM)){
            loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(LoadShedderType.RANDOM, inputFile);
            errors = loadShedder.shedLoad();
        }
        if(loadShedderType.equals(LoadShedderType.SEMANTIC)){
            loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(LoadShedderType.SEMANTIC, inputFile);
            errors = loadShedder.shedLoad();
        }
        return this.errors;
    }

    public HashMap<LoadShedderType, List<Double>> compareLoadShedders(String inputFile){
        return LoadShedderComparator.getInstance().compareLoadShedders(inputFile);
    }

}
