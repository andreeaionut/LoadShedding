package controllers;

import core.LoadShedderType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

            if(this.tbtnChoose.selectedProperty().getValue() || this.tbtnCompare.selectedProperty().getValue()){
                this.btnGo.setDisable(false);
            }
        }
    }

    public void handleChooseClick(){
        this.btnChart.setDisable(true);
        this.tbtnChoose.setOpacity(1);
        this.tbtnChoose.setStyle("-fx-background-color: #008B8B;");
        this.tbtnCompare.selectedProperty().setValue(false);
        this.tbtnCompare.setOpacity(0.3);
        this.tbtnCompare.setStyle("-fx-background-color: #FFA07A;");
        this.rbtnRandomLS.setDisable(false);
        this.rbtnSemanticLS.setDisable(false);
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
        if(this.txtUploadedFile.getText() != null && this.txtUploadedFile.getText().compareTo("") != 0){
            this.btnGo.setDisable(false);
        }else{
            this.btnGo.setDisable(true);
        }
    }

    public void handleRadioButtonRandom(){
        this.rbtnSemanticLS.setSelected(false);
        if(this.txtUploadedFile.getText() != null && this.txtUploadedFile.getText().compareTo("") != 0){
            this.btnGo.setDisable(false);
        }
    }

    public void handleRadioButtonSemantic(){
        this.rbtnRandomLS.setSelected(false);
        if(this.txtUploadedFile.getText() != null && this.txtUploadedFile.getText().compareTo("") != 0){
            this.btnGo.setDisable(false);
        }
    }

    public void handleGo(){
        loadShedderType = null;
        this.inputFile = this.txtUploadedFile.getText();
        this.loadSheddingService = new LoadSheddingService();
        if(this.tbtnCompare.isSelected()){
            this.comparatorErrors = this.loadSheddingService.compareLoadShedders(this.inputFile);
            this.btnChart.setDisable(false);
        }else{
            if(this.rbtnRandomLS.isSelected()){
                loadShedderType = LoadShedderType.RANDOM;
                this.loadSheddingService.shedLoad(inputFile, loadShedderType);
                this.btnChart.setDisable(false);
            }else{
                if(this.rbtnSemanticLS.isSelected()){
                    loadShedderType = LoadShedderType.SEMANTIC;
                }
                this.loadSheddingService.shedLoad(inputFile, loadShedderType);
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
                controller.initView(this.comparatorErrors);
            }else{
                controller.initView(this.loadSheddingService.shedLoad(this.inputFile, this.loadShedderType));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
