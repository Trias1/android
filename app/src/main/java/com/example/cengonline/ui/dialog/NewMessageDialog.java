package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.Conversation;
import com.example.cengonline.model.User;
import com.example.cengonline.ui.message.SpecialMessageFragment;
import com.google.firebase.auth.FirebaseAuth;


public class NewMessageDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button startButton;
    private EditText emailEditText;
    private Activity activity;

    public NewMessageDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_message);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.startButton = (Button)findViewById(R.id.new_message_start_button);
        this.cancelButton = (Button)findViewById(R.id.new_message_cancel_button);
        this.emailEditText = (EditText)findViewById(R.id.new_message_email);

        this.startButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_message_start_button: this.startButton.setEnabled(false); readUser();break;
            case R.id.new_message_cancel_button: dismiss(); break;
            default: break;
        }
    }

    public void readUser(){

        String email = this.emailEditText.getText().toString();
        if(email.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            makeToastMessage("You can not send message yourself!");
            dismiss();
            return;
        }


        DatabaseUtility.getInstance().getUserFromEmail(email, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final User user = (User)result;

                DatabaseUtility.getInstance().getConversations(user, new DatabaseCallback() {
                    @Override
                    public void onSuccess(Object result) {

                        if(result != null){
                            Conversation conversation = (Conversation)result;
                            dismiss();
                            goToMessage(user, conversation.getKey());
                        }
                        else{
                            dismiss();
                            goToMessage(user, null);
                        }
                    }

                    @Override
                    public void onFailed(String message) {

                    }
                });
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                startButton.setEnabled(true);
            }
        });
    }

    private void goToMessage(User user, String conversationKey) {

        Intent intent = new Intent(activity, SpecialMessageFragment.class);
        intent.putExtra("sendUser", user);
        if (conversationKey != null){
            intent.putExtra("conversationKey", conversationKey);
        }

        activity.startActivity(intent);
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
