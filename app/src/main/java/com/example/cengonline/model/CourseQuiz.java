package com.example.cengonline.model;

import com.example.cengonline.post.Quiz;

import java.util.List;

public class CourseQuiz extends CourseInformationStorage<Quiz> {

    public CourseQuiz(){

    }

    public CourseQuiz(String key, List<Quiz> quizzes, String courseKey ){
        super(key, quizzes, courseKey);
    }

    public List<Quiz> getQuiz() {
        return this.dataList;
    }

    public void setQuiz(List<Quiz> quizzes){
        this.dataList = quizzes;
    }
}
