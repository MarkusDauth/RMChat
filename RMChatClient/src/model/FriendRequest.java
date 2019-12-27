package model;

public class FriendRequest {

    private boolean requestAccepted;
    private String requester;

    public FriendRequest(boolean requestAccepted, String requester) {
        this.requestAccepted = requestAccepted;
        this.requester = requester;
    }

    public boolean isRequestAccepted() {
        return requestAccepted;
    }
    public String isRequestAcceptedString() {
        return requestAccepted == true ? Character.toString('1') : Character.toString('0');
    }

    public void setRequestAccepted(boolean requestAccepted) {
        this.requestAccepted = requestAccepted;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }
}
