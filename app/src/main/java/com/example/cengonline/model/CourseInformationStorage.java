package com.example.cengonline.model;


import java.io.Serializable;
import java.util.List;

public class CourseInformationStorage<T> implements Serializable {

    protected List<T> dataList;
    protected String courseKey;
    protected String key;


    protected CourseInformationStorage(){
    }

    protected CourseInformationStorage(String key, List<T> dataList, String courseKey){
        this.key = key;
        this.dataList = dataList;
        this.courseKey = courseKey;
    }

    public String getCourseKey() {
        return this.courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
