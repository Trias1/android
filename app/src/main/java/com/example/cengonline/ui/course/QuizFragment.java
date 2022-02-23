package com.example.cengonline.ui.course;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.cengonline.Create_Quiz.CustomQuiz;
import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.Test;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Quiz;
import com.example.cengonline.ui.dialog.EditQuizDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class QuizFragment extends AppCompatActivity implements View.OnClickListener {

    private static final int DELETE_ITEM = 1000;
    private static final int EDIT_ITEM = 1001;

    ArrayList<Test> questions;
    private Toolbar toolbar;
    private User user;
    private Course course;
    private CardView attempttest;
    private Quiz quiz;
    private TextView profilTextView;
    private TextView userTextView;
    private TextView dateTextView;
    private TextView quizTextView;
    private FloatingActionButton fab;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_fragment);

        this.toolbar = findViewById(R.id.course_toolbar);
        this.profilTextView = findViewById(R.id.quiz_detail_profil);
        this.userTextView = findViewById(R.id.quiz_detail_user);
        this.dateTextView = findViewById(R.id.quiz_detail_date);
        this.quizTextView = findViewById(R.id.quiz_detail_body);
        this.attempttest = findViewById(R.id.attemptTest);
        this.fab = findViewById(R.id.fab);
        this.quizTextView.setFocusable(false);
        this.questions = new ArrayList<Test>();

        this.fab.setOnClickListener(this);
        this.attempttest.setOnClickListener(this);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent() != null && getIntent().getSerializableExtra("user") != null && getIntent().getSerializableExtra("quiz") != null && getIntent().getSerializableExtra("course") != null){

            this.user = (User)getIntent().getSerializableExtra("user");
            this.course = (Course)getIntent().getSerializableExtra("course");
            this.quiz = (Quiz) getIntent().getSerializableExtra("quiz");

            this.profilTextView.setText(this.user.getDisplayName().toUpperCase().substring(0, 1));
            this.userTextView.setText(this.user.getDisplayName());
            if(this.quiz.getEditedAt() != null){
                String str = this.quiz.getPostedAt().toString() + " (Edited " + this.quiz.getEditedAt().toString() + ")";
                this.dateTextView.setText(str);
            }
            else{
                this.dateTextView.setText(this.quiz.getPostedAt().toString());
            }
            this.quizTextView.setText(this.quiz.getBody());


            reference = FirebaseDatabase.getInstance().getReference().child("Questions");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot1: snapshot.getChildren()){
                        Test p = dataSnapshot1.getValue(Test.class);
                        questions.add(p);
                    }
                    

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User)result;

                }

                @Override
                public void onFailed(String message) {

                }
            });


        }
        else{
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizFragment.this, CustomQuiz.class));
                finish();
            }
        });

        attempttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setMenuItems(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                break;
            case EDIT_ITEM:
                showEditQuizDialog();
                break;
            case DELETE_ITEM:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteQuiz();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quiz will be deleted. Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
                break;
        }
        return true;
    }

    private void deleteQuiz(){
        DatabaseUtility.getInstance().deleteCourseQuiz(course, quiz, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                String message = (String)result;
                makeToastMessage(message);
                finish();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
            }
        });
    }

    private void setMenuItems(final Menu menu){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;
                if(quiz.getPostedBy().equals(user.getKey())){
                    menu.add(0, EDIT_ITEM, 0, "Edit");
                    menu.add(0, DELETE_ITEM, 1, "Delete");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }


    private void showEditQuizDialog(){
        EditQuizDialog editQuizDialog = new EditQuizDialog(this, this.course, this.quiz);
        editQuizDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recreate();
            }
        });
        editQuizDialog.show();
    }

//    private void showAttemptTest(){
//        startActivity(new Intent(QuizFragment.this, AttemptTest.class));
//        finish();
//    }


    private void makeToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
