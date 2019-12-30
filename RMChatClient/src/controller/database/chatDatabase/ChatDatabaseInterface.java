package controller.database.chatDatabase;

import model.Message;

import java.util.List;

public interface ChatDatabaseInterface {
    List<Message> getMessages(String sender);
    void addMessage(Message message);
    void save();
    void load();
}
