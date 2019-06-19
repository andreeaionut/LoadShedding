package controllers;

import core.Computation;
import core.GlobalResult;
import core.LoadShedderType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import services.LoadSheddingService;

public class SuggestedSettingsController {

    private Computation computationType;
    private LoadShedderType loadShedderType;
    private LoadSheddingService loadSheddingService;

    @FXML
    private Label lblOptimalPercent;
    @FXML
    private Label lblMean;
    @FXML
    private Label lblStddev;
    @FXML
    private Label lblTime;

    @FXML
    private RadioButton rbtnMean;
    @FXML
    private RadioButton rbtnStddev;
    @FXML
    private RadioButton rbtnTime;
    @FXML
    private RadioButton rbtnBestRatio;
    @FXML
    private TextField txtMaximumMeanError;
    @FXML
    private TextField txtMaximumStddevError;


    @FXML
    private Label lblErrorMessage;

    public void initView(Computation computationType, LoadShedderType loadShedderType, LoadSheddingService loadSheddingService) {
        this.computationType = computationType;
        this.loadShedderType = loadShedderType;
        this.loadSheddingService = loadSheddingService;
        this.txtMaximumMeanError.textProperty().addListener((observable, oldValue, newValue) -> {
            double doubleMeanValue;
            double doubleStddevValue = 0;
            GlobalResult suggestedSettings;
            try{
                doubleMeanValue = Double.valueOf(newValue);
            }catch(NumberFormatException e){
                doubleMeanValue = -1;
            }
            try{
                doubleStddevValue = Double.valueOf(this.txtMaximumStddevError.getText());
            }catch(NumberFormatException e){
                doubleStddevValue = -1;
            }
            finally {
                suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "timeConsumed", doubleMeanValue, doubleStddevValue);
                this.completeResults(suggestedSettings);
            }
        });
        this.txtMaximumStddevError.textProperty().addListener((observable, oldValue, newValue) -> {
            double doubleMeanValue = 0;
            double doubleStddevValue;
            GlobalResult suggestedSettings;
            try{
                doubleStddevValue = Double.valueOf(newValue);
            }catch(NumberFormatException e){
                doubleStddevValue = -1;
            }
            try{
                doubleMeanValue = Double.valueOf(this.txtMaximumMeanError.getText());
            }catch(NumberFormatException e){
                doubleMeanValue = -1;
            }
            finally {
                suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "timeConsumed", doubleMeanValue, doubleStddevValue);
                this.completeResults(suggestedSettings);
            }
        });
    }

    public void handleMean(ActionEvent actionEvent) {
        this.lblErrorMessage.setVisible(false);
        this.txtMaximumMeanError.setDisable(true);
        this.txtMaximumStddevError.setDisable(true);
        this.rbtnStddev.setSelected(false);
        this.rbtnTime.setSelected(false);
        this.rbtnBestRatio.setSelected(false);
        GlobalResult suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "mean", -1, -1);
        this.completeResults(suggestedSettings);
    }

    public void handleTime(ActionEvent actionEvent) {
        this.lblErrorMessage.setVisible(false);
        this.txtMaximumMeanError.setDisable(true);
        this.txtMaximumStddevError.setDisable(true);
        this.rbtnStddev.setSelected(false);
        this.rbtnMean.setSelected(false);
        this.rbtnBestRatio.setSelected(false);
        GlobalResult suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "timeConsumed", -1, -1);
        this.completeResults(suggestedSettings);
    }

    public void handleStddev(ActionEvent actionEvent) {
        this.lblErrorMessage.setVisible(false);
        this.txtMaximumMeanError.setDisable(true);
        this.txtMaximumStddevError.setDisable(true);
        this.rbtnMean.setSelected(false);
        this.rbtnTime.setSelected(false);
        this.rbtnBestRatio.setSelected(false);
        GlobalResult suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "stddev", -1, -1);
        this.completeResults(suggestedSettings);
    }

    public void handleBestRatio(ActionEvent actionEvent) {
        this.lblErrorMessage.setVisible(false);
        this.txtMaximumMeanError.setDisable(false);
        this.txtMaximumStddevError.setDisable(false);
        this.rbtnMean.setSelected(false);
        this.rbtnTime.setSelected(false);
        this.rbtnStddev.setSelected(false);
        GlobalResult suggestedSettings = this.loadSheddingService.getSuggestedSettings(this.computationType, this.loadShedderType, "bestRatio", -1, -1);
        this.completeResults(suggestedSettings);
    }
    private void completeResults(GlobalResult suggestedSettings) {
        if(suggestedSettings == null){
            this.unsetLabelsText();
            this.lblErrorMessage.setVisible(true);
            return;
        }
        this.lblErrorMessage.setVisible(false);
        this.lblOptimalPercent.setText(String.valueOf(suggestedSettings.getLoadSheddingPercent()) + "%");
        this.lblMean.setText(String.valueOf(suggestedSettings.getMean()));
        this.lblStddev.setText(String.valueOf(suggestedSettings.getStandardDeviation()));
        this.lblTime.setText(String.valueOf(suggestedSettings.getLsCalculationTime()));
    }

    private void unsetLabelsText() {
        this.lblOptimalPercent.setText("");
        this.lblTime.setText("");
        this.lblStddev.setText("");
        this.lblMean.setText("");
    }
}
