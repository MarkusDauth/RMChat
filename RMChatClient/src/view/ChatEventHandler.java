package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import model.FriendRequest;
import properties.Properties;
import controller.database.chatDatabase.ChatDatabaseInterface;
import controller.database.chatDatabase.FileChatDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Friend;
import model.Message;
import properties.UINotifications;

import java.util.List;


public class ChatEventHandler {

    private Controller controller;
    private final ChatDatabaseInterface fileChatDatabase = FileChatDatabase.getInstance();
    private List<Friend> friendListData;
    private Friend selectedFriend = null;
    private Views views;

    @FXML
    Label userNameLabel;
    @FXML
    Label statusLabel;
    @FXML
    Label reconnectLabel;
    @FXML
    ListView<Friend> friendListView;
    @FXML
    ListView<String> messageHistory;
    @FXML
    TextArea messageTextArea;
    @FXML
    TextField addFriendTextField;
    @FXML
    Button addFriendButton;
    @FXML
    Button sendButton;
    @FXML
    Label showingChatFromLabel;
    @FXML
    Label showingChatFromNameLabel;

    @FXML
    public void sendMessage(){
        if(selectedFriend == null)
            return;
        if(messageTextArea.getText().length() < Properties.getInt("message.minLength")){
            return;
        }if(messageTextArea.getText().length() > Properties.getInt("message.maxLength")){
            views.showMessage("MessageTooLong");
            return;
        }
        sendButton.setDisable(true);
        sendButton.setText(UINotifications.getString("Sending"));
        Message message = new Message(userNameLabel.getText(), selectedFriend.getUsername(),messageTextArea.getText());
        controller.sendMessage(message);
    }
    public void finishSendMessage(){
        sendButton.setDisable(false);
        sendButton.setText(UINotifications.getString("Send"));
    }
    public void finishAddFriend(){
        addFriendButton.setDisable(false);
        addFriendButton.setText(UINotifications.getString("addFriendBtn"));
    }

    @FXML
    public void addFriend(){
        if(addFriendTextField.getText().length() < Properties.getInt("username.minLength")){
            return;
        }if(addFriendTextField.getText().length() > Properties.getInt("username.maxLength")){
            views.showMessage("FriendnameTooLong");
            return;
        }
        addFriendButton.setDisable(true);
        addFriendButton.setText(UINotifications.getString("sendFriendRequest"));
        Friend friend = new Friend(addFriendTextField.getText());
        addFriendTextField.clear();
        controller.addFriend(friend);
    }

    @FXML
    public void handleMouseClick() {
        Friend selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if(selectedFriend == null){
            return;
        }
        if(showingChatFromLabel.getText().isEmpty()){
            showingChatFromLabel.setText("Showing Chat from:");
        }
        showingChatFromNameLabel.setText(selectedFriend.getUsername());
        List<Message> friendMessages = fileChatDatabase.getMessages(selectedFriend.getUsername());
        this.selectedFriend = selectedFriend;
        messageHistory.getItems().clear();
        for(Message message : friendMessages) {
            addItemToMessageHistory(message);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public TextArea getMessageTextArea(){
        return messageTextArea;
    }
    public void setViews(Views views) {
        this.views = views;
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
        if(selectedFriend !=null) {
            if (selectedFriend.getUsername().equals(message.getRecipient())) {
                addItemToMessageHistory(message);
                handleMouseClick();
            }
        }
    }

    private void addItemToMessageHistory(Message message) {
        String chatHistoryMessage = message.getSender() +":\t"+ message.getText();
        messageHistory.getItems().add(chatHistoryMessage);
    }

    public void refreshFriendList(List<Friend> friendList) {
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

    public void setReconnectLabelText(int attempt, boolean wasConnectionSuccessful){
        if(wasConnectionSuccessful){
            sendButton.setDisable(false);
            addFriendButton.setDisable(false);
            reconnectLabel.setText("");
        }
        else {
            String reconnectMessage = UINotifications.getString("ReconnectMessage") + attempt;
            sendButton.setDisable(true);
            addFriendButton.setDisable(true);
            reconnectLabel.setText(reconnectMessage);
        }
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

    public void refreshMessageList(String sender) {
        try {
            if (sender.equals(selectedFriend.getUsername())) {
                List<Message> friendMessages = fileChatDatabase.getMessages(selectedFriend.getUsername());
                messageHistory.getItems().clear();
                for (Message message : friendMessages) {
                    addItemToMessageHistory(message);
                }
            }
        }catch(NullPointerException e){

        }
    }
}
