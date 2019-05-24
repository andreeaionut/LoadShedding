package controllers;

import core.LoadShedderType;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

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

    public void initComparatorView(HashMap<LoadShedderType, List<Double>> comparatorErrors){
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Load Shedding Percent");

        lineChart = new LineChart(xAxis, yAxis);

        lineChart.setTitle("Load Shedding Mean Error");

        List<XYChart.Series> series = new ArrayList<>();

        Iterator it = comparatorErrors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            XYChart.Series newSeries = new XYChart.Series();
            newSeries.setName(pair.getKey().toString());
            List<Double> errors = (List<Double>) pair.getValue();
            for(int percent = 10; percent<=90; percent+=10){
                newSeries.getData().add(new XYChart.Data(percent, errors.get((percent/10)-1)));
            }
            series.add(newSeries);
            it.remove();
        }

        for(XYChart.Series seriesToAdd : series){
            lineChart.getData().add(seriesToAdd);
        }

        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        //Creating a Group object
        Group root = new Group(lineChart, btnExport);

        //Creating a scene object
        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        //Setting title to the Stage
        stage.setTitle("Line Chart");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

    }

    public void initView(List<Double> errors){
        //Defining the x axis
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Load Shedding Percent");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis   (0.0, 1.0, 0.1);
        yAxis.setLabel("Mean error");

        //Creating the line chart
        lineChart = new LineChart(xAxis, yAxis);

        //Prepare XYChart.Series objects by setting data
        XYChart.Series series = new XYChart.Series();
        series.setName("Load shedding error evolution");

        series.getData().add(new XYChart.Data(10, errors.get(0)));
        series.getData().add(new XYChart.Data(20, errors.get(1)));
        series.getData().add(new XYChart.Data(30, errors.get(2)));
        series.getData().add(new XYChart.Data(40, errors.get(3)));
        series.getData().add(new XYChart.Data(50, errors.get(4)));
        series.getData().add(new XYChart.Data(60, errors.get(5)));
        series.getData().add(new XYChart.Data(70, errors.get(6)));
        series.getData().add(new XYChart.Data(80, errors.get(7)));
        series.getData().add(new XYChart.Data(90, errors.get(8)));

        //Setting the data to Line chart
        lineChart.getData().add(series);

        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        //Creating a Group object
        Group root = new Group(lineChart, btnExport);

        //Creating a scene object
        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        //Setting title to the Stage
        stage.setTitle("Line Chart");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public void initTimestampView(HashMap<Integer, Double> errors){
        //Defining the x axis
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Load Shedding Percent");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis   (0.0, 1.0, 0.1);
        yAxis.setLabel("Mean error");

        //Creating the line chart
        lineChart = new LineChart(xAxis, yAxis);

        //Prepare XYChart.Series objects by setting data
        XYChart.Series series = new XYChart.Series();
        series.setName("Load shedding error evolution");

        for (Map.Entry<Integer, Double> entry : errors.entrySet()) {
            series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }

        //Setting the data to Line chart
        lineChart.getData().add(series);

        btnExport.setLayoutX(550);
        btnExport.setLayoutY(350);
        //Creating a Group object
        Group root = new Group(lineChart, btnExport);

        //Creating a scene object
        Scene scene = new Scene(root, 700, 400);
        String css = ChartController.class.getResource("../style.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        //Setting title to the Stage
        stage.setTitle("Line Chart");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
