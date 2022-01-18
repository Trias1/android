package com.example.cengonline.post;

import androidx.annotation.Nullable;

import com.example.cengonline.model.MyTimestamp;
import com.example.cengonline.model.User;
import com.example.cengonline.post.AbstractPost;

import java.sql.Timestamp;
import java.util.List;

public class Assignment extends AbstractPost {

    private String title;
    private MyTimestamp dueDate;

    public Assignment(){
        super();
    }

    public Assignment(String title, MyTimestamp dueDate, String postedBy, MyTimestamp postedAt, String body){
        super(postedBy, postedAt, body);
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MyTimestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(MyTimestamp dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        Assignment o2 = (Assignment) obj;
        return this.postedBy.equals(o2.postedBy) && this.getPostedAt().toString().equals(o2.getPostedAt().toString());
    }

}
