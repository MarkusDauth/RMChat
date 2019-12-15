package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

public class ViewEventHandler {

    private boolean isShowing = false;
    private Stage stage = new Stage();

    @FXML
    private void handleRegisterAction() throws Exception {
        if(!isShowing) {
            Parent root = FXMLLoader.load(getClass().getResource("login/register.fxml"));
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root, 350, 333));
            stage.show();
            stage.setOnCloseRequest( event -> {isShowing = false;} );
            isShowing = true;
        }
        else{
            stage.setAlwaysOnTop(true);
            stage.setAlwaysOnTop(false);
        }

    }
}
