package com.example.cengonline.ui.course;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.FileType;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Assignment;
import com.example.cengonline.ui.dialog.EditAssignmentDialog;
import com.example.cengonline.ui.dialog.UploadAssignmentDialog;


public class AssignmentFragment extends AppCompatActivity implements View.OnClickListener {

    private static final int DELETE_ITEM = 1000;
    private static final int EDIT_ITEM = 1001;
    private static final int UPLOAD_WORK = 1002;
    private static final int LIST_SUBMISSIONS = 1003;

    private Toolbar toolbar;
    private User user;
    private Course course;
    private Assignment assignment;
    private TextView assignmentTitle;
    private TextView assignmentDueDate;
    private TextView assignmentDescription;

    private UploadAssignmentDialog uaD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_assignment);

        this.toolbar = findViewById(R.id.course_toolbar);
        this.assignmentTitle = findViewById(R.id.assignment_title);
        this.assignmentDueDate = findViewById(R.id.assignment_due_date);
        this.assignmentDescription = findViewById(R.id.assignment_description);
        this.assignmentDescription.setFocusable(false);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent() != null && getIntent().getSerializableExtra("user") != null && getIntent().getSerializableExtra("assignment") != null && getIntent().getSerializableExtra("course") != null){

            this.user = (User)getIntent().getSerializableExtra("user");
            this.course = (Course)getIntent().getSerializableExtra("course");
            this.assignment = (Assignment) getIntent().getSerializableExtra("assignment");

            this.assignmentTitle.setText(this.assignment.getTitle());
            this.assignmentDescription.setText(this.assignment.getBody());
            this.assignmentDueDate.setText(this.assignment.getDueDate().toStringAssignmentDue());


            DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User)result;

                }

                @Override
                public void onFailed(String message) {

                }
            });


        }
        else{
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        /*NewAnnouncementDialog newAD = new NewAnnouncementDialog(this, this.course);
        newAD.show();*/
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
    public boolean onCreateOptionsMenu(Menu menu) {

        setMenuItems(menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                break;
            case EDIT_ITEM:
                showEditAssignmentDialog();
                break;
            case DELETE_ITEM:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteAssignment();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(assignment.getTitle() + " will be deleted. Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
                break;
            case UPLOAD_WORK:
                showUploadAssignmentDialog();
                break;
            case LIST_SUBMISSIONS:
                showAssignmentListFragment();
                break;
        }
        return true;
    }

    private void deleteAssignment(){
       DatabaseUtility.getInstance().deleteCourseAssignment(course, assignment, new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                String message = (String)result;
                makeToastMessage(message);
                finish();
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
            }
        });
    }

    private void showUploadAssignmentDialog(){
        this.uaD = new UploadAssignmentDialog(this, this.course, this.assignment);
        this.uaD.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recreate();
            }
        });
        this.uaD.show();
    }

    private void setMenuItems(final Menu menu){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;
                if(assignment.getPostedBy().equals(user.getKey())){
                    menu.add(0, EDIT_ITEM, 0, "Edit");
                    menu.add(0, DELETE_ITEM, 1, "Delete");
                    menu.add(0, LIST_SUBMISSIONS, 3, "List Submissions");
                }
                if(user.getRoles().contains(User.Role.STUDENT) && user.getRoles().size() == 1){
                    menu.add(0, UPLOAD_WORK, 2, "Upload Your Work");
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void showAssignmentListFragment(){

        Intent intent = new Intent(this, AssignmentListFragment.class);
        intent.putExtra("course", course);
        intent.putExtra("user", user);
        intent.putExtra("assignment", assignment);
        startActivity(intent);
    }

    private void showEditAssignmentDialog(){
        EditAssignmentDialog eaD = new EditAssignmentDialog(this, this.course, this.assignment);
        eaD.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recreate();
            }
        });
        eaD.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == FileType.PDF.getValue() || requestCode == FileType.TXT.getValue()) && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            this.uaD.updateContent(filePath);
        }

    }

    private void makeToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
