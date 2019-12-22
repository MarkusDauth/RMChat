package database;

public interface DatabaseInterface {
    public boolean registerUser(String username, String password);
    public boolean usernameIsPresent(String username);
}
