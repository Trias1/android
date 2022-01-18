package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.User;


public class NewClassDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button createButton;
    private EditText classNameEditText;
    private EditText classSectionEditText;
    private EditText classSubjectEditText;
    private Activity activity;

    public NewClassDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_class);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.createButton = (Button)findViewById(R.id.new_class_create_button);
        this.cancelButton = (Button)findViewById(R.id.new_class_cancel_button);
        this.classNameEditText = (EditText)findViewById(R.id.new_class_class_name);
        this.classSectionEditText = (EditText)findViewById(R.id.new_class_class_section);
        this.classSubjectEditText = (EditText)findViewById(R.id.new_class_class_subject);

        this.createButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_class_create_button: this.createButton.setEnabled(false); createNewClass();break;
            case R.id.new_class_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void createNewClass(){

        final String courseName = classNameEditText.getText().toString();
        final String courseSection = classSectionEditText.getText().toString();
        final String courseSubject = classSubjectEditText.getText().toString();

        if(TextUtils.isEmpty(courseName) || courseName.length() < 5){
            Toast.makeText(this.activity, "Class name should be at least 5 characters!", Toast.LENGTH_LONG).show();
            this.createButton.setEnabled(true);
            return;
        }

        DatabaseUtility.getInstance().getRandomClassCode(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final String classCode = (String)result;
                DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        User user = (User)result;

                        if(user.getRoles().contains(User.Role.TEACHER)){
                            DatabaseUtility.getInstance().saveNewCourse(courseName, courseSection, courseSubject, classCode, user);
                            makeToastMessage("Class has been created with class code: " + classCode);
                            dismiss();
                        }
                        else{
                            makeToastMessage("An error occurred try again please!");
                            createButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailed(String message) {
                        makeToastMessage(message);
                        createButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                createButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
