package view;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.LoginData;
import properties.Properties;

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
        if(isUsernameAndPasswordCorrect()) {
            LoginData loginData = new LoginData();
            loginData.setUsername(usernameField.getText());
            loginData.setPassword(passwordField.getText());
            controller.loginUser(loginData);
        }
        else{
            views.showMessage("InvalidUsernameOrPassword");
        }
    }
    private boolean isUsernameAndPasswordCorrect(){
        return checkUsername() && checkPassword();
    }

    private boolean checkPassword(){
        return usernameField.getText().length() >= Properties.getInt("username.minLength") &&
                usernameField.getText().length() < Properties.getInt("username.maxLength");
    }
    private boolean checkUsername(){
        return passwordField.getText().length() >= Properties.getInt("password.minLength") &&
                passwordField.getText().length() < Properties.getInt("password.maxLength");
    }
}
