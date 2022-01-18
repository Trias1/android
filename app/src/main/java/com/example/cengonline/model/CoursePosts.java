package com.example.cengonline.model;

import com.example.cengonline.post.Post;

import java.io.Serializable;
import java.util.List;

public class CoursePosts extends CourseInformationStorage<Post>  {


    public CoursePosts(){
    }

    public CoursePosts(List<Post> posts, String courseKey, String key) {
        super(key, posts, courseKey);
    }

    public List<Post> getPosts() {
        return this.dataList;
    }

    public void setPosts(List<Post> posts) {
        this.dataList = posts;
    }

}
