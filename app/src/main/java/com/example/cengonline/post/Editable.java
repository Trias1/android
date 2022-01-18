package com.example.cengonline.post;

import com.example.cengonline.model.MyTimestamp;

public interface Editable {

    public MyTimestamp getEditedAt();
    public void setEditedAt(MyTimestamp editedAt);
    public String getEditedBy();
    public void setEditedBy(String editedBy);
}
