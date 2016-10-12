package com.yendry.firebase;

/**
 * Created by User on 10/11/2016.
 */

public class Message {
    public String getMessage() {
        return message;
    }


    public String getSender() {
        return sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    String message,sender;
}
