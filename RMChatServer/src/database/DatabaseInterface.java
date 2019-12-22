package database;

public interface DatabaseInterface {
    public boolean checkPassword(String username, String password);
    public void registerUser(String username, String password);
}
