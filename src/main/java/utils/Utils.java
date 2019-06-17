package utils;

import core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static HashMap<LoadShedderType, LoadSheddingFinalResult> copyHashMap(HashMap<LoadShedderType, LoadSheddingFinalResult> original){
        HashMap<LoadShedderType, LoadSheddingFinalResult> copy = new HashMap<>();
        for (Map.Entry<LoadShedderType, LoadSheddingFinalResult> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    LoadSheddingFinalResult.copy(entry.getValue()));
        }
        return copy;
    }

    public static HashMap<Integer, SoldierStatusReport> copySoldierStatusReportHashmap(HashMap<Integer, SoldierStatusReport> original){
        HashMap<Integer, SoldierStatusReport> copy = new HashMap<>();
        for (Map.Entry<Integer, SoldierStatusReport> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    SoldierStatusReport.copy(entry.getValue()));
        }
        return copy;
    }

    public static HashMap<Integer, GlobalResult> copyGlobalResultsReportHashmap(HashMap<Integer, GlobalResult> original){
        HashMap<Integer, GlobalResult> copy = new HashMap<>();
        for (Map.Entry<Integer, GlobalResult> entry : original.entrySet())
        {
            GlobalResult globalResult = new GlobalResult(entry.getValue().getMean(), entry.getValue().getStandardDeviation());
            globalResult.setLsCalculationTime(entry.getValue().getLsCalculationTime());
            globalResult.setStandardCalculationTime(entry.getValue().getStandardCalculationTime());
            copy.put(entry.getKey(), globalResult);
        }
        return copy;
    }

    public static HashMap<Integer, List<Soldier>> copySoldiersHashmap(
            HashMap<Integer, List<Soldier>> original)
    {
        HashMap<Integer, List<Soldier>> copy = new HashMap<Integer, List<Soldier>>();
        for (Map.Entry<Integer, List<Soldier>> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    new ArrayList<Soldier>(entry.getValue()));
        }
        return copy;
    }
}
