package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.MyTimestamp;
import com.example.cengonline.post.Assignment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAssignmentDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button editButton;
    private EditText assignmentTitleEditText;
    private EditText assignmentDescriptionEditText;
    private EditText assignmentDueEditText;
    private EditText assignmentDueTimeEditText;
    private Activity activity;
    private Course course;
    private Assignment assignment;

    public EditAssignmentDialog(Activity activity, Course course, Assignment assignment) {
        super(activity);
        this.activity = activity;
        this.course = course;
        this.assignment = assignment;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_assignment);
    }


    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.editButton = (Button)findViewById(R.id.edit_assignment_edit_button);
        this.cancelButton = (Button)findViewById(R.id.edit_assignment_cancel_button);
        this.assignmentTitleEditText = (EditText)findViewById(R.id.edit_assignment_title);
        this.assignmentDescriptionEditText = (EditText)findViewById(R.id.edit_assignment_description);
        this.assignmentDueEditText = (EditText)findViewById(R.id.edit_assignment_due);
        this.assignmentDueTimeEditText = (EditText)findViewById(R.id.edit_assignment_due_time);

        this.editButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.assignmentDueEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    goToDatePicker();
            }
        });
        this.assignmentDueTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    goToTimePicker();
            }
        });

        this.assignmentTitleEditText.setText(this.assignment.getTitle());
        this.assignmentDescriptionEditText.setText(this.assignment.getBody());
        this.assignmentDueEditText.setText(this.assignment.getDueDate().toStringDate());
        this.assignmentDueTimeEditText.setText(this.assignment.getDueDate().toStringTime());

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.edit_assignment_edit_button: this.editButton.setEnabled(false); editAssignment(); break;
            case R.id.edit_assignment_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void goToDatePicker(){

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String picked = this.assignmentDueEditText.getText().toString();

        try {
            if (!picked.equals("")) {
                String[] pars = picked.split("/");
                year = Integer.parseInt(pars[2]);
                month = Integer.parseInt(pars[0]) - 1;
                day = Integer.parseInt(pars[1]);
            }
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException ex){
            String str = ++month + "/" + day + "/" + "year";
            this.assignmentDueEditText.setText(str);
        }

        DatePickerDialog dpd = new DatePickerDialog(this.activity, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = ++month + "/" + dayOfMonth + "/" + year;
                assignmentDueEditText.setText(date);
                assignmentDueEditText.clearFocus();
            }
        }, year, month, day);

        dpd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if(assignmentDueEditText.getText().toString().equals("")) {
                    String date = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
                    assignmentDueEditText.setText(date);
                }
                assignmentDueEditText.clearFocus();
            }
        });
        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Pick", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
        dpd.show();
    }

    private void goToTimePicker(){

        int hour = 23;
        int minute = 59;

        String picked = this.assignmentDueTimeEditText.getText().toString();
        Log.w("asd", picked);

        try{
            if(!picked.equals("")){
                String[] pars = picked.split(":");
                hour = Integer.parseInt(pars[0]);
                minute = Integer.parseInt(pars[1]);
            }
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException ex){
            this.assignmentDueTimeEditText.setText("23:59");
        }

        TimePickerDialog tpd = new TimePickerDialog(this.activity, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.ENGLISH,"%02d:%02d", hourOfDay, minute);
                assignmentDueTimeEditText.setText(time);
                assignmentDueTimeEditText.clearFocus();
            }
        }, hour, minute, true);

        tpd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if(assignmentDueTimeEditText.getText().toString().equals("")){
                    assignmentDueTimeEditText.setText("23:59");
                }
                assignmentDueTimeEditText.clearFocus();
            }
        });

        tpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Pick", tpd);
        tpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", tpd);
        tpd.show();
    }

    private void editAssignment(){

        String title = assignmentTitleEditText.getText().toString();
        String description = assignmentDescriptionEditText.getText().toString();
        String due = assignmentDueEditText.getText().toString();
        String dueTime = assignmentDueTimeEditText.getText().toString();

        if(title.equals("")){
            makeToastMessage("Title can not be empty!");
            editButton.setEnabled(true);
            return;
        }
        if(due.equals("")){
            makeToastMessage("Due date can not be empty!");
            editButton.setEnabled(true);
            return;
        }

        int day = 0;
        int month = 0;
        int year = 0;
        int minute = 59;
        int hour = 23;

        try{
            String[] dues = due.split("/");
            month = Integer.parseInt(dues[0]) - 1;
            day = Integer.parseInt(dues[1]);
            year = Integer.parseInt(dues[2]);

            if(!dueTime.equals("")){
                String[] dueTimes = dueTime.split("\\:");
                hour = Integer.parseInt(dueTimes[0]);
                minute = Integer.parseInt(dueTimes[1]);
            }
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException ex){
            makeToastMessage(ex.getMessage());
            editButton.setEnabled(true);
            return;
        }

        MyTimestamp dueDate = new MyTimestamp(new Date(year- 1900, month, day, hour, minute));
        MyTimestamp postedAt = new MyTimestamp(new Date());

        this.assignment.setTitle(title);
        this.assignment.setDueDate(dueDate);
        this.assignment.setBody(description);

        DatabaseUtility.getInstance().updateCourseAssignments(this.course, this.assignment, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                String msg = (String) result;
                makeToastMessage(msg);
                dismiss();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
                editButton.setEnabled(true);
            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
