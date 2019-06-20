package controllers;

import core.Computation;
import core.GlobalResult;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import services.LoadSheddingService;
import utils.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChartController {
    private Stage stage;

    private LineChart lineChart;
    private Button btnExport;
    private TextField txtTimestamp;
    private TextField txtVersus;

    private LoadSheddingService loadSheddingService;
    private LoadSheddingFinalResult loadSheddingFinalResult;
    private String chartType;

    public ChartController(){
        btnExport = new Button("Export to file");
        txtTimestamp  = new TextField();
        txtTimestamp.setPromptText("Timestamp");

        txtVersus = new TextField();
        txtVersus.setPromptText("Timestamp");


        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
                // TODO: probably use a file chooser here
                File file = new File("D:\\2018\\Licenta\\References\\img.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (IOException ex) {
                    // TODO: handle exception here
                }
            }
        });

        this.txtTimestamp.textProperty().addListener((observable, oldValue, newValue) -> {
            ChartController.this.initTimestampChart(newValue, ChartController.this.chartType);
        });

        this.txtVersus.textProperty().addListener((observable, oldValue, newValue) -> {
            ChartController.this.initStandardVersusLsView(chartType, loadSheddingFinalResult, loadSheddingService, newValue);
        });
    }

    public void setLoadSheddingFinalResult(LoadSheddingFinalResult loadSheddingFinalResult){
        this.loadSheddingFinalResult = loadSheddingFinalResult;
    }

    private void initTimestampChart(String timestampValue, String chartType) {
        int timestamp = 0;
        try{
            timestamp = Integer.valueOf(timestampValue);
        }catch (NumberFormatException e){

        }
        List<GlobalResult> resultsPerTimestamp = this.loadSheddingService.getLoadSheddedResultsPerGivenTimestamp(timestamp);
        if(resultsPerTimestamp == null){
            return;
        }
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Load Shedding Percent");
        NumberAxis yAxis = new NumberAxis   ();
        yAxis.setLabel("Mean error");
        lineChart = new LineChart(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();

        series.setName("Load shedder error evolution");
        if(this.chartType.equals("all")){
            XYChart.Series meanSeries = new XYChart.Series();
            XYChart.Series stddevSeries = new XYChart.Series();

            meanSeries.setName("Mean error");
            stddevSeries.setName("Standard deviation error");

            for(GlobalResult globalResult : resultsPerTimestamp){
                meanSeries.getData().add(new XYChart.Data(globalResult.getLoadSheddingPercent(), globalResult.getMeanError()));
                stddevSeries.getData().add(new XYChart.Data(globalResult.getLoadSheddingPercent(), globalResult.getStddevError()));
            }
            lineChart.getData().add(meanSeries);
            lineChart.getData().add(stddevSeries);
        }else{
            for(GlobalResult globalResult : resultsPerTimestamp){
                series.getData().add(new XYChart.Data(globalResult.getLoadSheddingPercent(), globalResult.getValue(chartType)));
            }
            lineChart.getData().add(series);
        }


        btnExport.setLayoutX(625);
        btnExport.setLayoutY(450);
        Group root = this.getRoot(Computation.TIMESTAMP);
        Scene scene = new Scene(root, 800, 500);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    public void initAllChartsView(Computation computationType, LoadShedderType loadShedderType, LoadSheddingFinalResult loadSheddedResult){
        this.chartType = "all";

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Load Shedding Percent");
        lineChart = new LineChart(xAxis, yAxis);
        lineChart.setTitle("Load Shedding Errors");

        List<XYChart.Series> series = new ArrayList<>();

        HashMap<Integer, GlobalResult> lsResults = Utils.copyGlobalResultsReportHashmap(loadSheddedResult.getLoadSheddedResults());
        Iterator it = lsResults.entrySet().iterator();
        XYChart.Series meanSeries = new XYChart.Series();
        XYChart.Series stddevSeries = new XYChart.Series();

        meanSeries.setName("Mean error");
        stddevSeries.setName("Standard deviation error");

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            GlobalResult globalResult = (GlobalResult) pair.getValue();
            meanSeries.getData().add(new XYChart.Data(pair.getKey(), globalResult.getMeanError()));
            stddevSeries.getData().add(new XYChart.Data(pair.getKey(), globalResult.getStddevError()));
            it.remove();
        }

        series.add(meanSeries);
        series.add(stddevSeries);

        for(XYChart.Series seriesToAdd : series){
            lineChart.getData().add(seriesToAdd);
        }

        btnExport.setLayoutX(625);
        btnExport.setLayoutY(450);
        Group root = this.getRoot(computationType);
        Scene scene = new Scene(root, 800, 500);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Global " + loadShedderType.toString() + " Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    private Group getRoot(Computation computationType){
        if(computationType.equals(Computation.TIMESTAMP)){
            txtTimestamp.setLayoutX(550);
            txtTimestamp.setLayoutY(370);
            return new Group(lineChart, btnExport, txtTimestamp);
        }else{
            return new Group(lineChart, btnExport);
        }
    }

    public void initComparatorView(Computation computationType, String chartType, HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors){
        this.chartType = chartType;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Load Shedding Percent");
        lineChart = new LineChart(xAxis, yAxis);
        lineChart.setTitle("Load Shedding " + chartType + " error");
        List<XYChart.Series> series = new ArrayList<>();
        Iterator it = Utils.copyHashMap(comparatorErrors).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            XYChart.Series newSeries = new XYChart.Series();
            newSeries.setName(pair.getKey().toString());
            LoadSheddingFinalResult loadSheddingFinalResult = (LoadSheddingFinalResult) pair.getValue();
            HashMap<Integer, GlobalResult> loadSheddedResults = loadSheddingFinalResult.getLoadSheddedResults();
            for(int percent = 10; percent<=90; percent+=10){
                newSeries.getData().add(new XYChart.Data(percent, loadSheddedResults.get(percent).getValue(chartType)));
            }
            series.add(newSeries);
            it.remove();
        }
        for(XYChart.Series seriesToAdd : series){
            lineChart.getData().add(seriesToAdd);
        }
        btnExport.setLayoutX(625);
        btnExport.setLayoutY(450);
        Group root = this.getRoot(computationType);
        Scene scene = new Scene(root, 800, 500);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Comparing Load Shedders");
        stage.setScene(scene);
        stage.show();
    }

    void initView(String chartType, Computation computationType, LoadShedderType loadShedderType, LoadSheddingFinalResult loadSheddingFinalResult){
        this.chartType = chartType;
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Load Shedding Percent");
        NumberAxis yAxis = new NumberAxis   ();
        yAxis.setLabel("Mean error");
        lineChart = new LineChart(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        HashMap<Integer, GlobalResult> lsResults = loadSheddingFinalResult.getLoadSheddedResults();
        series.setName(loadShedderType.toString() + " Load shedder error evolution");
        series.getData().add(new XYChart.Data(10, lsResults.get(10).getValue(chartType)));
        series.getData().add(new XYChart.Data(20, lsResults.get(20).getValue(chartType)));
        series.getData().add(new XYChart.Data(30, lsResults.get(30).getValue(chartType)));
        series.getData().add(new XYChart.Data(40, lsResults.get(40).getValue(chartType)));
        series.getData().add(new XYChart.Data(50, lsResults.get(50).getValue(chartType)));
        series.getData().add(new XYChart.Data(60, lsResults.get(60).getValue(chartType)));
        series.getData().add(new XYChart.Data(70, lsResults.get(70).getValue(chartType)));
        series.getData().add(new XYChart.Data(80, lsResults.get(80).getValue(chartType)));
        series.getData().add(new XYChart.Data(90, lsResults.get(90).getValue(chartType)));
        lineChart.getData().add(series);
        btnExport.setLayoutX(625);
        btnExport.setLayoutY(450);
        Group root = this.getRoot(computationType);
        Scene scene = new Scene(root, 800, 500);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Global " + loadShedderType.toString() + " Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    public void initTimeConsumedView(Computation computationType, LoadSheddingFinalResult loadSheddingFinalResult){
        this.chartType = "timeConsumed";
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Load Shedding Percent");
        lineChart = new LineChart(xAxis, yAxis);
        lineChart.setTitle("Time consumed");
        List<XYChart.Series> series = new ArrayList<>();

        HashMap<Integer, GlobalResult> loadSheddedResults = Utils.copyGlobalResultsReportHashmap(loadSheddingFinalResult.getLoadSheddedResults());
        HashMap<Integer, GlobalResult> standardResults = Utils.copyGlobalResultsReportHashmap(loadSheddingFinalResult.getStandardResults());

        Iterator it = loadSheddedResults.entrySet().iterator();
        XYChart.Series lsSeries = new XYChart.Series();
        lsSeries.setName("Load shedded TIME");

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Integer percent = (Integer) pair.getKey();
            GlobalResult globalResult = (GlobalResult) pair.getValue();
            lsSeries.getData().add(new XYChart.Data(percent, globalResult.getLsCalculationTime()));
            it.remove();
        }
        series.add(lsSeries);

        for(XYChart.Series seriesToAdd : series){
            lineChart.getData().add(seriesToAdd);
        }
        btnExport.setLayoutX(625);
        btnExport.setLayoutY(450);
        Group root = this.getRoot(computationType);
        Scene scene = new Scene(root, 800, 500);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Time Consumed Chart");
        stage.setScene(scene);
        stage.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoadSheddingService(LoadSheddingService loadSheddingService) {
        this.loadSheddingService = loadSheddingService;
    }

    public void initStandardVersusLsView(String chartType, LoadSheddingFinalResult loadSheddingFinalResult, LoadSheddingService loadSheddingService, String timestamp) {
        int intValueOfTimestamp = 0;
        try{
            intValueOfTimestamp = Integer.valueOf(timestamp);
        }catch (NumberFormatException e){

        }
        this.loadSheddingFinalResult = loadSheddingFinalResult;
        List<GlobalResult> lsResultsPerTimestamp = loadSheddingService.getLoadSheddedResultsPerGivenTimestamp(intValueOfTimestamp);
        GlobalResult standardResult = loadSheddingFinalResult.getStandardResults().get(intValueOfTimestamp);

        if(lsResultsPerTimestamp == null || standardResult == null){
            return;
        }

        this.chartType = chartType;
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis;
        if(this.chartType.equals("mean")){
            yAxis = new NumberAxis(30.0, 38.0, 0.01);
        }else{
            if(this.chartType.equals("stddev")){
                yAxis = new NumberAxis(0.0, 10.0, 0.01);
            }else{
                yAxis = new NumberAxis();
            }
        }

        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Standard versus Load shedded results");
        xAxis.setLabel("Load shedding percent");
        yAxis.setLabel("Relative error");

        XYChart.Series stdSeries = new XYChart.Series();
        stdSeries.setName("Standard "  + chartType + " value");
        XYChart.Series lsSeries = new XYChart.Series();
        lsSeries.setName("Load shedded " + chartType + " value");

        for(int percent = 10; percent <= 90; percent += 10){
            if(chartType.equals("timeConsumed")){
                stdSeries.getData().add(new XYChart.Data(String.valueOf(percent), standardResult.getValue("stdTimeConsumed")));
            }else{
                stdSeries.getData().add(new XYChart.Data(String.valueOf(percent), standardResult.getValue(chartType)));
            }
            lsSeries.getData().add(new XYChart.Data(String.valueOf(percent), lsResultsPerTimestamp.get((percent-10)/10).getValue(chartType)));
        }

//        btnExport.setLayoutX(625);
//        btnExport.setLayoutY(450);

//        txtVersus.setLayoutX(625);
//        txtVersus.setLayoutY(450);

        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(stdSeries, lsSeries);
        stage.setTitle("Standard versus Load shedded results");
        stage.setScene(scene);
        stage.show();

    }
}
