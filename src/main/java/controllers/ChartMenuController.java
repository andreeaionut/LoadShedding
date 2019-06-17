package controllers;

import core.GlobalResult;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import services.LoadSheddingService;

import java.io.IOException;
import java.util.HashMap;

public class ChartMenuController {

    private LoadShedderType loadShedderType;
    private HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors;
    private HashMap<Integer, GlobalResult> globalErrors;
    private LoadSheddingFinalResult loadSheddingFinalResult;

    private LoadSheddingService loadSheddingService;

    private boolean comparator;
    private String chartType;

    public ChartMenuController(){
    }

    public void handleAbsoluteError(){
        this.chartType = "mean";
        this.showChart();
    }

    public void handleStandardDeviation(){
        this.chartType = "stddev";
        this.showChart();
    }

    public void handleTimeConsumed(){
        this.chartType = "timeConsumed";
        this.showChart();
    }

    public void handleAll(){
        this.chartType = "all";
        this.showChart();
    }

    public void initView(boolean comparator, LoadShedderType loadShedderType, LoadSheddingService loadSheddingService, LoadSheddingFinalResult loadSheddingFinalResult) {
        this.comparator = comparator;
        this.loadShedderType = loadShedderType;
        this.loadSheddingService = loadSheddingService;
        this.loadSheddingFinalResult = loadSheddingFinalResult;
    }

    private void showChart(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../ChartView.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        ChartController controller = loader.getController();
        controller.setStage(stage);
        if(this.comparator){
            controller.initComparatorView(this.chartType, this.loadSheddingService.getComparatorErrors());
        }else{
            if(this.chartType.equals("all")){
                controller.initAllChartsView(loadShedderType, this.loadSheddingFinalResult);
            }else{
                if(this.chartType.equals("mean")){
                    controller.initView(chartType, loadShedderType, this.loadSheddingFinalResult);
                }else{
                    if(this.chartType.equals("timeConsumed")){
                        controller.initTimeConsumedView(loadSheddingFinalResult);
                    }else{
                        controller.initView(chartType, loadShedderType, loadSheddingFinalResult);
                    }
                }
            }
        }
    }
}
