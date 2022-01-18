package com.example.cengonline.ui.home;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.User;
import com.example.cengonline.ui.course.CourseFragment;


import java.util.List;

public class HomeFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final LinearLayout lineaScrollLayout = (LinearLayout)view.findViewById(R.id.scroll_view_linear_layout);


        final User userUnknown = new User();
        userUnknown.setDisplayName("Unknown User");

        DatabaseUtility.getInstance().getAllCourses(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                final List<Course> courses = (List<Course>)result;
                lineaScrollLayout.removeAllViews();
                if(courses.size() == 0 && getActivity() != null){
                    TextView tv = new TextView(getActivity());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv.setLayoutParams(lp);
                    tv.setTextAppearance(getActivity(), R.style.fontForEmptyMessage);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tv.setText("You haven't joined any class!");
                    lineaScrollLayout.setGravity(Gravity.CENTER);
                    lineaScrollLayout.addView(tv, lp);
                }
                else{
                    lineaScrollLayout.setGravity(Gravity.TOP);
                }
                for(final Course course : courses){
                    DatabaseUtility.getInstance().getUser(course.getCreatedBy(), new DatabaseCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            User user = (User)result;
                            if(user != null){
                                try{
                                    drawCourse(lineaScrollLayout, course, user);
                                }
                                catch(NullPointerException ex){
                                    Log.w(ex.getClass().getName(), ex.getMessage());
                                }

                            }
                            else {
                                try{
                                    drawCourse(lineaScrollLayout, course, userUnknown);
                                }
                                catch (NullPointerException ex){
                                    Log.w(ex.getClass().getName(), ex.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailed(String message) {

                        }
                    });

                }
            }

            @Override
            public void onFailed(String message) {

            }
        });

        return view;
    }

    private void drawCourse(LinearLayout scroll, final Course course, User teacher){

        Utility util = Utility.getInstance();

        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setImageResource(course.getImageId());

        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams linearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearLayoutLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView courseName = new TextView(getActivity());
        LinearLayout.LayoutParams courseNameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        courseNameLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        courseNameLayoutParams.topMargin = util.DPtoPX(15, getActivity());
        courseName.setLayoutParams(courseNameLayoutParams);
        courseName.setLines(1);
        courseName.setEllipsize(TextUtils.TruncateAt.END);
        courseName.setTextAppearance(getActivity(), R.style.fontForCourseNameOnCard);
        courseName.setText(course.getClassName());

        TextView courseSection = new TextView(getActivity());
        LinearLayout.LayoutParams courseSectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        courseSectionLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        courseSection.setLayoutParams(courseSectionLayoutParams);
        courseSection.setTextAppearance(getActivity(), R.style.fontForCourseSectionOnCard);
        courseSection.setLines(1);
        courseSection.setEllipsize(TextUtils.TruncateAt.END);
        courseSection.setText(course.getClassSection());

        TextView courseTeacher = new TextView(getActivity());
        LinearLayout.LayoutParams courseTeacherLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        courseTeacherLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        courseTeacherLayoutParams.bottomMargin = util.DPtoPX(15, getActivity());
        courseTeacherLayoutParams.topMargin = util.DPtoPX(60, getActivity());
        courseTeacher.setLayoutParams(courseTeacherLayoutParams);
        courseTeacher.setLines(1);
        courseTeacher.setEllipsize(TextUtils.TruncateAt.END);
        courseTeacher.setTextAppearance(getActivity(), R.style.fontForCourseTeacherOnCard);
        courseTeacher.setText(teacher.getDisplayName());


        linearLayout.addView(courseName, courseNameLayoutParams);
        linearLayout.addView(courseSection, courseSectionLayoutParams);
        linearLayout.addView(courseTeacher, courseTeacherLayoutParams);

        CardView cardView = new CardView(getActivity());
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.topMargin = util.DPtoPX(7, getActivity());
        cardViewLayoutParams.leftMargin = util.DPtoPX(13, getActivity());
        cardViewLayoutParams.rightMargin = util.DPtoPX(13, getActivity());
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setClickable(true);
        cardView.setForeground(getSelectedItemDrawable());
        cardView.setRadius(util.DPtoPX(8, getActivity()));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CourseFragment.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });


        cardView.addView(imageView, imageViewLayoutParams);
        cardView.addView(linearLayout, linearLayoutLayoutParams);

        scroll.addView(cardView, cardViewLayoutParams);

    }

    private Drawable getSelectedItemDrawable() {
        int[] attrs = new int[] { R.attr.selectableItemBackground };
        TypedArray ta = getActivity().obtainStyledAttributes(attrs);
        Drawable selectedItemDrawable = ta.getDrawable(0);
        ta.recycle();
        return selectedItemDrawable;
    }


}
