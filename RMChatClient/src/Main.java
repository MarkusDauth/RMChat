import controller.NetworkController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.NewUser;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends Application {
    static NetworkController networkController;

    static Logger logger;
    static FileHandler logFileHandler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        //TODO: Ein Properties Objekt hier erstellen, aber NetworkController muss ein Konstruktor ohne Parameter für die View haben

        configureLogger();

        //MVC Setup
        networkController = new NetworkController();

        //TODO: Remove Tests
        testNetwork();

        //Launch GUI
        launch(args);

        //Close Logger file
        logFileHandler.close();
    }

    private static void configureLogger() {
        logger = Logger.getLogger("logger");

        try {
            logFileHandler = new FileHandler("logs.log",true);
            logger.addHandler(logFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter(formatter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testNetwork() {
        NewUser newUser = new NewUser();
        newUser.setUserName("Markus");
        newUser.setPassword("Test123");

        networkController.registerNewUser(newUser);
    }
}
