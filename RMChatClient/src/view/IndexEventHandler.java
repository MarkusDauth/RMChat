package view;

import controller.NetworkController;
import controller.chatDatabase.FileChatDatabase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Message;

import java.util.List;


public class IndexEventHandler {

    private NetworkController networkController;
    private Stage loginStage;
    private FXMLLoader indexFXMLLoader;
    private Stage indexStage = new Stage();
    FileChatDatabase fileChatDatabase;
    private String shownChatof = "none";

    @FXML
    Label userNameLabel;
    @FXML
    Label statusLabel;
    @FXML
    ListView<Label> friendList;
    @FXML
    ListView<String> messageHistory;
    @FXML
    TextArea messageTextArea;

    public IndexEventHandler() {
        this.fileChatDatabase = new FileChatDatabase();
        fileChatDatabase.load();
    }

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
        indexStage.setScene(new Scene(root, 800, 500));
        indexStage.show();
    }
    @FXML
    public void saveMessage(String sender, String text){
        Message message = new Message(userNameLabel.getText(),sender,text);
        fileChatDatabase.addMessage(message);
        if(shownChatof.equals(sender)){
            Platform.runLater(() -> messageHistory.getItems().add(message.getSender() +": "+ message.getText()));
        }
    }

    void setStatusLabelText(String text){
        statusLabel.setText(text);
    }
    public void initializeFriendList(){
        Label friend = new Label("monkas");
        friend.setOnMouseClicked(event -> {
            List<Message> friendMessages = fileChatDatabase.getMessages(friend.getText());
            shownChatof = friend.getText();
            messageHistory.getItems().clear();
            for(Message message : friendMessages) {
                messageHistory.getItems().add(message.getSender() +": "+ message.getText());
            }
        });
        friendList.getItems().add(friend);
    }
    public void sendMessage(){
        Message message = new Message(userNameLabel.getText(),shownChatof,messageTextArea.getText());
        networkController.sendMessage(message);
    }
}
