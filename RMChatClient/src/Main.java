import controller.NetworkController;
import controller.Properties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.NewUser;
import view.ViewEventHandler;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends Application {
    private static NetworkController networkController;
    private static Logger logger;
    private static FileHandler logFileHandler;
    private static Properties properties;
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws Exception {

        fxmlLoader = new FXMLLoader(getClass().getResource("view/gui/login.fxml"));
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("RM-CHAT");
        primaryStage.setScene(new Scene(root, 350, 350));
        primaryStage.setOnCloseRequest( event -> {System.exit(0);} );
        primaryStage.show();

        //MVC Setup
        configureLogger();
        ViewEventHandler viewEventHandler =fxmlLoader.getController();
        viewEventHandler.setPrimarystage(primaryStage);
        ViewEventHandler.setLogger(logger);

        properties = new Properties();
        networkController = new NetworkController(logger, properties);

        networkController.setViewEventHandler(viewEventHandler);
        ViewEventHandler.setNetworkController(networkController);

    }

    public static void main(String[] args) {

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
        newUser.setUsername("Markus");
        newUser.setPassword("Test123");

        //NetworkController.getInstance().registerNewUser(newUser);
    }
}
