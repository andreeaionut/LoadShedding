package managers;

import core.Soldier;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileManager {

    public static String[] getFileHeader(String filename){
        File file = new File(filename);
        BufferedReader br;
        String text = null;
        try {
            br = new BufferedReader(new FileReader(file));
            text = br.readLine();
            System.out.println(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.split(",");

    }

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
                soldier.setTimestamp(Integer.valueOf(splittedLine[0]));
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

    public static List<Soldier> getDataFromFile(String inputFile, int computationFieldNumber) {
        File file = new File(inputFile);
        List<Soldier> soldiers = new ArrayList<Soldier>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line  = br.readLine();
            while ((line = br.readLine()) != null){
                String[] splittedLine = line.split(",");
                Soldier soldier = new Soldier();
                soldier.setTimestamp(Integer.valueOf(splittedLine[0]));
                soldier.setId(Integer.valueOf(splittedLine[1]));
                soldier.setBodyTemperature(Double.parseDouble(splittedLine[computationFieldNumber]));
                soldiers.add(soldier);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soldiers;
    }
}
