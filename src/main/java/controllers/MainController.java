package controllers;

import core.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import managers.FileManager;
import services.LoadSheddingService;
import sun.applet.Main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainController {

    private LoadSheddingService loadSheddingService;
    private LoadSheddingFinalResult loadSheddingFinalResult;
    private LoadShedderType loadShedderType;
    private Computation computationType;
    private String inputFile;
    private int computationFieldNumber;

    private Stage stage;
    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    private TextField txtUploadedFile;
    @FXML
    private Button btnUploadFile;
    @FXML
    private ToggleButton tbtnChoose;
    @FXML
    private ToggleButton tbtnCompare;
    @FXML
    private ToggleButton tbtnGlobalComputation;
    @FXML
    private ToggleButton tbtnTimestampComputation;
    @FXML
    private RadioButton rbtnRandomLS;
    @FXML
    private RadioButton rbtnSemanticLS;
    @FXML
    private Button btnGo;
    @FXML
    private Button btnChart;
    @FXML
    private Button btnSuggestedSettings;
    @FXML
    private HBox hboxFileHeader;

    public MainController() {
        this.loadSheddingService = new LoadSheddingService();
    }

    public void handleUploadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File fileChosen = fileChooser.showOpenDialog(stage);
        if(fileChosen != null){
            this.inputFile = fileChosen.getAbsolutePath();
            txtUploadedFile.setText(this.inputFile);
            String[] fileHeader = FileManager.getFileHeader(this.inputFile);
            ToggleGroup group = new ToggleGroup();
            int currentHeader = 0;
            for(String header : fileHeader){
                RadioButton button1 = new RadioButton(header);
                button1.setId(String.valueOf(currentHeader));
                button1.setToggleGroup(group);
                button1.setStyle("-fx-font-size: 0.35cm;");
                int finalCurrentHeader = currentHeader;
                button1.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                        if(button1.isSelected()){
                            MainController.this.computationFieldNumber = finalCurrentHeader;
                        }
                    }
                });
                this.hboxFileHeader.getChildren().add(button1);
                currentHeader++;
            }
            if(this.tbtnChoose.selectedProperty().getValue() || this.tbtnCompare.selectedProperty().getValue()){
                this.btnGo.setDisable(false);
            }
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleChooseClick(){
        this.tbtnChoose.selectedProperty().setValue(true);
        this.tbtnChoose.setStyle("-fx-opacity: 1");

        this.tbtnCompare.selectedProperty().setValue(false);
        this.tbtnCompare.setStyle("-fx-opacity: 0.5");

        this.rbtnRandomLS.setDisable(false);
        this.rbtnSemanticLS.setDisable(false);
        if(this.txtUploadedFile.getText() != null &&
                this.txtUploadedFile.getText().compareTo("") != 0 &&
                (this.tbtnGlobalComputation.isSelected() || this.tbtnTimestampComputation.isSelected()) &&
                (this.rbtnRandomLS.isSelected() || this.rbtnSemanticLS.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleCompareClick(){
        this.tbtnCompare.setSelected(true);
        this.tbtnCompare.setStyle("-fx-opacity: 1");

        this.tbtnChoose.setSelected(false);
        this.tbtnChoose.setStyle("-fx-opacity: 0.5");

        this.rbtnRandomLS.setSelected(false);
        this.rbtnSemanticLS.setSelected(false);
        this.rbtnRandomLS.setDisable(true);
        this.rbtnSemanticLS.setDisable(true);
        if((this.txtUploadedFile.getText() != null &&
                this.txtUploadedFile.getText().compareTo("") != 0) &&
                (this.tbtnGlobalComputation.isSelected() ||
                this.tbtnTimestampComputation.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleRadioButtonRandom(){
        this.rbtnSemanticLS.setSelected(false);
        if(this.txtUploadedFile.getText() != null &&
                this.txtUploadedFile.getText().compareTo("") != 0 &&
                (this.tbtnGlobalComputation.isSelected() ||
                this.tbtnTimestampComputation.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleRadioButtonSemantic(){
        this.rbtnRandomLS.setSelected(false);
        if(this.txtUploadedFile.getText() != null &&
                this.txtUploadedFile.getText().compareTo("") != 0 &&
                (this.tbtnGlobalComputation.isSelected() ||
                this.tbtnTimestampComputation.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleGo(){
        loadShedderType = null;
        computationType = null;
        this.inputFile = this.txtUploadedFile.getText();
        if(this.tbtnGlobalComputation.isSelected()){
            computationType = Computation.GLOBAL;
        }else{
            computationType = Computation.TIMESTAMP;
        }
        if(this.tbtnCompare.isSelected()){
            this.loadSheddingService.compareLoadShedders(computationType, this.inputFile);
            this.btnChart.setDisable(false);
        }else{
            if(this.rbtnRandomLS.isSelected()){
                loadShedderType = LoadShedderType.RANDOM;
                this.loadSheddingFinalResult = this.loadSheddingService.shedLoad(inputFile, loadShedderType, computationType);
            }else{
                if(this.rbtnSemanticLS.isSelected()){
                    loadShedderType = LoadShedderType.SEMANTIC;
                }
                this.loadSheddingFinalResult = this.loadSheddingService.shedLoad(inputFile, loadShedderType, computationType);
            }
            this.btnChart.setDisable(false);
            this.btnSuggestedSettings.setDisable(false);
        }
    }

    public void handleChart(){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../ChartMenuView.fxml"));
            root = loader.load();
            ChartMenuController controller = loader.getController();
            controller.initView(this.tbtnCompare.isSelected(), this.loadShedderType, this.loadSheddingService, this.loadSheddingFinalResult);
            Stage stage = new Stage();
            stage.setTitle("Chart Menu");
            stage.setScene(new Scene(root, 763, 163));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGlobalComputationClick(){
        this.tbtnGlobalComputation.selectedProperty().setValue(true);
        this.tbtnGlobalComputation.setStyle("-fx-opacity: 1");

        this.tbtnTimestampComputation.selectedProperty().setValue(false);
        this.tbtnTimestampComputation.setStyle("-fx-opacity: 0.5");

        if(txtUploadedFile.getText().compareTo("") != 0 &&
                ((tbtnChoose.isSelected() &&
                        (rbtnRandomLS.isSelected() ||
                                rbtnSemanticLS.isSelected())) ||
                        tbtnCompare.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleTimestampComputationClick(){
        this.tbtnTimestampComputation.selectedProperty().setValue(true);
        this.tbtnTimestampComputation.setStyle("-fx-opacity: 1");

        this.tbtnGlobalComputation.selectedProperty().setValue(false);
        this.tbtnGlobalComputation.setStyle("-fx-opacity: 0.5");

        if(txtUploadedFile.getText().compareTo("") != 0 &&
                ((tbtnChoose.isSelected() &&
                        (rbtnRandomLS.isSelected() ||
                        rbtnSemanticLS.isSelected())) ||
                tbtnCompare.isSelected())){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
        this.btnSuggestedSettings.setDisable(true);
        this.btnChart.setDisable(true);
    }

    public void handleSuggestedSettings(){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../SuggestedSettingsView.fxml"));
            root = loader.load();
            SuggestedSettingsController controller = loader.getController();
            controller.initView(this.computationType, this.loadShedderType, this.loadSheddingService);
            Stage stage = new Stage();
            stage.setTitle("Suggested Settings Menu");
            stage.setScene(new Scene(root, 450, 481));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
