package com.example.cengonline.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable, Comparable {

    private String key;
    private User sender;
    private User receiver;
    private List<Message> messages;

    public Conversation(){
        this.messages = new ArrayList<Message>();
    }

    public Conversation(String key, User sender, User receiver, List<Message> messages) {
        this.key = key;
        this.sender = sender;
        this.receiver = receiver;
        this.messages = messages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int compareTo(Object o) {
        Conversation c2 = (Conversation)o;
        if(this.getMessages().size() == 0 || c2.getMessages().size() == 0)
            return 0;
        if(this.getMessages().get(this.getMessages().size() - 1).getSentAt().before(c2.getMessages().get(c2.getMessages().size() - 1).getSentAt()))
            return 1;
        else
            return -1;
    }
}
