package com.example.cengonline.model;

import com.example.cengonline.post.Announcement;
import com.example.cengonline.post.Assignment;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {

    private String key;
    private int imageId;
    private String className;
    private String classSection;
    private String classSubject;
    private String classCode;
    private String createdBy;
    private List<String> teacherList;
    private List<String> studentList;

    public Course(){

    }


    public Course(String key, int imageId, String className, String classSection, String classSubject, String classCode, String createdBy, List<String> teacherList, List<String> studentList) {
        this.key = key;
        this.imageId = imageId;
        this.className = className;
        this.classSection = classSection;
        this.classSubject = classSubject;
        this.classCode = classCode;
        this.createdBy = createdBy;
        this.teacherList = teacherList;
        this.studentList = studentList;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassSection() {
        return classSection;
    }

    public void setClassSection(String classSection) {
        this.classSection = classSection;
    }

    public String getClassSubject() {
        return classSubject;
    }

    public void setClassSubject(String classSubject) {
        this.classSubject = classSubject;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<String> teacherList) {
        this.teacherList = teacherList;
    }

    public List<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<String> studentList) {
        this.studentList = studentList;
    }

    public List<String> enrollStudent(User user){
        if(user.getRoles().contains(User.Role.STUDENT))
            this.studentList.add(user.getKey());
        return this.studentList;
    }

    public List<String> enrollTeacher(User user){
        if(user.getRoles().contains(User.Role.TEACHER))
            this.teacherList.add(user.getKey());
        return this.teacherList;
    }
}
