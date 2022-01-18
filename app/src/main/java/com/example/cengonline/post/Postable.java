package com.example.cengonline.post;

import com.example.cengonline.model.MyTimestamp;

public interface Postable {

    public String getPostedBy();
    public MyTimestamp getPostedAt();
    public String getBody();
    public void setPostedBy(String postedBy);
    public void setPostedAt(MyTimestamp postedAt);
    public void setBody(String body);
}
