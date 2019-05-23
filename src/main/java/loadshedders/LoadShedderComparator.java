package loadshedders;

import core.LoadShedderType;
import managers.LoadSheddersFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadShedderComparator {

    private HashMap<LoadShedderType, List<Double>> errors = new HashMap<>();

    private static LoadShedderComparator instance;

    public static LoadShedderComparator getInstance(){
        if(instance == null){
            instance = new LoadShedderComparator();
        }
        return instance;
    }

    private LoadShedderComparator() {
    }

    public HashMap<LoadShedderType, List<Double>> compareLoadShedders(String inputFile){
        List<LoadShedder> loadShedders = new ArrayList<>();
        for (LoadShedderType lsType : LoadShedderType.values()) {
            loadShedders.add(LoadSheddersFactory.getInstance().getLoadShedder(lsType, inputFile));
        }
        for(LoadShedder loadShedder : loadShedders){
            this.errors.put(loadShedder.getLoadShedderType(), loadShedder.shedLoad());
        }
        return this.errors;
    }
}
