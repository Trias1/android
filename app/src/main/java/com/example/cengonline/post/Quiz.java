package com.example.cengonline.post;


import androidx.annotation.Nullable;

import com.example.cengonline.model.MyTimestamp;

import java.io.Serializable;

public class Quiz extends AbstractPost implements Serializable {

    public Quiz(){

    }

    public Quiz(String postedBy, MyTimestamp postedAt, String body){
        super(postedBy, postedAt, body);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        Quiz o2 = (Quiz) obj;
        return this.postedBy.equals(o2.postedBy) && this.getPostedAt().toString().equals(o2.getPostedAt().toString()) && this.getBody().equals(o2.getBody());
    }
}
