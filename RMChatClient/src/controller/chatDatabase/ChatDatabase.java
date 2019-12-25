package controller.chatDatabase;

import model.Message;

import java.util.List;

public interface ChatDatabase {
    List<Message> getMessages(String sender);
    void addMessage(Message message);
    void save();
    void load();
}
