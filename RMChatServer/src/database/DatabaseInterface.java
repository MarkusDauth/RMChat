package database;

public interface DatabaseInterface {
    boolean registerUser(String username, String password);
    boolean usernameIsPresent(String username);

    boolean checkPassword(String username, String password);

    boolean addFriend(String username, String newFriend);
}
