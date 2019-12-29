package view;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.LoginData;

import java.io.IOException;


public class LoginEventHandler {

    private Controller controller;
    private Views views;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    void setController(Controller controller) {
        this.controller = controller;
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
        controller.loginUser(loginData);
    }
}
