package model;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 3366046728803888722L;
    private String sender;
    private String recipient;
    private String text;
    //TODO
    //private String time;

    public Message(String sender, String recipient, String text) {

        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }
}
