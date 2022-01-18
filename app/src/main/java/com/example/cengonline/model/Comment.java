package com.example.cengonline.model;

import java.io.Serializable;

public class Comment extends Message implements Comparable {

    public Comment(){
        super();
    }

    public Comment(String senderKey, MyTimestamp sentAt, String body){
        super(senderKey, sentAt, body);
    }

    @Override
    public int compareTo(Object o) {
        Comment cmp = (Comment)o;
        if(this.getSentAt().after(cmp.getSentAt())){
            return 1;
        }
        else{
            return -1;
        }

    }
}
