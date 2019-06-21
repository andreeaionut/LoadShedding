package controllers;

import core.Computation;
import core.Result;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import services.LoadSheddingService;

import java.io.IOException;
import java.util.HashMap;

public class ChartMenuController {

    private LoadShedderType loadShedderType;
    private HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors;
    private HashMap<Integer, Result> globalErrors;
    private LoadSheddingFinalResult loadSheddingFinalResult;

    private LoadSheddingService loadSheddingService;

    private boolean comparator;
    private String chartType;
    private Computation computationType;

    @FXML
    private Button btnMeanError;
    @FXML
    private Button btnStddevError;
    @FXML
    private Button btnTimeConsumed;
    @FXML
    private Button btnAll;
    @FXML
    private CheckBox cBoxVersus;

    public ChartMenuController(){
    }

    public void handleMean(){
        if(this.cBoxVersus.isSelected()){
            this.chartType = "mean";
        }else{
            this.chartType = "meanError";
        }
        this.showChart();
    }

    public void handleStandardDeviation(){
        if(this.cBoxVersus.isSelected()){
            this.chartType = "stddev";
        }else{
            this.chartType = "stddevError";
        }
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

    public void initView(boolean comparator, Computation computationType, LoadShedderType loadShedderType, LoadSheddingService loadSheddingService, LoadSheddingFinalResult loadSheddingFinalResult) {
        this.comparator = comparator;
        this.loadShedderType = loadShedderType;
        this.loadSheddingService = loadSheddingService;
        this.loadSheddingFinalResult = loadSheddingFinalResult;
        this.computationType = computationType;
        cBoxVersus.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(cBoxVersus.isSelected()){
                    ChartMenuController.this.btnAll.setDisable(true);
                    ChartMenuController.this.btnTimeConsumed.setDisable(true);
                }else{
                    ChartMenuController.this.btnAll.setDisable(false);
                    ChartMenuController.this.btnTimeConsumed.setDisable(false);
                }
            }
        });
    }

    public void handleCbox(){

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
        controller.setLoadSheddingFinalResult(this.loadSheddingFinalResult);
        controller.setLoadSheddingService(this.loadSheddingService);
        controller.setStage(stage);
        if(this.cBoxVersus.isSelected()){
            controller.initStandardVersusLsView(this.chartType, this.loadSheddingFinalResult, loadSheddingService, "61");
            return;
        }
        if(this.comparator){
            controller.initComparatorView(computationType, this.chartType, this.loadSheddingService.getComparatorErrors());
        }else{
            if(this.chartType.equals("all")){
                controller.initAllChartsView(computationType, loadShedderType, this.loadSheddingFinalResult);
            }else{
                if(this.chartType.equals("meanError") || this.chartType.equals("stddevError")){
                    controller.initView(chartType, computationType, loadShedderType, this.loadSheddingFinalResult);
                }else{
                    if(this.chartType.equals("timeConsumed")){
                        controller.initTimeConsumedView(computationType, loadSheddingFinalResult);
                    }
                }
            }
        }
    }


}
