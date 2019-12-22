package database;

public interface DatabaseInterface {
    boolean registerUser(String username, String password);
    boolean usernameIsPresent(String username);
}
