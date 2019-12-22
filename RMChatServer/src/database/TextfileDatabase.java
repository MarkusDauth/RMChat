package database;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TextfileDatabase implements DatabaseInterface {
    private static final TextfileDatabase instance = new TextfileDatabase();
    private static final Logger logger = Logger.getLogger("logger");

    private List<User> users = new LinkedList<>();

    public static DatabaseInterface getInstance(){
        return instance;
    }

    private TextfileDatabase(){
        load();
    }

    @Override
    public synchronized boolean registerUser(String username, String password) {
        User user = new User(username, password);
        users.add(user);
        return save();
    }

    @Override
    public boolean usernameIsPresent(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    private synchronized boolean save(){
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream("users.ser"))))
        {
            out.writeObject(users);
            out.flush();
            return true;
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void load(){
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream("users.ser"))) )
        {
            users = (List<User>) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.severe(e.getMessage());
        }
    }
}
