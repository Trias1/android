package com.example.cengonline.ui.course;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Post;
import com.example.cengonline.ui.dialog.EditPostDialog;


public class PostFragment extends AppCompatActivity implements View.OnClickListener {

    private static final int DELETE_ITEM = 1000;
    private static final int EDIT_ITEM = 1001;

    private Toolbar toolbar;
    private User user;
    private Course course;
    private Post post;
    private TextView profilTextView;
    private TextView userTextView;
    private TextView dateTextView;
    private TextView postTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);

        this.toolbar = findViewById(R.id.course_toolbar);
        this.profilTextView = findViewById(R.id.post_detail_profil);
        this.userTextView = findViewById(R.id.post_detail_user);
        this.dateTextView = findViewById(R.id.post_detail_date);
        this.postTextView = findViewById(R.id.post_detail_body);
        this.postTextView.setFocusable(false);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent() != null && getIntent().getSerializableExtra("user") != null && getIntent().getSerializableExtra("post") != null && getIntent().getSerializableExtra("course") != null){

            this.user = (User)getIntent().getSerializableExtra("user");
            this.course = (Course)getIntent().getSerializableExtra("course");
            this.post = (Post)getIntent().getSerializableExtra("post");

            this.profilTextView.setText(this.user.getDisplayName().toUpperCase().substring(0, 1));
            this.userTextView.setText(this.user.getDisplayName());
            if(this.post.getEditedAt() != null){
                String str = this.post.getPostedAt().toString() + " (Edited " + this.post.getEditedAt().toString() + ")";
                this.dateTextView.setText(str);
            }
            else{
                this.dateTextView.setText(this.post.getPostedAt().toString());
            }
            this.postTextView.setText(this.post.getBody());

        }
        else{
            finish();
        }
    }


    @Override
    public void onClick(View v) {

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
                showEditAnnouncementDialog();
                break;
            case DELETE_ITEM:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteAnnouncement();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Post will be deleted. Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
                break;
        }
        return true;
    }

    private void deleteAnnouncement(){
        DatabaseUtility.getInstance().deleteCoursePost(course, post, new DatabaseCallback() {
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
                if(post.getPostedBy().equals(user.getKey())){
                    menu.add(0, EDIT_ITEM, 0, "Edit");
                    menu.add(0, DELETE_ITEM, 1, "Delete");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void showEditAnnouncementDialog(){
        EditPostDialog epD = new EditPostDialog(this, this.course, this.post);
        epD.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recreate();
            }
        });
        epD.show();
    }



    private void makeToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
