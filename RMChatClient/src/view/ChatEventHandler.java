package view;

import controller.network.NetworkController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import model.FriendRequest;
import properties.Properties;
import controller.database.chatDatabase.ChatDatabase;
import controller.database.chatDatabase.FileChatDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Friend;
import model.Message;
import properties.UINotifications;

import java.util.List;


public class ChatEventHandler {

    private NetworkController networkController;
    private final ChatDatabase fileChatDatabase = FileChatDatabase.getInstance();
    private List<Friend> friendListData;
    private Friend selectedFriend = null;

    @FXML
    Label userNameLabel;
    @FXML
    Label statusLabel;
    @FXML
    ListView<Friend> friendListView;
    @FXML
    ListView<String> messageHistory;
    @FXML
    TextArea messageTextArea;
    @FXML
    TextField addFriendTextField;

    @FXML
    public void sendMessage(){
        if(selectedFriend.getUsername().length() < Properties.getInt("message.minLength")){
            return;
        }if(selectedFriend.getUsername().length() > Properties.getInt("message.maxLength")){
            //TODO errormessage
            return;
        }
        Message message = new Message(userNameLabel.getText(), selectedFriend.getUsername(),messageTextArea.getText());
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
        Friend selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if(selectedFriend == null){
            return;
        }
        List<Message> friendMessages = fileChatDatabase.getMessages(selectedFriend.getUsername());
        this.selectedFriend = selectedFriend;
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
    }
    public void setStatusLabelText(String text){
        if(text.equals("Online")){
            statusLabel.setTextFill(Color.GREEN);
        }
        else{
            statusLabel.setTextFill(Color.RED);
        }
        statusLabel.setText(text);
    }

    public void addMessageToHistory(Message message) {
        if(selectedFriend.equals(message.getRecipient())){
            addItemToMessageHistory(message);
            handleMouseClick();
            //TODO experimental
            FileChatDatabase.getInstance().save();
        }
    }

    private void addItemToMessageHistory(Message message) {
        String chatHistoryMessage = message.getSender() +":\t"+ message.getText();
        messageHistory.getItems().add(chatHistoryMessage);
    }

    public void refreshFriendList(List<Friend> friendList) {
        //this.friendListView.getItems().clear();
        this.friendListData = friendList;

        ObservableList<Friend> friendObservableList = FXCollections.observableList(friendList);
        friendListView.getItems().setAll(friendObservableList);
    }

    public FriendRequest showFriendRequest(Friend friend) {
        String addFriendText = getFriendRequestText(friend.getUsername());
        Alert friendRequestAlert = createFriendRequestAlert(addFriendText);

        friendRequestAlert.showAndWait();
        ButtonBar.ButtonData clickedButton = friendRequestAlert.getResult().getButtonData();
        boolean acceptedFriendRequest = didUserAcceptFriendRequest(clickedButton);
        FriendRequest friendRequest = new FriendRequest(acceptedFriendRequest,friend.getUsername());
        return friendRequest;
    }

    private Alert createFriendRequestAlert(String addFriendText) {
        Alert friendRequestAlert = new Alert(Alert.AlertType.CONFIRMATION);
        friendRequestAlert.setContentText(addFriendText);
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        friendRequestAlert.getButtonTypes().setAll(okButton, noButton);
        return friendRequestAlert;
    }

    private String getFriendRequestText(String sender) {
        String requestNotification = UINotifications.getString("FriendRequest");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sender);
        stringBuilder.append(" ");
        stringBuilder.append(requestNotification);
        return stringBuilder.toString();
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean didUserAcceptFriendRequest(ButtonBar.ButtonData result) {
        if(result == ButtonBar.ButtonData.YES){
            return true;
        }
        if(result == ButtonBar.ButtonData.NO){
            return false;
        }
        return false;
    }
}
