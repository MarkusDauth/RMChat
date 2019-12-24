package view;

import controller.NetworkController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class IndexEventHandler {

    private NetworkController networkController;
    private Stage loginStage;
    private FXMLLoader indexFXMLLoader;
    private Stage indexStage = null;

    @FXML
    Label userNameLabel;

    @FXML
    Label statusLabel;


    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }
    public void setLoginStage(Stage loginStage){
        this.loginStage = loginStage;
    }
    public void setIndexStage(FXMLLoader indexFXMLLoader){
        this.indexFXMLLoader = indexFXMLLoader;
    }

    @FXML
    private void handleIndexAction() throws Exception {
        loginStage.close();
        Parent root = indexFXMLLoader.load();
        indexStage.setTitle("Index");
        indexStage.setScene(new Scene(root, 650, 400));
        indexStage.show();
    }
    void setStatusLabelText(String text){
        statusLabel.setText(text);
    }
}
