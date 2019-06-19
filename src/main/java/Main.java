import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene scene;
    private static Stage stage;

    public static void setScene(Scene scene) {
        Main.scene = scene;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        double width = 800;
        double height = 480;

        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        stage.setTitle("Load Shedding Investigation");
        controller.setStage(stage);

        scene = new Scene(root,width,height);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
