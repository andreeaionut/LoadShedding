package services;

import core.Computation;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import services.LoadSheddingService;

import java.util.HashMap;

public class LoadShedderComparator {

    private HashMap<LoadShedderType, LoadSheddingFinalResult> errors = new HashMap<>();
    private LoadSheddingService loadSheddingService;

    public LoadShedderComparator(LoadSheddingService loadSheddingService) {
        this.loadSheddingService = loadSheddingService;
    }

    public HashMap<LoadShedderType, LoadSheddingFinalResult> compareLoadShedders(Computation computationType, String inputFile, int computationFieldNumber){
        this.errors.clear();
        for (LoadShedderType lsType : LoadShedderType.values()) {
            if(computationType.equals(Computation.GLOBAL)) {
                if(this.loadSheddingService.getLoadSheddingGlobalFinalResult() == null){
                    loadSheddingService.shedLoad(inputFile, lsType, computationType, computationFieldNumber);
                }
                this.errors.put(lsType, this.loadSheddingService.getLoadSheddingGlobalFinalResult());
            }else{
                this.errors.put(lsType, loadSheddingService.shedLoad(inputFile, lsType, computationType, computationFieldNumber));
            }
        }
        return this.errors;
    }
}
