package controller.database.chatDatabase;

import model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileChatDatabase implements ChatDatabase {
    private static FileChatDatabase instance = null;

    private final Logger logger = Logger.getLogger("logger");
    private List<Message> messageList = new ArrayList<>();

    private FileChatDatabase() {
        load();
    }

    public static ChatDatabase getInstance() {
        if (instance == null)
            instance = new FileChatDatabase();
        return instance;
    }


    @Override
    public synchronized List<Message> getMessages(String sender) {
        List<Message> senderMessageList = messageList.stream().filter(message -> message.getSender().equals(sender)).collect(Collectors.toList());
        logger.info("Reading " + sender + "'s messages");
        return senderMessageList;
    }

    @Override
    public synchronized void addMessage(Message message) {
        messageList.add(message);
    }

    @Override
    public synchronized void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream("messages.ser",false)))) {
            out.writeObject(messageList);
            out.flush();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void load() {
        File f = new File("messages.ser");
        if (f.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream("messages.ser")))) {
                messageList = (List<Message>) in.readObject();
                logger.info("Messages succesfully loaded");
            } catch (IOException | ClassNotFoundException e) {
                logger.severe(e.getMessage());
            }
        } else {
            logger.info("messages.ser does not exist");
        }
    }
}
