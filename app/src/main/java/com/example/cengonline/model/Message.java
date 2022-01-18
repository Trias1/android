package com.example.cengonline.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Message implements Serializable {

    protected String senderKey;
    protected MyTimestamp sentAt;
    protected String body;

    public Message(){

    }

    public Message(String senderKey, MyTimestamp sentAt, String body) {
        this.senderKey = senderKey;
        this.sentAt = sentAt;
        this.body = body;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public MyTimestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(MyTimestamp sentAt) {
        this.sentAt = sentAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        Message m2 = (Message) obj;
        return this.sentAt.toString().equals(m2.sentAt.toString()) && this.body.equals(m2.body);
    }
}
