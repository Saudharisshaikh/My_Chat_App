package com.example.my_chat_app;

public class Messages {

    String From, message, type, to, messageId, date, time,name;


    public Messages() {


    }

    public Messages(String from, String message, String type, String to, String messageId, String date, String time, String name) {
        From = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.messageId = messageId;
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}