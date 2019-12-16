package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ViewEventHandler {

    private boolean isLoginStageShowing = false;
    private Stage loginStage = new Stage();
    private Stage indexStage = new Stage();
    public static Stage primarystage;

    @FXML
    private void handleRegisterAction() throws Exception {
        if (!isLoginStageShowing) {
            Parent root = FXMLLoader.load(getClass().getResource("gui/register.fxml"));
            loginStage.setTitle("Create Account");
            loginStage.setScene(new Scene(root, 350, 333));
            loginStage.show();
            loginStage.setOnCloseRequest(event -> {
                isLoginStageShowing = false;
            });
            isLoginStageShowing = true;
        } else {
            loginStage.setAlwaysOnTop(true);
            loginStage.setAlwaysOnTop(false);
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
