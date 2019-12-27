package view;

import controller.network.NetworkController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.LoginData;

import java.io.IOException;


public class LoginEventHandler {

    private NetworkController networkController;
    private Views views;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    void setViews(Views views) {
        this.views = views;
    }

    @FXML
    private void handleRegisterAction() throws IOException {
        views.showRegisterUIifNotShowing();
    }

    @FXML
    private void handleLoginButton() {
        LoginData loginData = new LoginData();
        loginData.setUsername(usernameField.getText());
        loginData.setPassword(passwordField.getText());
        networkController.loginUser(loginData);
    }
}
