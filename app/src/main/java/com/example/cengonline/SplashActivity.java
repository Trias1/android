package com.example.cengonline;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cengonline.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private User user;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5782BB"), PorterDuff.Mode.SRC_IN);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            setUserFromFirebaseUser();
        }
        else{
            launchLoginActivity();
        }
    }

    private void launchMainActivity() {
        MainActivity.startActivity(this, user);
        finish();

    }

    private void launchLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUserFromFirebaseUser(){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                user = (User)result;
                launchMainActivity();
            }

            @Override
            public void onFailed(String message) {
                launchLoginActivity();
            }
        });
    }
}
