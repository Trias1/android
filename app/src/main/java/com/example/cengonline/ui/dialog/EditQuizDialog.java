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
import com.example.cengonline.post.Announcement;
import com.example.cengonline.post.BaganCourse;
import com.example.cengonline.post.Quiz;

public class EditQuizDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button editButton;
    private EditText quizEditText;
    private Activity activity;
    private Course course;
    private Quiz quiz;

    public EditQuizDialog(Activity activity, Course course, Quiz quiz) {
        super(activity);
        this.activity = activity;
        this.course = course;
        this.quiz = quiz;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_quiz_dialog);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.editButton = (Button)findViewById(R.id.edit_quiz_update_button);
        this.cancelButton = (Button)findViewById(R.id.edit_quiz_cancel_button);
        this.quizEditText = (EditText)findViewById(R.id.edit_quiz_text);
        this.quizEditText.setText(quiz.getBody());

        this.editButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.edit_quiz_update_button: this.editButton.setEnabled(false); updateQuiz(); break;
            case R.id.edit_quiz_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void updateQuiz(){

        DatabaseUtility.getInstance().updateCourseQuiz(this.course, this.quiz, this.quizEditText.getText().toString(), new DatabaseCallback() {
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
                editButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
