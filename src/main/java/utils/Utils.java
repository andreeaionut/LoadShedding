package utils;

import core.*;

import java.util.*;

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
        for (Map.Entry<Integer, GlobalResult> entry : original.entrySet()) {
            GlobalResult globalResult = new GlobalResult();
            globalResult.setStandardDeviation(entry.getValue().getStandardDeviation());
            globalResult.setMean(entry.getValue().getMean());
            globalResult.setLsCalculationTime(entry.getValue().getLsCalculationTime());
            globalResult.setStddevError(entry.getValue().getStddevError());
            globalResult.setMeanError(entry.getValue().getMeanError());
            globalResult.setStandardCalculationTime(entry.getValue().getStandardCalculationTime());
            globalResult.setLoadSheddingPercent(entry.getValue().getLoadSheddingPercent());
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

    public static Map<Integer, GlobalResult> sortByComparator(Map<Integer, GlobalResult> unsortedMap, String valueType, final boolean order) {
        List<Map.Entry<Integer, GlobalResult>> list = new LinkedList<Map.Entry<Integer, GlobalResult>>(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, GlobalResult>>() {
            public int compare(Map.Entry<Integer, GlobalResult> o1,
                               Map.Entry<Integer, GlobalResult> o2) {
                if (order) {
                    return Double.compare(o1.getValue().getValue(valueType), o2.getValue().getValue(valueType));
                }
                else {
                    return Double.compare(o2.getValue().getValue(valueType), o1.getValue().getValue(valueType));
                }
            }
        });
        Map<Integer, GlobalResult> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, GlobalResult> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

}
