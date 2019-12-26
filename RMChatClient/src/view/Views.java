package view;

import controller.NetworkController;
import controller.Properties;
import controller.chatDatabase.FileChatDatabase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Message;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Views extends Application {

    private static Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;
    private static NetworkController networkController;

    private Stage loginStage;
    private Stage registerStage = new Stage();
    private Stage indexStage = new Stage();
    private boolean registerStageIsInitialized = false;
    private boolean indexStageIsInitialized = false;

    private LoginEventHandler loginEventHandler;
    private RegisterEventHandler registerEventHandler;
    private ChatEventHandler chatEventHandler;

    public static void launchApplication(NetworkController networkController) {
        Views.networkController = networkController;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage = primaryStage;

        //MVC Setup
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

    void showIndexUIifNotShowing() throws IOException {
        if(!indexStageIsInitialized) {
            loginStage.close();
            initIndexUI();
            chatEventHandler.initializeFriendList();
            indexStageIsInitialized = true;
        }
        if(indexStage.isShowing()) {
            indexStage.setAlwaysOnTop(true);
            indexStage.setAlwaysOnTop(false);
        }
        else {
            indexStage.show();
        }
    }

    private void initLoginUI() throws IOException {
        FXMLLoader loginFxmlLoader = new FXMLLoader(getClass().getResource("gui/login.fxml"));
        Parent loginRoot = loginFxmlLoader.load();
        loginStage.setTitle("RM-CHAT");
        loginStage.setScene(new Scene(loginRoot, 350, 350));
        loginStage.setOnCloseRequest( event -> System.exit(0));
        loginEventHandler =loginFxmlLoader.getController();
        loginEventHandler.setNetworkController(networkController);
        loginEventHandler.setViews(this);
    }

    private void initRegisterUI() throws IOException {
        FXMLLoader registerFxmlLoader = new FXMLLoader(getClass().getResource("gui/register.fxml"));
        Parent loginRoot = registerFxmlLoader.load();
        registerStage.setTitle("RM-CHAT");
        registerStage.setScene(new Scene(loginRoot, 350, 333));
        registerEventHandler = registerFxmlLoader.getController();
        registerEventHandler.setNetworkController(networkController);
        registerEventHandler.setViews(this);
    }

    private void initIndexUI() throws IOException {
        FXMLLoader indexFxmlLoader = new FXMLLoader(getClass().getResource("gui/chat.fxml"));
        Parent indexRoot = indexFxmlLoader.load();
        indexStage.setTitle("RM-CHAT");
        indexStage.setScene(new Scene(indexRoot, 660, 500));
        indexStage.setOnCloseRequest( event -> {
            FileChatDatabase.getInstance().save();
            System.exit(0);
        });
        chatEventHandler = indexFxmlLoader.getController();
        chatEventHandler.setNetworkController(networkController);
        chatEventHandler.setViews(this);
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

    private void showError(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR,message, ButtonType.OK);
            alert.show();});
    }

    private void showInfo(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,message, ButtonType.OK);
            alert.show();});
    }

    public void showMessage(String msgKey) {
        String LANGUAGE = Properties.getString("LANGUAGE");
        String message = UINotifications.getString(LANGUAGE +msgKey);
        if(msgKey.startsWith("OK")) {
            showInfo(message);
        }
        else
            showError(message);
    }

    public void showIndexUI(){
        Platform.runLater(()->{
            try {
                String text = NetworkController.getUsername();
                showIndexUIifNotShowing();
                chatEventHandler.userNameLabel.setText(text);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        });
    }
    public void setIndexStatus(String statusKey){
        Platform.runLater(() ->{
            String LANGUAGE = Properties.getString("LANGUAGE");
            String status = UINotifications.getString(LANGUAGE+statusKey);
            chatEventHandler.setStatusLabelText(status);
        });
    }

    public void addMessageToHistory(Message message) {
        Platform.runLater(() ->
                chatEventHandler.addMessageToHistory(message));
    }

    public void finishSend() {
        chatEventHandler.getMessageTextArea().clear();
    }
}
