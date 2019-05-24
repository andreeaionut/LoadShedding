package services;

import core.Computation;
import core.LoadShedderType;
import loadshedders.LoadShedder;
import loadshedders.LoadShedderComparator;
import managers.LoadSheddersFactory;
import managers.TimestampLoadSheddersFactory;
import timestamp.LoadShedderTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadSheddingService {

    private List<Double> globalErrors = new ArrayList<>();
    private HashMap<Integer, List<Double>> timestampErrors = new HashMap<>();
    private HashMap<Integer, Double> resultsPerPercent = new HashMap<>();
    private HashMap<LoadShedderType, List<Double>> comparatorErrors;

    public LoadSheddingService() {
    }

    public void shedLoad(String inputFile, LoadShedderType loadShedderType, Computation computation){
        if(computation.equals(Computation.GLOBAL)){
            LoadShedder loadShedder = LoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
            globalErrors = loadShedder.shedLoad();
        }else{
            LoadShedderTS loadShedderTS = TimestampLoadSheddersFactory.getInstance().getLoadShedder(loadShedderType, inputFile);
            this.timestampErrors = loadShedderTS.shedLoad();
            this.resultsPerPercent = loadShedderTS.getResultsPerPercent();
        }
    }

    public HashMap<LoadShedderType, List<Double>> compareLoadShedders(String inputFile){
        return LoadShedderComparator.getInstance().compareLoadShedders(inputFile);
    }

    public HashMap<Integer, List<Double>> getTimestampErrors() {
        return timestampErrors;
    }

    public List<Double> getGlobalErrors() {
        return globalErrors;
    }

    public HashMap<Integer, Double> getResultsPerPercent() {
        return resultsPerPercent;
    }
}
