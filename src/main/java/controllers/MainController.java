package controllers;

import core.Computation;
import core.LoadShedderType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.LoadSheddingService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainController {

    private LoadSheddingService loadSheddingService;
    private LoadShedderType loadShedderType;
    private Computation computationType;
    private String inputFile;
    private HashMap<LoadShedderType, List<Double>> comparatorErrors;
    
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
        ;
        if(fileChosen != null){
            String filePath = fileChosen.getAbsolutePath();
            txtUploadedFile.setText(filePath);
            this.inputFile = filePath;
            if(this.tbtnChoose.selectedProperty().getValue() || this.tbtnCompare.selectedProperty().getValue()){
                this.btnGo.setDisable(false);
            }
        }
    }

    public void handleChooseClick(){
        this.tbtnChoose.setOpacity(1);
        this.tbtnChoose.setStyle("-fx-background-color: #008B8B;");
        this.tbtnCompare.selectedProperty().setValue(false);
        this.tbtnCompare.setOpacity(0.3);
        this.tbtnCompare.setStyle("-fx-background-color: #FFA07A;");
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
    }

    public void handleCompareClick(){
        this.btnChart.setDisable(true);
        this.tbtnCompare.setOpacity(1);
        this.tbtnCompare.setStyle("-fx-background-color: #FF8C00;");
        this.tbtnChoose.selectedProperty().setValue(false);
        this.tbtnChoose.setOpacity(0.3);
        this.tbtnChoose.setStyle("-fx-background-color: #66CDAA;");
        this.rbtnRandomLS.setSelected(false);
        this.rbtnSemanticLS.setSelected(false);
        this.rbtnRandomLS.setDisable(true);
        this.rbtnSemanticLS.setDisable(true);
        if(this.txtUploadedFile.getText() != null &&
                this.txtUploadedFile.getText().compareTo("") != 0 &&
                this.tbtnGlobalComputation.isSelected() ||
                this.tbtnTimestampComputation.isSelected()){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
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
    }

    public void handleGo(){
        loadShedderType = null;
        computationType = null;
        this.inputFile = this.txtUploadedFile.getText();
        this.loadSheddingService = new LoadSheddingService();
        if(this.tbtnGlobalComputation.isSelected()){
            computationType = Computation.GLOBAL;
        }else{
            computationType = Computation.TIMESTAMP;
        }
        if(this.tbtnCompare.isSelected()){
            this.comparatorErrors = this.loadSheddingService.compareLoadShedders(this.inputFile);
            this.btnChart.setDisable(false);
        }else{
            if(this.rbtnRandomLS.isSelected()){
                loadShedderType = LoadShedderType.RANDOM;
                this.loadSheddingService.shedLoad(inputFile, loadShedderType, computationType);
                this.btnChart.setDisable(false);
            }else{
                if(this.rbtnSemanticLS.isSelected()){
                    loadShedderType = LoadShedderType.SEMANTIC;
                }
                this.loadSheddingService.shedLoad(inputFile, loadShedderType, computationType);
            }
            this.btnChart.setDisable(false);
        }
    }

    public void handleChart(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../ChartView.fxml"));
            loader.load();

            Stage stage = new Stage();

            ChartController controller = loader.getController();

            controller.setStage(stage);
            if(this.tbtnCompare.isSelected()){
                controller.initComparatorView(this.comparatorErrors);
            }else{
                this.loadSheddingService.shedLoad(this.inputFile, this.loadShedderType, this.computationType);
                if(this.computationType.equals(Computation.GLOBAL)){
                    controller.initView(this.loadSheddingService.getGlobalErrors());
                }else{
                    controller.initTimestampView(this.loadSheddingService.getResultsPerPercent());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGlobalComputationClick(){
        this.tbtnGlobalComputation.setOpacity(1);
        this.tbtnGlobalComputation.setStyle("-fx-background-color: #48D1CC;");

        this.tbtnTimestampComputation.setOpacity(0.3);
        this.tbtnTimestampComputation.setStyle("-fx-background-color: #FFB6C1;");
        this.tbtnTimestampComputation.selectedProperty().setValue(false);

        if(txtUploadedFile.getText().compareTo("") != 0 &&
                tbtnChoose.isSelected() ||
                rbtnRandomLS.isSelected() ||
                rbtnSemanticLS.isSelected()){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
    }

    public void handleTimestampComputationClick(){
        this.tbtnTimestampComputation.setOpacity(1);
        this.tbtnTimestampComputation.setStyle("-fx-background-color: #DB7093;");

        this.tbtnGlobalComputation.setOpacity(0.3);
        this.tbtnGlobalComputation.setStyle("-fx-background-color: #ADD8E6;");
        this.tbtnGlobalComputation.selectedProperty().setValue(false);

        if(txtUploadedFile.getText().compareTo("") != 0 &&
                tbtnChoose.isSelected() ||
                rbtnRandomLS.isSelected() ||
                rbtnSemanticLS.isSelected()){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
