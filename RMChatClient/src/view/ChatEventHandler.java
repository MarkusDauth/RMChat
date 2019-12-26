package view;

import controller.NetworkController;
import controller.Properties;
import controller.chatDatabase.ChatDatabase;
import controller.chatDatabase.FileChatDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Friend;
import model.Message;

import java.util.List;


public class ChatEventHandler {

    private NetworkController networkController;
    private ChatDatabase fileChatDatabase = FileChatDatabase.getInstance();
    private Views views;
    private String shownChatOf = "none";

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
    @FXML
    TextField addFriendTextField;

    @FXML
    public void sendMessage(){

        if(messageTextArea.getText().length() < Properties.getInt("message.minlength")){
            return;
        }
        if(messageTextArea.getText().length() > Properties.getInt("message.maxlength")){
            views.showMessage("MessageTooLong");
            return;
        }
        Message message = new Message(userNameLabel.getText(), shownChatOf,messageTextArea.getText());
        networkController.sendMessage(message);
    }

    @FXML
    public void addFriend(){
        Friend friend = new Friend(addFriendTextField.getText());
        addFriendTextField.clear();
        networkController.addFriend(friend);
    }

    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }
    public TextArea getMessageTextArea(){
        return messageTextArea;
    }
    public void setViews(Views views) {
        this.views = views;
    }
    void setStatusLabelText(String text){
        statusLabel.setText(text);
    }
    public void initializeFriendList(){
        Label friend = new Label("monkas");
        friend.setOnMouseClicked(event -> {
            List<Message> friendMessages = fileChatDatabase.getMessages(friend.getText());
            shownChatOf = friend.getText();
            messageHistory.getItems().clear();
            for(Message message : friendMessages) {
                addItemToMessageHistory(message);
            }
        });
        friendList.getItems().add(friend);
    }

    public void addMessageToHistory(Message message) {
        if(shownChatOf.equals(message.getRecipient())){
            addItemToMessageHistory(message);
        }
    }

    private void addItemToMessageHistory(Message message) {
        String chatHistoryMessage = message.getSender() +":\t"+ message.getText();
        messageHistory.getItems().add(chatHistoryMessage);
    }
}
