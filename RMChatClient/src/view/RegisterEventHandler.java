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
        if(usernameField.getText().length() >= Properties.getInt("username.minLength") &&
                usernameField.getText().length() < Properties.getInt("username.maxLength")&&
                passwordField.getText().length() >= Properties.getInt("password.minLength") &&
                passwordField.getText().length() < Properties.getInt("password.maxLength")
        ) {
            NewUser newUser = new NewUser();
            newUser.setUsername(usernameField.getText());
            newUser.setPassword(passwordField.getText());
            controller.registerNewUser(newUser);
        }
        else {
            views.showMessage("InvalidUsernameOrPassword");
        }

    }
}
