package utils;

import core.Soldier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
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
