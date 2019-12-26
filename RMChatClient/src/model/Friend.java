package model;

public class Friend {
    private String username;
    private boolean status;

    /**
     *
     * @param username
     * @param status    true = online /// false = offline
     */
    public Friend(String username, boolean status) {
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
