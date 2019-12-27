package view;

import controller.network.NetworkController;
import properties.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.NewUser;

public class RegisterEventHandler {


    private NetworkController networkController;
    private Views views;

    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
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
            networkController.registerNewUser(newUser);
        }
        else {
            views.showMessage("InvalidUsernameOrPassword");
        }

    }
}
