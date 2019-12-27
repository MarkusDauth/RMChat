package view;

import controller.network.NetworkController;
import controller.database.chatDatabase.FileChatDatabase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import model.Friend;
import model.Message;
import properties.UINotifications;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Views extends Application {

    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;
    private static NetworkController networkController;

    private Stage loginStage;
    private final Stage registerStage = new Stage();
    private final Stage chatStage = new Stage();
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

    private void showIndexUIifNotShowing() throws IOException {
        if(!indexStageIsInitialized) {
            loginStage.close();
            initIndexUI();
            indexStageIsInitialized = true;
        }
        if(chatStage.isShowing()) {
            chatStage.setAlwaysOnTop(true);
            chatStage.setAlwaysOnTop(false);
        }
        else {
            chatStage.show();
        }
    }

    private void initLoginUI() throws IOException {
        FXMLLoader loginFxmlLoader = new FXMLLoader(getClass().getResource("gui/login.fxml"));
        Parent loginRoot = loginFxmlLoader.load();
        loginStage.setTitle("RM-CHAT Login");
        loginStage.setScene(new Scene(loginRoot, Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE));
        loginStage.setOnCloseRequest( event -> System.exit(0));
        loginStage.setResizable(false);
        loginEventHandler =loginFxmlLoader.getController();
        loginEventHandler.setNetworkController(networkController);
        loginEventHandler.setViews(this);
    }

    private void initRegisterUI() throws IOException {
        FXMLLoader registerFxmlLoader = new FXMLLoader(getClass().getResource("gui/register.fxml"));
        Parent loginRoot = registerFxmlLoader.load();
        registerStage.setTitle("RM-CHAT");
        registerStage.setScene(new Scene(loginRoot, Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE));
        loginStage.setResizable(false);
        registerEventHandler = registerFxmlLoader.getController();
        registerEventHandler.setNetworkController(networkController);
        registerEventHandler.setViews(this);
    }

    private void initIndexUI() throws IOException {
        FXMLLoader indexFxmlLoader = new FXMLLoader(getClass().getResource("gui/chat.fxml"));
        Parent indexRoot = indexFxmlLoader.load();
        chatStage.setTitle("RM-CHAT");
        chatStage.setScene(new Scene(indexRoot, Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE));
        chatStage.setOnCloseRequest(event -> {
            FileChatDatabase.getInstance().save();
            System.exit(0);
        });
        chatEventHandler = indexFxmlLoader.getController();
        chatEventHandler.setNetworkController(networkController);
        chatEventHandler.setViews(this);
    }

    public void refreshFriendList(List<Friend> friendList){
        Platform.runLater(() -> {
            chatEventHandler.refreshFriendList(friendList);});
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
        String message = UINotifications.getString(msgKey);
        if(msgKey.startsWith("OK")) {
            showInfo(message);
        }
        else{
            processErrorCode(msgKey, message);
        }

    }

    private void processErrorCode(String msgKey, String message) {
        if(message != null) {
            showError(message);
        }
        else{
            String errorMessage = "Specified Code has not been added to the notifications file: "+msgKey;
            showError(errorMessage);
        }
    }

    public void showChatUI(){
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
            String status = UINotifications.getString(statusKey);
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

    public void showFriendRequest(Friend friend) {
        Platform.runLater(() -> chatEventHandler.showFriendRequest(friend));
    }
}
