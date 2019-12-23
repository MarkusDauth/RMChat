package view;

import controller.NetworkController;
import controller.Properties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Views extends Application {

    public static final String LANGUAGE = "EN.";
    private static Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    private NetworkController networkController;
    private Stage loginStage;
    private Stage registerStage = new Stage();
    private boolean registerStageIsInitialized = false;

    public static void launchApplication(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage = primaryStage;

        //MVC Setup
        networkController = new NetworkController();
        networkController.setViews(this);

        initLoginUI();
        showLoginUIifNotShowing();
    }

    private void showLoginUIifNotShowing(){
        if(loginStage.isShowing()) {
            loginStage.setAlwaysOnTop(true);
            loginStage.setAlwaysOnTop(false);
        }
        else {
            loginStage.show();
        }
    }

    void showRegisterUIifNotShowing() throws IOException {
        if(!registerStageIsInitialized) {
            initRegisterUI();
            registerStageIsInitialized = true;
        }
        if(registerStage.isShowing()) {
            registerStage.setAlwaysOnTop(true);
            registerStage.setAlwaysOnTop(false);
        }
        else {
            registerStage.show();
        }
    }

    private void initLoginUI() throws IOException {
        FXMLLoader loginFxmlLoader = new FXMLLoader(getClass().getResource("gui/login.fxml"));
        Parent loginRoot = loginFxmlLoader.load();
        loginStage.setTitle("RM-CHAT");
        loginStage.setScene(new Scene(loginRoot, 350, 350));
        loginStage.setOnCloseRequest( event -> System.exit(0));
        LoginEventHandler loginEventHandler =loginFxmlLoader.getController();
        loginEventHandler.setNetworkController(networkController);
        loginEventHandler.setViews(this);
    }

    private void initRegisterUI() throws IOException {
        FXMLLoader registerFxmlLoader = new FXMLLoader(getClass().getResource("gui/register.fxml"));
        Parent loginRoot = registerFxmlLoader.load();
        registerStage.setTitle("RM-CHAT");
        registerStage.setScene(new Scene(loginRoot, 350, 333));
        RegisterEventHandler registerEventHandler = registerFxmlLoader.getController();
        registerEventHandler.setNetworkController(networkController);
    }

    private static void configureLogger() {
        try {
            logFileHandler = new FileHandler("logs.log",true);
            logger.addHandler(logFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter(formatter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showError(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR,message, ButtonType.OK);
            alert.show();});
    }

    public void showInfo(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,message, ButtonType.OK);
            alert.show();});
    }

    public void showMessage(String msg) {
        String message = Properties.getString(LANGUAGE +msg);
        if(msg.startsWith("OK")) {
            showInfo(message);
        }
        else
            showError(message);
    }
}
