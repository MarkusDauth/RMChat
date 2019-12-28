package model;

import controller.UserStatus;

public class Friend {
    private String username;
    private UserStatus status;

    /**
     *  @param username
     *  @param status
     */
    public Friend(String username, UserStatus status) {
        this.username = username;
        this.status = status;
    }

    /**
     * Needed to add a User.
     * @param username
     */
    public Friend(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus isStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "username='" + username + '\'' +
                ", status=" + status +
                '}';
    }
}
