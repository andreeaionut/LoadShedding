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

    public static HashMap<Integer, Result> copyGlobalResultsReportHashmap(HashMap<Integer, Result> original){
        HashMap<Integer, Result> copy = new HashMap<>();
        for (Map.Entry<Integer, Result> entry : original.entrySet()) {
            Result result = new Result();
            result.setStandardDeviation(entry.getValue().getStandardDeviation());
            result.setMean(entry.getValue().getMean());
            result.setLsCalculationTime(entry.getValue().getLsCalculationTime());
            result.setStddevError(entry.getValue().getStddevError());
            result.setMeanError(entry.getValue().getMeanError());
            result.setStandardCalculationTime(entry.getValue().getStandardCalculationTime());
            result.setLoadSheddingPercent(entry.getValue().getLoadSheddingPercent());
            copy.put(entry.getKey(), result);
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

    public static Map<Integer, Result> sortByComparator(Map<Integer, Result> unsortedMap, String valueType, final boolean order) {
        List<Map.Entry<Integer, Result>> list = new LinkedList<Map.Entry<Integer, Result>>(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Result>>() {
            public int compare(Map.Entry<Integer, Result> o1,
                               Map.Entry<Integer, Result> o2) {
                if (order) {
                    return Double.compare(o1.getValue().getValue(valueType), o2.getValue().getValue(valueType));
                }
                else {
                    return Double.compare(o2.getValue().getValue(valueType), o1.getValue().getValue(valueType));
                }
            }
        });
        Map<Integer, Result> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Result> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

}
