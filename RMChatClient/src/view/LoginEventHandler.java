package view;

import controller.NetworkController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.LoginData;

import java.io.IOException;
import java.util.logging.Logger;


public class LoginEventHandler {

    private static Logger logger = Logger.getLogger("logger");


    private NetworkController networkController;
    private Views views;

    void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    void setViews(Views views) {
        this.views = views;
    }

    @FXML
    TextField usernameField;

    @FXML
    private TextField passwordField;

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
