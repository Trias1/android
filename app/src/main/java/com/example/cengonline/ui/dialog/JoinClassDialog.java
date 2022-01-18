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



public class JoinClassDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button joinButton;
    private EditText classCodeEditText;
    private Activity activity;

    public JoinClassDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_class);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.joinButton = (Button)findViewById(R.id.join_class_join_button);
        this.cancelButton = (Button)findViewById(R.id.join_class_cancel_button);
        this.classCodeEditText = (EditText)findViewById(R.id.join_class_class_code);

        this.joinButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.join_class_join_button: this.joinButton.setEnabled(false); joinClass(); break;
            case R.id.join_class_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void joinClass(){

        String classCode = this.classCodeEditText.getText().toString();
        DatabaseUtility.getInstance().addStudentToCourse(classCode, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                makeToastMessage((String)result);
                dismiss();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage((String)message);
                classCodeEditText.setText("");
                joinButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
