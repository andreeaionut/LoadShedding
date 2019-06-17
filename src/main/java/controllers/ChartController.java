package controllers;

import core.GlobalResult;
import core.LoadShedderType;
import core.LoadSheddingFinalResult;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import utils.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChartController {
    private Stage stage;

    private LineChart lineChart;
    private Button btnExport;

    public ChartController(){
        btnExport = new Button("Export to file");

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
    }

    public void initAllChartsView(LoadShedderType loadShedderType, LoadSheddingFinalResult loadSheddedResult){
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
            meanSeries.getData().add(new XYChart.Data(pair.getKey(), globalResult.getMean()));
            stddevSeries.getData().add(new XYChart.Data(pair.getKey(), globalResult.getStandardDeviation()));
            it.remove();
        }

        series.add(meanSeries);
        series.add(stddevSeries);

        for(XYChart.Series seriesToAdd : series){
            lineChart.getData().add(seriesToAdd);
        }
        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        Group root = new Group(lineChart, btnExport);

        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Global " + loadShedderType.toString() + " Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    public void initComparatorView(String chartType, HashMap<LoadShedderType, LoadSheddingFinalResult> comparatorErrors){
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
        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        Group root = new Group(lineChart, btnExport);

        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Comparing Load Shedders");
        stage.setScene(scene);
        stage.show();
    }

    void initView(String chartType, LoadShedderType loadShedderType, LoadSheddingFinalResult loadSheddingFinalResult){
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
        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        Group root = new Group(lineChart, btnExport);

        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Global " + loadShedderType.toString() + " Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    void initTimestampView(String chartType, LoadShedderType loadShedderType, LoadSheddingFinalResult loadSheddedResult){
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Load Shedding Percent");

        NumberAxis yAxis = new NumberAxis   (0.0, 1.0, 0.1);
        yAxis.setLabel("Mean error");

        lineChart = new LineChart(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Load shedding error evolution");
        HashMap<Integer, GlobalResult> errors = loadSheddedResult.getLoadSheddedResults();
        for (Map.Entry<Integer, GlobalResult> entry : errors.entrySet()) {
            series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getValue(chartType)));
        }

        lineChart.getData().add(series);

        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        Group root = new Group(lineChart, btnExport);

        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        stage.setTitle("Timestamp " + loadShedderType.toString() + " Load Shedder");
        stage.setScene(scene);
        stage.show();
    }

    public void initTimeConsumedView(LoadSheddingFinalResult loadSheddingFinalResult){
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
        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        Group root = new Group(lineChart, btnExport);

        Scene scene = new Scene(root, 700, 400);
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

}
