package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
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
import com.example.cengonline.model.Course;

public class NewPostDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button shareButton;
    private EditText postEditText;
    private Activity activity;
    private Course course;

    public NewPostDialog(Activity activity, Course course) {
        super(activity);
        this.activity = activity;
        this.course = course;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_post);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.shareButton = (Button)findViewById(R.id.share_post_share_button);
        this.cancelButton = (Button)findViewById(R.id.share_post_cancel_button);
        this.postEditText = (EditText)findViewById(R.id.share_post_text);

        this.shareButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.share_post_share_button: this.shareButton.setEnabled(false); postPost(); break;
            case R.id.share_post_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void postPost(){

        DatabaseUtility.getInstance().newCoursePost(this.course, this.postEditText.getText().toString(), new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                String msg = (String) result;
                makeToastMessage(msg);
                dismiss();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                postEditText.setText("");
                shareButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
