package com.example.cengonline.ui.message;

import android.graphics.Color;
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
import com.example.cengonline.model.Conversation;
import com.example.cengonline.model.Message;
import com.example.cengonline.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SpecialMessageFragment extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private User sendUser;
    private EditText messageText;
    private FloatingActionButton sendFab;
    private String conversationKey;
    private List<Message> messages;
    private LinearLayout linearLayout;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        this.toolbar = findViewById(R.id.message_toolbar);
        this.messageText = findViewById(R.id.new_message);
        this.sendFab = findViewById(R.id.send_message_fab);
        this.linearLayout = findViewById(R.id.scroll_view_linear_layout);
        this.scrollView = findViewById(R.id.scroll_view);
        this.messages = new ArrayList<Message>();

        if(getIntent() != null && getIntent().getSerializableExtra("sendUser") != null){
            this.sendUser = (User)getIntent().getSerializableExtra("sendUser");
            this.toolbar.setTitle(this.sendUser.getDisplayName());
            setSupportActionBar(this.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            this.sendFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(messageText.getText().toString().equals("")){
                        return;
                    }
                    Message message = new Message(null, null, messageText.getText().toString());
                    messageText.setText("");
                    DatabaseUtility.getInstance().newMessage(conversationKey, message, new DatabaseCallback() {
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

            if(getIntent().getStringExtra("conversationKey") != null){
                this.conversationKey = getIntent().getStringExtra("conversationKey");
                getMessages();
            }
            else{
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("conversations");
                final DatabaseReference newVal = ref.push();
                DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        User user = (User) result;
                        Conversation conversation = new Conversation(newVal.getKey(), user, sendUser, new ArrayList<Message>());
                        ref.child(conversation.getKey()).setValue(conversation);
                        conversationKey = conversation.getKey();
                        getMessages();
                    }

                    @Override
                    public void onFailed(String message) {

                    }
                });
            }
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

    private void getDiff(List<Message> newMessages){

        for(Message message : newMessages){
            if(!this.messages.contains(message)){
                this.messages.add(message);
                drawMessage(message);
            }
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },500);
        }
    }

    private void drawMessage(Message message){
        Utility util = Utility.getInstance();


        TextView displayNameText = new TextView(this);
        LinearLayout.LayoutParams displayNameTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        displayNameTextLayoutParams.weight = 0;
        displayNameTextLayoutParams.leftMargin = util.DPtoPX(10, this);
        displayNameTextLayoutParams.rightMargin = util.DPtoPX(10, this);
        displayNameText.setLayoutParams(displayNameTextLayoutParams);
        displayNameText.setMaxWidth(util.DPtoPX(240, this));
        displayNameText.setTextAppearance(this, R.style.fontForDisplayNameOnCard);
        displayNameText.setText(message.getBody());

        TextView dateText = new TextView(this);
        LinearLayout.LayoutParams dateTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateTextLayoutParams.weight = 1;
        dateText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        dateTextLayoutParams.rightMargin = util.DPtoPX(10, this);
        dateTextLayoutParams.leftMargin = util.DPtoPX(10, this);
        dateText.setLayoutParams(dateTextLayoutParams);
        dateText.setTextAppearance(this, R.style.fontForDateOnCard);
        dateText.setText(message.getSentAt().toStringTime());



        LinearLayout middleLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams middleLinarLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        middleLinarLayoutLayoutParams.topMargin = util.DPtoPX(10, this);
        middleLinarLayoutLayoutParams.bottomMargin = util.DPtoPX(10, this);

        middleLinearLayout.setLayoutParams(middleLinarLayoutLayoutParams);
        middleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        middleLinearLayout.setGravity(Gravity.TOP);



        LinearLayout outerLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams outerLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLayoutParams);
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);

        if(message.getSenderKey().equals(sendUser.getKey())){
            middleLinearLayout.addView(dateText, dateTextLayoutParams);
            middleLinearLayout.addView(displayNameText, displayNameTextLayoutParams);
        }
        else{
            middleLinearLayout.addView(displayNameText, displayNameTextLayoutParams);
            middleLinearLayout.addView(dateText, dateTextLayoutParams);
        }

        outerLinearLayout.addView(middleLinearLayout, middleLinarLayoutLayoutParams);


        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.bottomMargin = util.DPtoPX(7, this);
        cardViewLayoutParams.leftMargin = util.DPtoPX(8, this);
        cardViewLayoutParams.rightMargin = util.DPtoPX(8, this);
        cardView.setLayoutParams(cardViewLayoutParams);

        if(message.getSenderKey().equals(sendUser.getKey())){
            cardViewLayoutParams.gravity = Gravity.LEFT;
        }
        else{
            cardViewLayoutParams.gravity = Gravity.RIGHT;
            cardView.setBackgroundColor(Color.parseColor("#DEFAC7"));
        }

        cardView.setClickable(true);



        cardView.addView(outerLinearLayout, outerLinearLayoutLayoutParams);

        this.linearLayout.addView(cardView, cardViewLayoutParams);

    }

    private void getMessages(){

        DatabaseUtility.getInstance().getConversationWithKey(conversationKey, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                Conversation conversation = (Conversation)result;
                getDiff(conversation.getMessages());
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
