package database;

import java.util.Set;

public interface DatabaseInterface {
    boolean registerUser(String username, String password);
    boolean usernameIsPresent(String username);

    boolean checkPassword(String username, String password);

    boolean addFriend(String requester, String newFriend);

    Set<String> getFriends(String username);
}
