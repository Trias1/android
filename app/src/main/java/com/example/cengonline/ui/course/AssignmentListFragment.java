package com.example.cengonline.ui.course;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.MyTimestamp;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Assignment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class AssignmentListFragment extends AppCompatActivity implements View.OnClickListener {

    private static final int REFRESH_ITEM = 1000;
    private static final int DOWNLOAD_ALL = 1001;

    private Toolbar toolbar;
    private User user;
    private Course course;
    private Assignment assignment;
    private LinearLayout linearLayout;
    private ProgressDialog progressDialog;
    private List<FileEntity> files;
    private int dismissLimit;
    private int dismissCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_assignment_list);

        this.toolbar = findViewById(R.id.assignment_list_toolbar);
        this.linearLayout = findViewById(R.id.assignment_list_linear_layout);
        this.progressDialog = new ProgressDialog(this);
        this.files = new ArrayList<FileEntity>();

        this.toolbar.setTitle("Submissions");

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent() != null && getIntent().getSerializableExtra("user") != null && getIntent().getSerializableExtra("assignment") != null && getIntent().getSerializableExtra("course") != null){

            this.user = (User)getIntent().getSerializableExtra("user");
            this.course = (Course)getIntent().getSerializableExtra("course");
            this.assignment = (Assignment) getIntent().getSerializableExtra("assignment");


            progressDialog.setMessage("Fetching files...");
            progressDialog.show();
            getFiles();


        }
        else{
            finish();
        }
    }

    private void printFileNames(final FileEntity fileEntity){

        Utility util = Utility.getInstance();
        try{
            String[] parts = fileEntity.getFileName().split(" ");

            LinearLayout ll = new LinearLayout(this);
            LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setLayoutParams(llparams);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER);

            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(util.DPtoPX(230, this), ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = util.DPtoPX(10, this);
            tv.setLayoutParams(lp);
            tv.setText(parts[0]);
            tv.setGravity(Gravity.LEFT);
            tv.setTextAppearance(this, R.style.fontForDisplayNameOnCard);

            TextView tv2 = new TextView(this);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(util.DPtoPX(230, this), ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.topMargin = util.DPtoPX(10, this);
            lp2.bottomMargin = util.DPtoPX(10, this);
            tv2.setLayoutParams(lp2);
            tv2.setText(parts[1]);
            tv2.setGravity(Gravity.LEFT);
            tv2.setTextAppearance(this, R.style.fontForDisplayNameOnCard);

            TextView tv3 = new TextView(this);
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(util.DPtoPX(230, this), ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.bottomMargin = util.DPtoPX(10, this);
            tv3.setLayoutParams(lp3);
            tv3.setText(fileEntity.getCreationTime().superToString());
            tv3.setGravity(Gravity.LEFT);
            tv3.setTextAppearance(this, R.style.fontForDisplayNameOnCard);

            ll.addView(tv, lp);
            ll.addView(tv2, lp2);
            ll.addView(tv3, lp3);

            LinearLayout outerLinearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams outerLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            outerLinearLayout.setLayoutParams(outerLinearLayoutLayoutParams);
            outerLinearLayout.setGravity(Gravity.CENTER);
            outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            outerLinearLayout.addView(ll, llparams);

            ImageView downloadImage = new ImageView(this);
            LinearLayout.LayoutParams downloadImageLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(20, this), util.DPtoPX(20, this));
            //downloadImageLayoutParams.topMargin = util.DPtoPX(10, activity);
            downloadImageLayoutParams.rightMargin = util.DPtoPX(20, this);
            downloadImageLayoutParams.leftMargin = util.DPtoPX(10, this);
            downloadImage.setLayoutParams(downloadImageLayoutParams);
            downloadImage.setBackground(this.getResources().getDrawable(R.drawable.ic_direction));

            downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Downloading " + fileEntity.getFileName());
                    progressDialog.show();
                    downloadFile(fileEntity);
                }
            });

            ImageView deleteImage = new ImageView(this);
            LinearLayout.LayoutParams deleteImageLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(20, this), util.DPtoPX(20, this));
            // deleteImageLayoutParams.topMargin = util.DPtoPX(10, activity);
            deleteImage.setLayoutParams(deleteImageLayoutParams);
            deleteImage.setBackground(this.getResources().getDrawable(R.drawable.ic_iconmonstr_trash_can_30));



            outerLinearLayout.addView(downloadImage, downloadImageLayoutParams);
            outerLinearLayout.addView(deleteImage, deleteImageLayoutParams);

            final CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardViewLayoutParams.bottomMargin = util.DPtoPX(7, this);
            cardViewLayoutParams.leftMargin = util.DPtoPX(8, this);
            cardViewLayoutParams.rightMargin = util.DPtoPX(8, this);
            cardView.setLayoutParams(cardViewLayoutParams);
            cardView.setCardElevation(util.DPtoPX(2, this));
            cardView.setClickable(false);

            if(fileEntity.getCreationTime().before(assignment.getDueDate())){
                cardView.setBackground(getResources().getDrawable(R.drawable.layout_border_bottom_green));
            }
            else{
                cardView.setBackground(getResources().getDrawable(R.drawable.layout_border_bottom_red));
            }
            cardView.addView(outerLinearLayout, outerLinearLayoutLayoutParams);
            this.linearLayout.addView(cardView, cardViewLayoutParams);

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    progressDialog.setMessage("Deleting " + fileEntity.getFileName());
                                    progressDialog.show();
                                    deleteFile(fileEntity, cardView);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentListFragment.this);
                    builder.setMessage("You are deleting " + fileEntity.getFileName() + ". Are you sure?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener)
                            .show();
                }
            });

        }
        catch(Exception ex){
            Log.w("Err", "Something went wrong while getting files");
        }
    }

    private void getFiles(){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode());
                storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        dismissLimit = task.getResult().getItems().size();
                        for(StorageReference item: task.getResult().getItems()){
                            item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    FileEntity fileEntity = new FileEntity(storageMetadata.getName(), new MyTimestamp(new Date(storageMetadata.getCreationTimeMillis())));
                                    printFileNames(fileEntity);
                                    files.add(fileEntity);
                                    dismissCounter++;
                                    if(dismissCounter == dismissLimit){
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                        if(task.getResult().getItems().size() == 0 ){
                            TextView tv = new TextView(AssignmentListFragment.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.topMargin = Utility.getInstance().DPtoPX(20, AssignmentListFragment.this);
                            tv.setLayoutParams(lp);
                            tv.setTextAppearance(AssignmentListFragment.this, R.style.fontForEmptyMessage);
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tv.setText("There aren't any submissions!");
                            linearLayout.addView(tv, lp);
                            progressDialog.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void deleteFile(final FileEntity file, final CardView cardView){


        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode() + "/" + file.getFileName());
        storageReference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        makeToastMessage(file.getFileName() + " has been deleted successfully!");
                        linearLayout.removeView(cardView);
                        progressDialog.dismiss();
                    }
                });

    }


    private void downloadFile(final FileEntity file){

        try {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode() + "/" + file.getFileName());
            ref.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadManager(file.getFileName(), DIRECTORY_DOWNLOADS, uri.toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToastMessage(e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
        }
        catch (Exception ex){
            makeToastMessage(ex.getMessage());
        }

    }

    private void downloadManager(String fileName, String destinationDirectory, String url){


        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, destinationDirectory, url);


        downloadManager.enqueue(request);
        progressDialog.dismiss();
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
            case REFRESH_ITEM:
                recreate();
                break;
            case DOWNLOAD_ALL:
                downloadAllFiles();
            break;
        }
        return true;
    }

    private void downloadAllFiles(){
        for(FileEntity fileEntity : this.files){
            progressDialog.setMessage("Downloading " + fileEntity.getFileName());
            progressDialog.show();
            downloadFile(fileEntity);
        }
    }


    private void setMenuItems(final Menu menu){

        menu.add(0, REFRESH_ITEM, 0, "Refresh");
        menu.add(0, DOWNLOAD_ALL, 1, "Download All");
    }

    private void makeToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private static class FileEntity{

        private String fileName;
        private MyTimestamp creationTime;

        public FileEntity(String fileName, MyTimestamp creationTime) {
            this.fileName = fileName;
            this.creationTime = creationTime;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public MyTimestamp getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(MyTimestamp creationTime) {
            this.creationTime = creationTime;
        }
    }
}
