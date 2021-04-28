package com.example.handsgivingt;

public class Messages
{
    private String from, message, to, messageID, name;

    public Messages()
    {

    }

    public Messages(String from, String message, String to, String messageID, String name) {
        this.from = from;
        this.message = message;
        this.to = to;
        this.messageID = messageID;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}