package com.example.cengonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cengonline.model.Comment;
import com.example.cengonline.model.Conversation;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.CourseAnnouncements;
import com.example.cengonline.model.CourseAssignments;
import com.example.cengonline.model.CoursePosts;
import com.example.cengonline.model.CourseQuiz;
import com.example.cengonline.model.Message;
import com.example.cengonline.model.MyTimestamp;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Announcement;
import com.example.cengonline.post.Assignment;
import com.example.cengonline.post.Post;
import com.example.cengonline.post.Quiz;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DatabaseUtility {

    private static DatabaseUtility instance;

    private DatabaseUtility(){

    }

    public void getUser(final DatabaseCallback callback){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");
        Query query = ref.orderByChild("uid").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                        callback.onSuccess(childSnapshot.getValue(User.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("An error occurred try again please!");
            }
        });
    }

    public void getUser(final String key, final DatabaseCallback callback){

        Query user = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("key").equalTo(key);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                }
                callback.onSuccess(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("Error occurred while fetching user with given key: " + key);
            }
        });
    }

    public void getRandomClassCode(final DatabaseCallback callback){

        final String code = UUID.randomUUID().toString().substring(0, 7);
        final DatabaseReference courseIdsRef = FirebaseDatabase.getInstance().getReference().child("courseCodeIds");
        Query query = courseIdsRef.equalTo(code);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    courseIdsRef.push().setValue(code);
                    callback.onSuccess(code);
                }
                else{
                    callback.onFailed("An error occurred try again please!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("An error occurred try again please!");
            }
        });
    }

    public void saveNewCourse(String courseName, String courseSection, String courseSubject, String classCode, User user){

        int[] images = {
                R.drawable.img_blue_office,
                R.drawable.img_gray,
                R.drawable.img_office,
                R.drawable.img_red,
                R.drawable.img_yellow,
                R.drawable.img_website
        };

        Random random  = new Random();
        int courseImage = images[random.nextInt(images.length)];

        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("courses");
        final DatabaseReference newVal = coursesRef.push();
        final Course course = new Course(newVal.getKey(), courseImage, courseName, courseSection, courseSubject, classCode, user.getKey(), Arrays.asList(user.getKey()), new ArrayList<String>());
        newVal.setValue(course);
    }

    public void updateCourse(Course course, final DatabaseCallback callback){

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses");
        Query query = courseRef.orderByKey().equalTo(course.getKey());
        courseRef.child(course.getKey()).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess("Course updated successfully!");
            }
        });

    }


    public void addStudentToCourse(String classCode, final DatabaseCallback callback){

        final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses");
        Query query = courseRef.orderByChild("classCode").equalTo(classCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        final String courseKey = ds.getKey();
                        final Course course = ds.getValue(Course.class);
                        getUser(new DatabaseCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                final User student = (User) result;
                                if(student.getRoles().contains(User.Role.TEACHER)){
                                    List<String> techerList = course.getTeacherList();
                                    if(techerList == null){
                                        techerList = Arrays.asList(student.getKey());
                                    }
                                    else{
                                        if(techerList.contains(student.getKey())){
                                            callback.onFailed("You have already enrolled!");
                                            return;
                                        }
                                        else{
                                            techerList.add(student.getKey());
                                        }
                                    }
                                    course.setTeacherList(techerList);
                                    courseRef.child(courseKey).setValue(course);
                                }
                                else{
                                    List<String> studentList = course.getStudentList();
                                    if(studentList == null){
                                        studentList = Arrays.asList(student.getKey());
                                    }
                                    else{
                                        if(studentList.contains(student.getKey())){
                                            callback.onFailed("You have already enrolled!");
                                            return;
                                        }
                                        else{
                                            studentList.add(student.getKey());
                                        }
                                    }
                                    course.setStudentList(studentList);
                                    courseRef.child(courseKey).setValue(course);
                                }
                                callback.onSuccess("You enrolled the class successfully.");
                            }

                            @Override
                            public void onFailed(String message) {
                                callback.onFailed("An error occurred!");
                            }
                        });
                    }
                }
                else{
                    callback.onFailed("Class is not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("An error occurred!");
            }
        });
    }

    public void getAllCourses(final DatabaseCallback callback){

        final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses");

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User) result;
                Query query = courseRef.orderByChild("key");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Course> courses = new ArrayList<Course>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Course course = ds.getValue(Course.class);
                            if(course.getTeacherList() != null && course.getTeacherList().contains(user.getKey())){
                                courses.add(course);
                            }
                            else if(course.getStudentList() != null && course.getStudentList().contains(user.getKey())){
                                courses.add(course);
                            }
                        }
                        callback.onSuccess(courses);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("An error occurred!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("An error occurred!");
            }
        });
    }

    public void removeStudentFromCourse(final Course course, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;

                final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses");
                Query query = courseRef.orderByChild("key").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Course course = ds.getValue(Course.class);
                            if(course.getStudentList() != null){
                                course.getStudentList().remove(user.getKey());
                            }
                            if(course.getTeacherList() != null){
                                course.getTeacherList().remove(user.getKey());
                            }
                            courseRef.child(course.getKey()).setValue(course);
                            callback.onSuccess("You unenrolled successfully!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void removeCourse(final Course course, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User) result;
                final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses");
                Query query = courseRef.orderByChild("key").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            Course course = ds.getValue(Course.class);
                            if(user.getKey().equals(course.getCreatedBy())){
                                courseRef.child(course.getKey()).removeValue();
                                callback.onSuccess("You removed course successfully!");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void newCourseAnnouncement(final Course course, final String announcementBody, final DatabaseCallback callback){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("courseAnnouncements");

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                if(user.getRoles().contains(User.Role.TEACHER) && course.getTeacherList().contains(user.getKey())){
                    Query query = ref.orderByChild("courseKey").equalTo(course.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                CourseAnnouncements announcements = null;
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    announcements = ds.getValue(CourseAnnouncements.class);
                                }
                                if(announcements != null && announcements.getAnnouncements() != null){
                                    Announcement announcement = new Announcement(user.getKey(), new MyTimestamp(new Date()), announcementBody);
                                    announcements.getAnnouncements().add(announcement);
                                    ref.child(announcements.getKey()).setValue(announcements);
                                    callback.onSuccess("You have posted an announcement!");
                                }
                                else{
                                    callback.onFailed("An error occurred while posting announcement!");
                                }
                            }
                            else{
                                DatabaseReference newVal = ref.push();
                                Announcement announcement = new Announcement(user.getKey(), new MyTimestamp(new Date()), announcementBody);
                                CourseAnnouncements announcements = new CourseAnnouncements(newVal.getKey(), Arrays.asList(announcement), course.getKey());
                                newVal.setValue(announcements);
                                callback.onSuccess("You have posted an announcement!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onFailed("An error occurred while posting announcement!");
                        }
                    });
                }
                else{
                    callback.onFailed("You are not authorized to post announcement!");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getCourseAnnouncements(final Course course, final DatabaseCallback callback){

        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference().child("courseAnnouncements");
        Query query = announcementsRef.orderByChild("courseKey").equalTo(course.getKey());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseAnnouncements ca = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ca = ds.getValue(CourseAnnouncements.class);
                }
                if(ca != null && ca.getAnnouncements() != null){
                    callback.onSuccess(ca);
                }
                else{
                    callback.onSuccess("Announcements empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("Error!");
            }
        });
    }

    public void deleteCourseAnnouncement(final Course course, final Announcement announcement, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference courseAnnouncementsRef = FirebaseDatabase.getInstance().getReference().child("courseAnnouncements");
                Query query = courseAnnouncementsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseAnnouncements courseAnnouncements = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseAnnouncements = ds.getValue(CourseAnnouncements.class);
                            }
                            if(courseAnnouncements != null && courseAnnouncements.getAnnouncements() != null){
                                courseAnnouncements.getAnnouncements().remove(announcement);
                                if(courseAnnouncements.getAnnouncements().isEmpty()){
                                    courseAnnouncementsRef.child(courseAnnouncements.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            callback.onSuccess("You deleted announcement successfully!");
                                        }
                                    });
                                }
                                else{
                                    courseAnnouncementsRef.child(courseAnnouncements.getKey()).setValue(courseAnnouncements);
                                    callback.onSuccess("You deleted announcement successfully!");
                                }
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void updateCourseAnnouncement(final Course course, final Announcement announcement, final String newBody, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference courseAnnouncementsRef = FirebaseDatabase.getInstance().getReference().child("courseAnnouncements");
                Query query = courseAnnouncementsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseAnnouncements courseAnnouncements = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseAnnouncements = ds.getValue(CourseAnnouncements.class);
                            }
                            if(courseAnnouncements != null && courseAnnouncements.getAnnouncements() != null){
                                courseAnnouncements.getAnnouncements().remove(announcement);
                                announcement.setBody(newBody);
                                announcement.setEditedBy(user.getKey());
                                announcement.setEditedAt(new MyTimestamp(new Date()));
                                courseAnnouncements.getAnnouncements().add(announcement);
                                courseAnnouncementsRef.child(courseAnnouncements.getKey()).setValue(courseAnnouncements);
                                callback.onSuccess("You updated announcement successfully!");
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }


    public void newCourseQuiz(final Course course, final String quizBody, final DatabaseCallback callback){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("courseQuiz");

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                if(user.getRoles().contains(User.Role.TEACHER) && course.getTeacherList().contains(user.getKey())){
                    Query query = ref.orderByChild("courseKey").equalTo(course.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                CourseQuiz quizzes = null;
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    quizzes = ds.getValue(CourseQuiz.class);
                                }
                                if(quizzes != null && quizzes.getQuiz() != null){
                                    Quiz quiz = new Quiz(user.getKey(), new MyTimestamp(new Date()), quizBody);
                                    quizzes.getQuiz().add(quiz);
                                    ref.child(quizzes.getKey()).setValue(quizzes);
                                    callback.onSuccess("You have posted an quiz!");
                                }
                                else{
                                    callback.onFailed("An error occurred while posting quiz!");
                                }
                            }
                            else{
                                DatabaseReference newVal = ref.push();
                                Quiz quiz = new Quiz(user.getKey(), new MyTimestamp(new Date()), quizBody);
                                CourseQuiz quizzes = new CourseQuiz(newVal.getKey(), Arrays.asList(quiz), course.getKey());
                                newVal.setValue(quizzes);
                                callback.onSuccess("You have posted an quiz!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onFailed("An error occurred while posting quiz!");
                        }
                    });
                }
                else{
                    callback.onFailed("You are not authorized to post quiz!");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getCourseQuiz(final Course course, final DatabaseCallback callback){

        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child("courseQuiz");
        Query query = quizRef.orderByChild("courseKey").equalTo(course.getKey());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseQuiz ca = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ca = ds.getValue(CourseQuiz.class);
                }
                if(ca != null && ca.getQuiz() != null){
                    callback.onSuccess(ca);
                }
                else{
                    callback.onSuccess("Quiz empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("Error!");
            }
        });
    }

    public void deleteCourseQuiz(final Course course, final Quiz quiz, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference courseQuizRef = FirebaseDatabase.getInstance().getReference().child("courseQuiz");
                Query query = courseQuizRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseQuiz courseQuiz = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseQuiz = ds.getValue(CourseQuiz.class);
                            }
                            if(courseQuiz != null && courseQuiz.getQuiz() != null){
                                courseQuiz.getQuiz().remove(quiz);
                                if(courseQuiz.getQuiz().isEmpty()){
                                    courseQuizRef.child(courseQuiz.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            callback.onSuccess("You deleted quiz successfully!");
                                        }
                                    });
                                }
                                else{
                                    courseQuizRef.child(courseQuiz.getKey()).setValue(courseQuiz);
                                    callback.onSuccess("You deleted Quiz successfully!");
                                }
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void updateCourseQuiz(final Course course, final Quiz quizzes, final String newBody, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference courseQuizRef = FirebaseDatabase.getInstance().getReference().child("courseQuiz");
                Query query = courseQuizRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseQuiz courseQuiz = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseQuiz = ds.getValue(CourseQuiz.class);
                            }
                            if(courseQuiz != null && courseQuiz.getQuiz() != null){
                                courseQuiz.getQuiz().remove(quizzes);
                                quizzes.setBody(newBody);
                                quizzes.setEditedBy(user.getKey());
                                quizzes.setEditedAt(new MyTimestamp(new Date()));
                                courseQuiz.getQuiz().add(quizzes);
                                courseQuizRef.child(courseQuiz.getKey()).setValue(courseQuiz);
                                callback.onSuccess("You updated quiz successfully!");
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void newCourseAssignment(final Course course, final Assignment assignment, final DatabaseCallback callback ){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("courseAssignments");

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                assignment.setPostedBy(user.getKey());
                if(user.getRoles().contains(User.Role.TEACHER) && course.getTeacherList().contains(user.getKey())){
                    Query query = ref.orderByChild("courseKey").equalTo(course.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                CourseAssignments assignments = null;
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    assignments = ds.getValue(CourseAssignments.class);
                                }
                                if(assignments != null && assignments.getAssignments() != null){
                                    assignments.getAssignments().add(assignment);
                                    ref.child(assignments.getKey()).setValue(assignments);
                                    callback.onSuccess("You have posted an assignment!");
                                }
                                else{
                                    callback.onFailed("An error occurred while posting assignment!");
                                }
                            }
                            else{
                                DatabaseReference newVal = ref.push();
                                CourseAssignments assignments = new CourseAssignments(Arrays.asList(assignment), course.getKey(), newVal.getKey());
                                newVal.setValue(assignments);
                                callback.onSuccess("You have posted an assignment!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onFailed("An error occurred while posting assignment!");
                        }
                    });
                }
                else{
                    callback.onFailed("You are not authorized to post assignment!");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getCourseAssignments(final Course course, final DatabaseCallback callback){

        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference().child("courseAssignments");
        Query query = announcementsRef.orderByChild("courseKey").equalTo(course.getKey());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseAssignments ca = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ca = ds.getValue(CourseAssignments.class);
                }
                if(ca != null && ca.getAssignments() != null){
                    callback.onSuccess(ca);
                }
                else{
                    callback.onSuccess("Assignments empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("Error!");
            }
        });
    }

    public void updateCourseAssignments(final Course course, final Assignment assignment, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference couseAssignmentsRef = FirebaseDatabase.getInstance().getReference().child("courseAssignments");
                Query query = couseAssignmentsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseAssignments courseAssignments = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseAssignments = ds.getValue(CourseAssignments.class);
                            }
                            if(courseAssignments != null && courseAssignments.getAssignments() != null){
                                courseAssignments.getAssignments().remove(assignment);
                                assignment.setEditedBy(user.getKey());
                                assignment.setEditedAt(new MyTimestamp(new Date()));
                                courseAssignments.getAssignments().add(assignment);
                                couseAssignmentsRef.child(courseAssignments.getKey()).setValue(courseAssignments);
                                callback.onSuccess("You updated assignment successfully!");
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void deleteCourseAssignment(final Course course, final Assignment assignment, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference courseAssignmentsRef = FirebaseDatabase.getInstance().getReference().child("courseAssignments");
                Query query = courseAssignmentsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CourseAssignments courseAssignments = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                courseAssignments = ds.getValue(CourseAssignments.class);
                            }
                            if(courseAssignments != null && courseAssignments.getAssignments() != null){
                                courseAssignments.getAssignments().remove(assignment);
                                if(courseAssignments.getAssignments().isEmpty()){
                                    courseAssignmentsRef.child(courseAssignments.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            callback.onSuccess("You deleted assignment successfully!");
                                        }
                                    });
                                }
                                else{
                                    courseAssignmentsRef.child(courseAssignments.getKey()).setValue(courseAssignments);
                                    callback.onSuccess("You deleted assignment successfully!");
                                }
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void getUserFromEmail(final String email, final DatabaseCallback callback){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    user = ds.getValue(User.class);
                }
                if(user == null){
                    callback.onFailed("User not found!");
                }
                else{
                    callback.onSuccess(user) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("An error occurred! Try again.");
            }
        });
    }

    public void newMessage(final String conversationKey, final Message message, final DatabaseCallback callback){

        final DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("conversations");
        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                message.setSenderKey(user.getKey());
                message.setSentAt(new MyTimestamp(new Date()));
                Query query = messagesRef.orderByKey().equalTo(conversationKey);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Conversation conversation = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                conversation = ds.getValue(Conversation.class);
                            }
                            if(conversation != null && conversation.getMessages() != null){
                                conversation.getMessages().add(message);
                                messagesRef.child(conversation.getKey()).setValue(conversation);
                                callback.onSuccess("Successful!");
                            }
                            else if(conversation != null){
                                conversation.setMessages(Arrays.asList(message));
                                messagesRef.child(conversation.getKey()).setValue(conversation);
                                callback.onSuccess("Successful!");
                            }
                            else{
                                callback.onFailed("An error occurred while sending message!");
                            }
                        }
                        else{
                            callback.onFailed("An error occurred while sending message!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("An error occurred while sending message!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("An error occurred while sending message!");
            }
        });

    }

    public void getConversations(final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                conversationsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            List<Conversation> conversations = new ArrayList<Conversation>();
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Conversation conversation = ds.getValue(Conversation.class);
                                if(conversation != null && (conversation.getSender().getKey().equals(user.getKey()) || conversation.getReceiver().getKey().equals(user.getKey()))){
                                    conversations.add(conversation);
                                }
                            }
                            callback.onSuccess(conversations);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("An error occurred while getting messages!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getConversations(final User secondUser,final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Conversation conversation = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                conversation = ds.getValue(Conversation.class);
                                if(conversation != null &&
                                        (conversation.getSender().getKey().equals(user.getKey()) || conversation.getReceiver().getKey().equals(user.getKey())) &&
                                        (conversation.getSender().getKey().equals(secondUser.getKey()) || conversation.getReceiver().getKey().equals(secondUser.getKey()))
                                ){
                                    break;
                                }
                                else{
                                    conversation = null;
                                }
                            }
                            callback.onSuccess(conversation);
                        }
                        else{
                            callback.onSuccess(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("An error occurred while getting messages!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getConversationWithKey(final String conversationKey, final DatabaseCallback callback){

        DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference().child("conversations");
        Query query = conversationRef.orderByKey().equalTo(conversationKey);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Conversation conversation = null;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        conversation = ds.getValue(Conversation.class);
                    }
                    if(conversation != null){
                        callback.onSuccess(conversation);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void newCoursePost(final Course course, final String postBody, final DatabaseCallback callback){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("coursePosts");

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                if(user.getRoles().contains(User.Role.TEACHER) && course.getTeacherList().contains(user.getKey())){
                    Query query = ref.orderByChild("courseKey").equalTo(course.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                CoursePosts posts = null;
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    posts = ds.getValue(CoursePosts.class);
                                }
                                if(posts != null && posts.getPosts() != null){
                                    Post post = new Post(user.getKey(), new MyTimestamp(new Date()), postBody);
                                    posts.getPosts().add(post);
                                    ref.child(posts.getKey()).setValue(posts);
                                    callback.onSuccess("You have posted a new post!");
                                }
                                else{
                                    callback.onFailed("An error occurred while posting a new post!");
                                }
                            }
                            else{
                                DatabaseReference newVal = ref.push();
                                Post post = new Post(user.getKey(), new MyTimestamp(new Date()), postBody);
                                CoursePosts posts = new CoursePosts(Arrays.asList(post), course.getKey(), newVal.getKey());
                                newVal.setValue(posts);
                                callback.onSuccess("You have posted a new post!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onFailed("An error occurred while posting post!");
                        }
                    });
                }
                else{
                    callback.onFailed("You are not authorized to post a new post!");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void getCoursePosts(final Course course, final DatabaseCallback callback){

        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference().child("coursePosts");
        Query query = announcementsRef.orderByChild("courseKey").equalTo(course.getKey());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursePosts cp = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    cp = ds.getValue(CoursePosts.class);
                }
                if(cp != null && cp.getPosts() != null){
                    callback.onSuccess(cp);
                }
                else{
                    callback.onSuccess("Posts empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("Error!");
            }
        });
    }

    public void deleteCoursePost(final Course course, final Post post, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference coursePostsRef = FirebaseDatabase.getInstance().getReference().child("coursePosts");
                Query query = coursePostsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CoursePosts coursePosts = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                coursePosts = ds.getValue(CoursePosts.class);
                            }
                            if(coursePosts != null && coursePosts.getPosts() != null){
                                coursePosts.getPosts().remove(post);
                                if(coursePosts.getPosts().isEmpty()){
                                    coursePostsRef.child(coursePosts.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            callback.onSuccess("You deleted post successfully!");
                                        }
                                    });
                                }
                                else{
                                    coursePostsRef.child(coursePosts.getKey()).setValue(coursePosts);
                                    callback.onSuccess("You deleted post successfully!");
                                }
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void updateCoursePost(final Course course, final Post post, final String newBody, final DatabaseCallback callback){

        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                final DatabaseReference coursePostsRef = FirebaseDatabase.getInstance().getReference().child("coursePosts");
                Query query = coursePostsRef.orderByChild("courseKey").equalTo(course.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CoursePosts coursePosts = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                coursePosts = ds.getValue(CoursePosts.class);
                            }
                            if(coursePosts != null && coursePosts.getPosts() != null){
                                coursePosts.getPosts().remove(post);
                                post.setBody(newBody);
                                post.setEditedBy(user.getKey());
                                post.setEditedAt(new MyTimestamp(new Date()));
                                coursePosts.getPosts().add(post);
                                coursePostsRef.child(coursePosts.getKey()).setValue(coursePosts);
                                callback.onSuccess("You updated post successfully!");
                            }
                            else{
                                callback.onFailed("Something went wrong!");
                            }
                        }
                        else{
                            callback.onFailed("Something went wrong!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("Something went wrong!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("Something went wrong!");
            }
        });
    }

    public void newComment(final Course course, final Post post, final Comment comment, final DatabaseCallback callback){

        final DatabaseReference coursePostsRef = FirebaseDatabase.getInstance().getReference().child("coursePosts");
        getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;
                comment.setSenderKey(user.getKey());
                comment.setSentAt(new MyTimestamp(new Date()));
                Query query = coursePostsRef.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CoursePosts coursePosts = null;
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                coursePosts = ds.getValue(CoursePosts.class);
                                if(coursePosts.getCourseKey().equals(course.getKey())){
                                    break;
                                }
                                coursePosts = null;
                            }
                            if(coursePosts != null && coursePosts.getPosts() != null){
                                for(Post p : coursePosts.getPosts()){
                                    if(p.equals(post)){
                                        if(p.getComments() == null){
                                            p.setComments(new ArrayList<Comment>());
                                        }
                                        p.getComments().add(comment);
                                        coursePostsRef.child(coursePosts.getKey()).setValue(coursePosts);
                                        callback.onSuccess("Successful!");
                                    }
                                }
                            }
                            else{
                                callback.onFailed("An error occurred while sending message!");
                            }
                        }
                        else{
                            callback.onFailed("An error occurred while sending message!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailed("An error occurred while sending message!");
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                callback.onFailed("An error occurred while sending message!");
            }
        });

    }

    public void getComments(final Course course, final Post post, final DatabaseCallback callback){

        final DatabaseReference coursePostsRef = FirebaseDatabase.getInstance().getReference().child("coursePosts");
        Query query = coursePostsRef.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursePosts coursePosts = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    coursePosts = ds.getValue(CoursePosts.class);
                    if(coursePosts.getCourseKey().equals(course.getKey())){
                        break;
                    }
                    coursePosts = null;
                }
                if(coursePosts != null && coursePosts.getPosts() != null){
                    for(Post p : coursePosts.getPosts()){
                        if(p.equals(post)){
                            callback.onSuccess(p.getComments());
                        }
                    }
                }
                else{
                    callback.onFailed("An error occurred while fetching comments!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed("An error occurred while fetching comments!");
            }
        });
    }

    public static DatabaseUtility getInstance(){
        if(instance == null){
            instance = new DatabaseUtility();
        }

        return instance;
    }
}
