package view;

import controller.NetworkController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.LoginData;
import model.NewUser;


public class ViewEventHandler {

    private boolean isLoginStageShowing = false;
    private Stage registerStage = new Stage();
    private Stage indexStage = new Stage();
    public static Stage primarystage;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegisterAction() throws Exception {
        if (!isLoginStageShowing) {
            Parent root = FXMLLoader.load(getClass().getResource("gui/register.fxml"));
            registerStage.setTitle("Create Account");
            registerStage.setScene(new Scene(root, 350, 333));
            registerStage.show();
            registerStage.setOnCloseRequest(event -> {
                isLoginStageShowing = false;
            });
            isLoginStageShowing = true;
        } else {
            registerStage.setAlwaysOnTop(true);
            registerStage.setAlwaysOnTop(false);
        }
    }

    @FXML
    private void handleIndexAction() throws Exception {
        primarystage.close();
        Parent root = FXMLLoader.load(getClass().getResource("gui/index.fxml"));
        indexStage.setTitle("Index");
        indexStage.setScene(new Scene(root, 650, 400));
        indexStage.show();
    }

    @FXML
    private void handleRegisterButton() throws Exception{
        System.out.println("handleRegisterButton");
        NewUser newUser = new NewUser();
        newUser.setUsername(usernameField.getText());
        newUser.setPassword(passwordField.getText());
        NetworkController.getInstance().registerNewUser(newUser);
    }

    @FXML
    private void handleLoginButton() throws Exception{
        System.out.println("handleLoginButton");
        LoginData loginData = new LoginData();
        loginData.setUsername(username.getText());
        loginData.setPassword(password.getText());

        NetworkController.getInstance().loginUser(loginData);
    }

    public static void showError(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR,message, ButtonType.OK);
            alert.show();});
    }

    public static void showInfo(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,message, ButtonType.OK);
            alert.show();});
    }
}
