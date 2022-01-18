package com.example.cengonline.model;

import com.example.cengonline.post.Assignment;

import java.io.Serializable;
import java.util.List;

public class CourseAssignments extends CourseInformationStorage<Assignment> {

    public CourseAssignments(){
    }

    public CourseAssignments(List<Assignment> assignments, String courseKey, String key) {
        super(key, assignments, courseKey);
    }

    public List<Assignment> getAssignments() {
        return this.dataList;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.dataList = assignments;
    }


}
