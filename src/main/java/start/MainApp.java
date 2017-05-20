package start;

import DAO.HibernateUtil;
import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.Locale", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader();
        //fxml for main
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setResources(resourceBundle);
        Parent rootMain = fxmlLoader.load();
        //controller for editDialog
        MainController mainController = fxmlLoader.getController();
        //primaryStage to editDialog
        mainController.setMainStage(primaryStage);
        //setting and start primaryStage
        primaryStage.setTitle(fxmlLoader.getResources().getString("main_window"));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(400);
        primaryStage.setScene(new Scene(rootMain, 400, 600));
        primaryStage.show();
    }
    @Override
    public void stop(){
        try {
            System.out.println("end.");
            HibernateUtil.shutdown();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
