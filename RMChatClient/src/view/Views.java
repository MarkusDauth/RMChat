package view;

import controller.Controller;
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
import model.FriendRequest;
import model.Message;
import properties.UINotifications;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Diese Klasse dient zur weiterleitung von Anfragen des controllers auf den GUI-Thread.
 */
public class Views extends Application {

    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;
    private static Controller controller;

    private Stage loginStage;
    private final Stage registerStage = new Stage();
    private final Stage chatStage = new Stage();
    private boolean registerStageIsInitialized = false;
    private boolean chatStageIsInitialized = false;

    private LoginEventHandler loginEventHandler;
    private RegisterEventHandler registerEventHandler;
    private ChatEventHandler chatEventHandler;

    public static void launchApplication(Controller controller) {
        Views.controller = controller;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage = primaryStage;

        //MVC Setup
        controller.setViews(this);

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

    private void showChatUIifNotShowing() throws IOException {
        if(!chatStageIsInitialized) {
            loginStage.close();
            initChatUI();
            chatStageIsInitialized = true;
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
        loginEventHandler.setController(controller);
        loginEventHandler.setViews(this);
    }

    private void initRegisterUI() throws IOException {
        FXMLLoader registerFxmlLoader = new FXMLLoader(getClass().getResource("gui/register.fxml"));
        Parent loginRoot = registerFxmlLoader.load();
        registerStage.setTitle("RM-CHAT");
        registerStage.setScene(new Scene(loginRoot, Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE));
        loginStage.setResizable(false);
        registerEventHandler = registerFxmlLoader.getController();
        registerEventHandler.setController(controller);
        registerEventHandler.setViews(this);
    }

    private void initChatUI() throws IOException {
        FXMLLoader chatFxmlLoader = new FXMLLoader(getClass().getResource("gui/chat.fxml"));
        Parent chatRoot = chatFxmlLoader.load();
        chatStage.setTitle("RM-CHAT");
        chatStage.setScene(new Scene(chatRoot, Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE));
        chatStage.setOnCloseRequest(event -> {
            FileChatDatabase.getInstance().save();
            System.exit(0);
        });
        chatStage.setResizable(false);
        chatEventHandler = chatFxmlLoader.getController();
        chatEventHandler.setController(controller);
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
        if(!msgKey.startsWith("OK")) {
            processErrorCode(msgKey, message);
        }
        else{
            showInfo(message);
        }

    }

    private void processErrorCode(String msgKey, String message) {
        if(message != null) {
            showError(message);
        }
        else{
            String errorMessage = "Unexpected Error "+msgKey;
            showError(errorMessage);
        }
    }

    public void showChatUI(){
        Platform.runLater(()->{
            try {
                String text = Controller.getUsername();
                showChatUIifNotShowing();
                chatEventHandler.userNameLabel.setText(text);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        });
    }
    public void setChatStatus(String statusKey){
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

    public FutureTask<FriendRequest> showFriendRequest(Friend friend) {
        final FutureTask query = new FutureTask(() -> chatEventHandler.showFriendRequest(friend));
        Platform.runLater(query);
        return query;
    }
    public void setReconnectText(int attempt, boolean wasConnectionSuccessful){
        Platform.runLater(() -> chatEventHandler.setReconnectLabelText(attempt,wasConnectionSuccessful));
    }
    public void refreshMessageList(String sender) {
        Platform.runLater(()->chatEventHandler.refreshMessageList(sender));
    }

    public void finishSendMessage() {
        Platform.runLater(()->chatEventHandler.finishSendMessage());
    }

    public void finishAddFriend() {
        Platform.runLater(()->chatEventHandler.finishAddFriend());
    }
}
