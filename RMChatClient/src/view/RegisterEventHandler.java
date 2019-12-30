package view;

import controller.Controller;
import properties.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.NewUser;

public class RegisterEventHandler {


    private Controller controller;
    private Views views;

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setViews(Views views) {
        this.views = views;
    }

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;

    @FXML
    private void handleRegisterButton(){
        if(isUsernameAndPasswordCorrect()) {
            NewUser newUser = new NewUser();
            newUser.setUsername(usernameField.getText());
            newUser.setPassword(passwordField.getText());
            controller.registerNewUser(newUser);
        }
        else {
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
