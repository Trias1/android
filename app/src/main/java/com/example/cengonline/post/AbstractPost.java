package com.example.cengonline.post;


import java.io.Serializable;
import com.example.cengonline.model.MyTimestamp;

public abstract class AbstractPost implements Postable, Editable, Serializable {

    protected String postedBy;
    private MyTimestamp postedAt;
    private String body;
    private MyTimestamp editedAt;
    protected String editedBy;

    public AbstractPost(){

    }

    public AbstractPost(String postedBy, MyTimestamp postedAt, String body){
        this.postedAt = postedAt;
        this.postedBy = postedBy;
        this.body = body;
    }


    @Override
    public String getPostedBy(){
        return this.postedBy;
    }

    @Override
    public MyTimestamp getPostedAt(){
        return this.postedAt;
    }

    @Override
    public String getBody(){
        return this.body;
    }

    @Override
    public MyTimestamp getEditedAt(){
        return this.editedAt;
    }

    @Override
    public String getEditedBy() {
        return this.editedBy;
    }

    @Override
    public void setPostedAt(MyTimestamp postedAt){
        this.postedAt = postedAt;
    }

    @Override
    public void setBody(String body){
        this.body = body;
    }

    @Override
    public void setEditedAt(MyTimestamp editedAt){
        this.editedAt = editedAt;
    }

    @Override
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    @Override
    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }
}
