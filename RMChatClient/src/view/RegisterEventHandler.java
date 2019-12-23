package view;

import controller.NetworkController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.NewUser;

public class RegisterEventHandler {


    private NetworkController networkController;

    void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;

    @FXML
    private void handleRegisterButton(){
        NewUser newUser = new NewUser();
        newUser.setUsername(usernameField.getText());
        newUser.setPassword(passwordField.getText());
        networkController.registerNewUser(newUser);
    }
}
