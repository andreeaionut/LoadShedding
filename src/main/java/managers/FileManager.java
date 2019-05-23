package managers;

import core.Soldier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<Soldier> getDataFromFile(String filename){
        File file = new File(filename);
        List<Soldier> soldiers = new ArrayList<Soldier>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                String[] splittedLine = line.split(",");
                Soldier soldier = new Soldier();
                soldier.setId(Integer.valueOf(splittedLine[1]));
                soldier.setBodyTemperature(Double.valueOf(splittedLine[2]));
                soldier.setSystolicBloodPressure(Integer.valueOf(splittedLine[3]));
                soldier.setDiastolicBloodPressure(Integer.valueOf(splittedLine[4]));
                soldier.setPulseRate(Integer.valueOf(splittedLine[4]));
                soldiers.add(soldier);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soldiers;
    }
}
