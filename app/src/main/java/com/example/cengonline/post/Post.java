package com.example.cengonline.post;

import androidx.annotation.Nullable;

import com.example.cengonline.model.Comment;
import com.example.cengonline.model.MyTimestamp;

import java.util.ArrayList;
import java.util.List;

public class Post extends AbstractPost {

    private List<Comment> comments;

    public Post(){

    }

    public Post(String postedBy, MyTimestamp postedAt, String body){
        super(postedBy, postedAt, body);
        this.comments = new ArrayList<Comment>();
    }

    public List<Comment> getComments(){
        return this.comments;
    }

    public void setComments (List<Comment> comments){
        this.comments = comments;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        Post o2 = (Post) obj;
        return this.postedBy.equals(o2.postedBy) && this.getPostedAt().toString().equals(o2.getPostedAt().toString()) && this.getBody().equals(o2.getBody());
    }

}
