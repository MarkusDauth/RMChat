package database;

import java.io.Serializable;
import java.util.*;

class User implements Serializable {
    private static final long serialVersionUID = 6517483924825507038L;

    private String username;
    private String password;
    private HashSet<String> friends;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        this.friends = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void addFriend(String username){
        friends.add(username);
    }
}
