package com.example.cengonline.ui.course;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.Comment;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CommentFragment extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private User user;
    private Course course;
    private Post post;
    private EditText commentText;
    private FloatingActionButton sendFab;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private List<CardView> commentViews;
    private Map<Comment, User> commentCache;
    private int commentNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_comment);
        this.commentViews = new ArrayList<CardView>();
        this.commentCache = new TreeMap<Comment, User>();
        this.toolbar = findViewById(R.id.comment_toolbar);
        this.commentText = findViewById(R.id.new_comment);
        this.sendFab = findViewById(R.id.send_comment_fab);
        this.linearLayout = findViewById(R.id.scroll_view_linear_layout);
        this.scrollView = findViewById(R.id.scroll_view);


        if(getIntent() != null && getIntent().getSerializableExtra("user") != null && getIntent().getSerializableExtra("post") != null && getIntent().getSerializableExtra("course") != null){
            this.user = (User)getIntent().getSerializableExtra("user");
            this.course = (Course)getIntent().getSerializableExtra("course");
            this.post = (Post)getIntent().getSerializableExtra("post");
            this.toolbar.setTitle("Class Comments");
            setSupportActionBar(this.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);



            this.sendFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(commentText.getText().toString().equals("")){
                        return;
                    }
                    Comment comment = new Comment(null, null, commentText.getText().toString());
                    commentText.setText("");
                    DatabaseUtility.getInstance().newComment(course, post, comment, new DatabaseCallback() {
                        @Override
                        public void onSuccess(Object result) {

                        }

                        @Override
                        public void onFailed(String message) {
                            makeToastMessage(message);
                        }
                    });
                }
            });

            getComments();

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
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    private void drawComment(Comment comment, User user){
        Utility util = Utility.getInstance();

        TextView imageText = new TextView(this);
        LinearLayout.LayoutParams imageTextLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(40, this), util.DPtoPX(40, this));
        imageText.setLayoutParams(imageTextLayoutParams);
        imageText.setTextAppearance(this, R.style.fontForImageTextOnCard);
        imageText.setBackgroundResource(R.drawable.rounded_textview);
        imageText.setText(String.valueOf(user.getDisplayName().toUpperCase().charAt(0)));
        imageText.setGravity(Gravity.CENTER);

        TextView bodyText = new TextView(this);
        LinearLayout.LayoutParams bodyTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyTextLayoutParams.leftMargin = util.DPtoPX(10, this);
        bodyText.setTextAppearance(this, R.style.fontForBodyTextOnCard);
        bodyText.setText(comment.getBody());

        TextView displayNameText = new TextView(this);
        LinearLayout.LayoutParams displayNameTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        displayNameTextLayoutParams.weight = 0;
        displayNameTextLayoutParams.leftMargin = util.DPtoPX(10, this);
        displayNameTextLayoutParams.rightMargin = util.DPtoPX(10, this);
        displayNameText.setLayoutParams(displayNameTextLayoutParams);
        displayNameText.setMaxWidth(util.DPtoPX(240, this));
        displayNameText.setTextAppearance(this, R.style.fontForDisplayNameOnComment);
        displayNameText.setText(user.getDisplayName());

        TextView dateText = new TextView(this);
        LinearLayout.LayoutParams dateTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateTextLayoutParams.weight = 1;
        dateTextLayoutParams.rightMargin = util.DPtoPX(10, this);
        dateText.setLayoutParams(dateTextLayoutParams);
        dateText.setTextAppearance(this, R.style.fontForDateOnCard);
        dateText.setText(comment.getSentAt().toStringTime());


        LinearLayout middleLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams middleLinarLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        middleLinarLayoutLayoutParams.bottomMargin = util.DPtoPX(4, this);

        middleLinearLayout.setLayoutParams(middleLinarLayoutLayoutParams);
        middleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        middleLinearLayout.setGravity(Gravity.TOP);



        LinearLayout outerLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams outerLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLayoutParams);
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        outerLinearLayout.setGravity(Gravity.TOP);

        LinearLayout mostOuterLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams mostOuterLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mostOuterLinearLayout.setLayoutParams(mostOuterLinearLayoutLayoutParams);
        mostOuterLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mostOuterLinearLayout.setGravity(Gravity.TOP);

        middleLinearLayout.addView(displayNameText, displayNameTextLayoutParams);
        middleLinearLayout.addView(dateText, dateTextLayoutParams);

        outerLinearLayout.addView(middleLinearLayout, middleLinarLayoutLayoutParams);
        outerLinearLayout.addView(bodyText, bodyTextLayoutParams);

        mostOuterLinearLayout.addView(imageText, imageTextLayoutParams);
        mostOuterLinearLayout.addView(outerLinearLayout, outerLinearLayoutLayoutParams);

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.topMargin = util.DPtoPX(13, this);
        cardViewLayoutParams.leftMargin = util.DPtoPX(8, this);
        cardViewLayoutParams.rightMargin = util.DPtoPX(8, this);
        cardViewLayoutParams.bottomMargin = util.DPtoPX(13, this);
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setClickable(true);
        cardView.setRadius(util.DPtoPX(8, this));
        cardView.setElevation(0);


        cardView.addView(mostOuterLinearLayout, mostOuterLinearLayoutLayoutParams);
        commentViews.add(cardView);

        this.linearLayout.addView(cardView, cardViewLayoutParams);

    }

    private void drawComments(){

        for(Map.Entry<Comment, User> entry : commentCache.entrySet()){

            drawComment(entry.getKey(), entry.getValue());
        }
    }

    private void getComments(){

        DatabaseUtility.getInstance().getComments(this.course, this.post, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                if(result == null) return;
                List<Comment> comments = (List<Comment>)result;
                commentNumber = comments.size();
                linearLayout.removeAllViews();
                commentCache.clear();
                for(final Comment comment : comments){
                    DatabaseUtility.getInstance().getUser(comment.getSenderKey(), new DatabaseCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            User user = (User)result;
                            if(user != null){
                                commentCache.put(comment, user);
                                if(commentCache.size() == commentNumber){
                                    drawComments();
                                }
                            }
                        }

                        @Override
                        public void onFailed(String message) {

                        }
                    });


                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
