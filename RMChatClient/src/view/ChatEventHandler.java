package view;

import controller.NetworkController;
import properties.Properties;
import controller.chatDatabase.ChatDatabase;
import controller.chatDatabase.FileChatDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Friend;
import model.Message;

import java.util.List;


public class ChatEventHandler {

    private NetworkController networkController;
    private ChatDatabase fileChatDatabase = FileChatDatabase.getInstance();
    private Views views;
    private String shownChatOf = "non";

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
        if(shownChatOf.length() < Properties.getInt("username.minLength")){
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

    @FXML
    public void handleMouseClick() {
        Label friendLabel = friendList.getSelectionModel().getSelectedItem();
        if(friendLabel == null){
            return;
        }
        String friendText = friendLabel.getText();
        List<Message> friendMessages = fileChatDatabase.getMessages(friendText);
        shownChatOf = friendText;
        messageHistory.getItems().clear();
        for(Message message : friendMessages) {
            addItemToMessageHistory(message);
        }
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

    public void addMessageToHistory(Message message) {
        if(shownChatOf.equals(message.getRecipient())){
            addItemToMessageHistory(message);
        }
    }

    private void addItemToMessageHistory(Message message) {
        String chatHistoryMessage = message.getSender() +":\t"+ message.getText();
        messageHistory.getItems().add(chatHistoryMessage);
    }

    public void refreshFriendList(List<Friend> friendList) {
        friendList.clear();
        for(Friend friend:friendList){
            Label label = new Label(friend.getUsername());
            this.friendList.getItems().add(label);
        }
    }
}
