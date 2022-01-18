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
import com.example.cengonline.model.Course;
import com.example.cengonline.model.User;
import com.google.firebase.auth.FirebaseAuth;


public class EditClassDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button updateButton;
    private EditText classNameEditText;
    private EditText classSectionEditText;
    private EditText classSubjectEditText;
    private Activity activity;
    private Course course;

    public EditClassDialog(Activity activity, Course course) {
        super(activity);
        this.activity = activity;
        this.course = course;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_class);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.updateButton = (Button)findViewById(R.id.edit_class_create_button);
        this.cancelButton = (Button)findViewById(R.id.edit_class_cancel_button);
        this.classNameEditText = (EditText)findViewById(R.id.edit_class_class_name);
        this.classSectionEditText = (EditText)findViewById(R.id.edit_class_class_section);
        this.classSubjectEditText = (EditText)findViewById(R.id.edit_class_class_subject);

        this.classNameEditText.setText(course.getClassName());
        this.classSectionEditText.setText(course.getClassSection());
        this.classSubjectEditText.setText(course.getClassSubject());

        this.updateButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.edit_class_create_button: this.updateButton.setEnabled(false); createNewClass();break;
            case R.id.edit_class_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void createNewClass(){

        final String courseName = classNameEditText.getText().toString();
        final String courseSection = classSectionEditText.getText().toString();
        final String courseSubject = classSubjectEditText.getText().toString();

        if(TextUtils.isEmpty(courseName) || courseName.length() < 5){
            Toast.makeText(this.activity, "Class name should be at least 5 characters!", Toast.LENGTH_LONG).show();
            this.updateButton.setEnabled(true);
            return;
        }

        this.course.setClassName(courseName);
        this.course.setClassSection(courseSection);
        this.course.setClassSubject(courseSubject);


        DatabaseUtility.getInstance().getUser(course.getCreatedBy(), new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;

                if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    DatabaseUtility.getInstance().updateCourse(course, new DatabaseCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            String res = (String)result;
                            makeToastMessage(res);
                            dismiss();
                        }

                        @Override
                        public void onFailed(String message) {

                        }
                    });

                }
                else{
                    makeToastMessage("An error occurred try again please!");
                    updateButton.setEnabled(true);
                }
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                updateButton.setEnabled(true);
            }
        });



    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
