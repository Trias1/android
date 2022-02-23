package com.example.cengonline.ui.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.Activity;
import android.app.Dialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cengonline.R;
import com.example.cengonline.model.Course;
import com.example.cengonline.ui.dialog.NewAnnouncementDialog;
import com.example.cengonline.ui.dialog.NewAssignmentDialog;
import com.example.cengonline.ui.dialog.NewPostDialog;
import com.example.cengonline.ui.dialog.NewQuizDialog;

public class BaganCourseTemplate extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Course course;
    private CardView newquiz;
    private CardView newannoun;
    private Intent noun;
    private Intent quez;

    protected BaganCourseTemplate(Activity activity, Course course){
        super(activity);
        this.activity = activity;
        this.course = course;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bagan_course);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.newquiz =findViewById(R.id.quiz_new);
        this.newannoun =findViewById(R.id.announcement_new);
        this.newquiz.setOnClickListener(this);
        this.newannoun.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
  //      int id = view.getId();
//        switch (id){
//            case R.id.announcement_new:
//                NewAnnouncementDialog newPoD = new NewAnnouncementDialog(this, this.course);
//                newPoD.show();
//                break;
//            case R.id.quiz_new:
//                NewQuizDialog newPdD = new NewQuizDialog(this, this.course);
//                newPdD.show();
//                break;

//            case R.id.share_announcement_card:
//                BaganCourseTemplate newAnD = new BaganCourseTemplate(this, this.course);
//                newAnD.show();
//                break;
//            case R.id.share_assignment_card:
//                NewAssignmentDialog newAsD = new NewAssignmentDialog(this, this.course);
//                newAsD.show();
//                break;
 //       }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}