package com.example.cengonline.model;

import com.example.cengonline.post.Announcement;

import java.util.List;

public class CourseAnnouncements extends CourseInformationStorage<Announcement>  {

    public CourseAnnouncements(){
    }

    public CourseAnnouncements(String key, List<Announcement> announcements, String courseKey){
        super(key, announcements, courseKey);
    }

    public List<Announcement> getAnnouncements() {
        return this.dataList;
    }

    public void setAnnouncements(List<Announcement> announcements){
        this.dataList = announcements;
    }
}
