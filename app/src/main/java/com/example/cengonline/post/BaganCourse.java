package com.example.cengonline.post;


import androidx.annotation.Nullable;

import com.example.cengonline.model.MyTimestamp;

import java.io.Serializable;

public class BaganCourse extends AbstractPost implements Serializable {

    public BaganCourse(){

    }

    public BaganCourse(String postedBy, MyTimestamp postedAt, String body){
        super(postedBy, postedAt, body);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        BaganCourse o2 = (BaganCourse) obj;
        return this.postedBy.equals(o2.postedBy) && this.getPostedAt().toString().equals(o2.getPostedAt().toString()) && this.getBody().equals(o2.getBody());
    }
}
