package controller.database.chatDatabase;

import controller.Controller;
import model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Mithilfe dieser Klasse werden Chatnachrichten lokal auf einer datei serialisiert
 */
public class FileChatDatabase implements ChatDatabaseInterface {
    private static FileChatDatabase instance = null;

    private final Logger logger = Logger.getLogger("logger");
    private List<Message> messageList = new ArrayList<>();

    private FileChatDatabase() {
        load();
    }

    public static ChatDatabaseInterface getInstance() {
        if (instance == null)
            instance = new FileChatDatabase();
        return instance;
    }

    /**
     * Diese Klasse gibt alle nachrichten von sender und Nutzer zurueck
     * @param sender
     * @return
     */
    @Override
    public synchronized List<Message> getMessages(String sender) {
        List<Message> senderMessageList = messageList.stream().filter(message -> isMessageToOrFromSender(message,sender)).collect(Collectors.toList());
        logger.info("Reading " + sender + "'s messages");
        return senderMessageList;
    }

    /**
     * Mit dieser methode wird eine neue nachricht zur liste hinzugefuegt
     * @param message
     */
    @Override
    public synchronized void addMessage(Message message) {
        messageList.add(message);
    }

    /**
     * Mit dieser methode werden die gespeicherten nachrichten in die datei messages.ser geschrieben
     */
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

    /**
     * Hier werden die serialisierten Objekte in die Liste geladen
     */
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

    private boolean isMessageToOrFromSender(Message message, String sender){
        if(message.getSender().equals(sender) && message.getRecipient().equals(Controller.getUsername())
                || message.getSender().equals(Controller.getUsername()) && message.getRecipient().equals(sender))
            return true;
        return false;
    }
}
