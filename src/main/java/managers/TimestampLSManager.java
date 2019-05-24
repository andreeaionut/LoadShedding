package managers;

import core.GlobalResult;
import core.Soldier;
import utils.Utils;

import java.util.*;

public class TimestampLSManager {

    private List<Soldier> data;
    private HashMap<Integer, List<Soldier>> results = new HashMap<>();

    public TimestampLSManager(String inputFile) {
        this.data = FileManager.getDataFromFile(inputFile);
        this.createTimestampHashMap();
    }

    private void createTimestampHashMap(){
        for(Soldier soldier : this.data){
            int timestamp = soldier.getTimestamp();
            if(results.containsKey(timestamp)){
                results.get(timestamp).add(soldier);
            }else{
                results.put(timestamp, new ArrayList<>());
                results.get(timestamp).add(soldier);
            }
        }
    }

    public HashMap<Integer, List<Soldier>> getData(){
        return Utils.copySoldiersHashmap(results);
    }

    public HashMap<Integer, GlobalResult> getStandardResults(){
        HashMap<Integer, GlobalResult> results = new HashMap<>();
        HashMap<Integer, List<Soldier>> resultsCopy = Utils.copySoldiersHashmap(this.results);
        Iterator it = resultsCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<Soldier> soldiersPerTimestamp = (List<Soldier>) pair.getValue();
            double sum = 0;
            int numberOfValues = 0;
            for(Soldier soldier : soldiersPerTimestamp){
                sum += soldier.getBodyTemperature();
                numberOfValues++;
            }
            GlobalResult globalResult = new GlobalResult();
            globalResult.setMean(sum/numberOfValues);
            results.put((Integer) pair.getKey(), globalResult);
            it.remove();
        }
        return results;
    }

}
