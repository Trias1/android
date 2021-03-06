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
import com.example.cengonline.ui.course.BaganCourseTemplate;

public class NewQuizDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button shareButton;
    private EditText quizEditText;
    private Activity activity;
    private Course course;

////    public NewAnnouncementDialog(Activity activity, Course course) {
////        super(activity);
////        this.activity = activity;
////        this.course = course;
//
//    }

    public NewQuizDialog(Activity activity, Course course) {
        super(activity);
        this.activity = activity;
        this.course = course;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_quiz_dialog);
    }


    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.shareButton = (Button)findViewById(R.id.share_quiz_share_button);
        this.cancelButton = (Button)findViewById(R.id.share_quiz_cancel_button);
        this.quizEditText = (EditText)findViewById(R.id.share_quiz_text);

        this.shareButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.share_quiz_share_button: this.shareButton.setEnabled(false); postQuiz(); break;
            case R.id.share_quiz_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void postQuiz(){

        DatabaseUtility.getInstance().newCourseQuiz(this.course, this.quizEditText.getText().toString(), new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                String msg = (String) result;
                makeToastMessage(msg);
                dismiss();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                quizEditText.setText("");
                shareButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
