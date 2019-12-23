package database;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TextfileDatabase implements DatabaseInterface {
    private static TextfileDatabase instance = null;
    private static final Logger logger = Logger.getLogger("logger");

    private List<User> users = new LinkedList<>();

    public static DatabaseInterface getInstance() {
        if(instance == null){
            instance = new TextfileDatabase();
        }

        return instance;
    }

    private TextfileDatabase() {
        load();
    }

    @Override
    public synchronized boolean registerUser(String username, String password) {
        User user = new User(username, password);
        users.add(user);
        return save();
    }

    @Override
    public synchronized boolean usernameIsPresent(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public synchronized boolean checkPassword(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password))
                    return true;
            }
        }
        return false;
    }

    /**
     * Save Userlist to .ser file
     * @return
     */
    private synchronized boolean save() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream("users.ser")))) {
            out.writeObject(users);
            out.flush();
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    /**
     * Only load ser. File if it exists
     */
    @SuppressWarnings("unchecked")
    private synchronized void load() {
        File f = new File("users.ser");
        if(f.exists()){
            try (ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream("users.ser")))) {
                users = (List<User>) in.readObject();
                logger.info("User data succesfully loaded");
            } catch (IOException | ClassNotFoundException e) {
                logger.severe(e.getMessage());
            }
        }
        else{
            logger.info("users.ser does not exist");
        }
    }
}
