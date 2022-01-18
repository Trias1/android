package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.Course;
import com.example.cengonline.model.FileType;
import com.example.cengonline.model.User;
import com.example.cengonline.post.Assignment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class UploadAssignmentDialog extends Dialog implements View.OnClickListener {

    private Button cancelButton;
    private Button uploadPdfButton;
    private Button uploadTxtButton;
    private EditText fileNameEditText;
    private Activity activity;
    private Assignment assignment;
    private LinearLayout fileNamesLinearLayout;
    private Course course;
    private ArrayList<File> files;
    private ProgressDialog progressDialog;


    public UploadAssignmentDialog(Activity activity, Course course, Assignment assignment) {
        super(activity);
        this.activity = activity;
        this.assignment = assignment;
        this.course = course;
        this.progressDialog = new ProgressDialog(activity);
        this.files = new ArrayList<File>();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload_assignment);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.uploadPdfButton = (Button)findViewById(R.id.upload_assignment_upload_pdf);
        this.uploadTxtButton = (Button)findViewById(R.id.upload_assignment_upload_txt);
        this.cancelButton = (Button)findViewById(R.id.upload_assignment_cancel_button);
        this.fileNameEditText = (EditText)findViewById(R.id.file_name);
        this.fileNamesLinearLayout = (LinearLayout)findViewById(R.id.file_names_linear_layout);


        this.uploadPdfButton.setOnClickListener(this);
        this.uploadTxtButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);

        getFiles();

    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.upload_assignment_upload_pdf: uploadPdf(); break;
            case R.id.upload_assignment_upload_txt: uploadTxt(); break;
            case R.id.upload_assignment_cancel_button: dismiss(); break;
            default: break;
        }
    }

    public void updateContent(Uri filePath){
        File file = new File(filePath,FirebaseAuth.getInstance().getCurrentUser().getEmail() + " " + this.fileNameEditText.getText().toString());
        progressDialog.setMessage("Uploading file, please wait.");
        progressDialog.show();
        uploadFile(file);
        this.fileNameEditText.setText("");

    }

    private void uploadPdf(){
        if(fileNameEditText.getText().toString().equals("")){
            makeToastMessage("Filename can not be empty!");
            return;
        }
        else if(!fileNameEditText.getText().toString().matches("^\\S*$")){
            makeToastMessage("Filename can not include whitespaces!");
            return;
        }
        this.fileNameEditText.setText(String.valueOf(this.fileNameEditText.getText().toString() + ".pdf"));
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.activity.startActivityForResult(Intent.createChooser(intent, "Select PDF"), FileType.PDF.getValue());
    }

    private void uploadTxt(){
        if(fileNameEditText.getText().toString().equals("")){
            makeToastMessage("Filename can not be empty!");
            return;
        }
        else if(!fileNameEditText.getText().toString().matches("^\\S*$")){
            makeToastMessage("Filename can not include whitespaces!");
            return;
        }
        this.fileNameEditText.setText(String.valueOf(this.fileNameEditText.getText().toString() + ".txt"));
        Intent intent = new Intent();
        intent.setType("application/word");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.activity.startActivityForResult(Intent.createChooser(intent, "Select PDF"), FileType.TXT.getValue());
    }

    private void printFileNames(){

        Utility util = Utility.getInstance();
        this.fileNamesLinearLayout.removeAllViews();
        for(final File file : this.files){
            try{
                String[] parts = file.getFileName().split(" ");
                if(parts[0].equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){

                    TextView tv = new TextView(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(util.DPtoPX(170, activity), ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = util.DPtoPX(10, activity);
                    lp.bottomMargin = util.DPtoPX(10, activity);
                    tv.setLayoutParams(lp);
                    tv.setText(parts[1]);
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextAppearance(getContext(), R.style.fontForDisplayNameOnCard);


                    LinearLayout outerLinearLayout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams outerLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLayoutParams);
                    outerLinearLayout.setGravity(Gravity.CENTER);
                    outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    outerLinearLayout.addView(tv, lp);

                    ImageView downloadImage = new ImageView(getContext());
                    LinearLayout.LayoutParams downloadImageLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(20, activity), util.DPtoPX(20, activity));
                    //downloadImageLayoutParams.topMargin = util.DPtoPX(10, activity);
                    downloadImageLayoutParams.rightMargin = util.DPtoPX(15, activity);
                    downloadImage.setLayoutParams(downloadImageLayoutParams);
                    downloadImage.setBackground(getContext().getResources().getDrawable(R.drawable.ic_direction));

                    downloadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.setMessage("Downloading file, please wait.");
                            progressDialog.show();
                            downloadFile(file);
                        }
                    });

                    ImageView deleteImage = new ImageView(getContext());
                    LinearLayout.LayoutParams deleteImageLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(20, activity), util.DPtoPX(20, activity));
                   // deleteImageLayoutParams.topMargin = util.DPtoPX(10, activity);
                    deleteImage.setLayoutParams(deleteImageLayoutParams);
                    deleteImage.setBackground(getContext().getResources().getDrawable(R.drawable.ic_iconmonstr_trash_can_30));

                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            progressDialog.setMessage("Deleting " + file.getFileName());
                                            progressDialog.show();
                                            deleteFile(file);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("You are deleting " + file.getFileName() + ". Are you sure?")
                                    .setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .show();
                        }
                    });

                    outerLinearLayout.addView(downloadImage, downloadImageLayoutParams);
                    outerLinearLayout.addView(deleteImage, deleteImageLayoutParams);

                    CardView cardView = new CardView(getContext());
                    LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardViewLayoutParams.bottomMargin = util.DPtoPX(7, activity);
                    cardViewLayoutParams.leftMargin = util.DPtoPX(8, activity);
                    cardViewLayoutParams.rightMargin = util.DPtoPX(8, activity);
                    cardView.setLayoutParams(cardViewLayoutParams);
                    cardView.setCardElevation(util.DPtoPX(2, activity));
                    cardView.setClickable(true);

                    cardView.addView(outerLinearLayout, outerLinearLayoutLayoutParams);
                    this.fileNamesLinearLayout.addView(cardView, cardViewLayoutParams);
                }


            }
            catch(Exception ex){
                Log.w("Err", "Something went wrong while getting files");
            }

        }
    }

    private void uploadFile(final File file){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;

                    if(file.getFilePath() != null){
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode() + "/" + file.fileName);
                        storageReference.putFile(file.getFilePath())
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        makeToastMessage("File has been uploaded successfully!");
                                        files.add(file);
                                        printFileNames();
                                        progressDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        makeToastMessage(e.getMessage());
                                    }
                                });
                    }

            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void deleteFile(final File file){

        DatabaseUtility.getInstance().getUser(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User)result;
                try{
                    String[] parts = file.getFileName().split(" ");
                    if(!parts[0].equals(user.getEmail())){
                        throw new Exception();
                    }
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode() + "/" + file.getFileName());
                    storageReference.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    makeToastMessage("File has been deleted successfully!");
                                    files.remove(file);
                                    printFileNames();
                                    progressDialog.dismiss();
                                }
                            });
                }
                catch(Exception ex){
                    makeToastMessage("An error occurred while deleting file!");
                }
            }

            @Override
            public void onFailed(String message) {
                makeToastMessage("An error occurred while deleting file!");
            }
        });
    }


    private void downloadFile(final File file){

        try {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(course.getKey() + "/" + assignment.getPostedAt().hashCode() + "/" + file.getFileName());
            ref.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadManager(activity, file.getFileName(), DIRECTORY_DOWNLOADS, uri.toString());

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

    private void downloadManager(Context context, String fileName, String destinationDirectory, String url){


        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, url);


        downloadManager.enqueue(request);
        makeToastMessage("Download request has been added the queue!");
        progressDialog.dismiss();
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
                        for(StorageReference item: task.getResult().getItems()){
                            files.add(new File(null, item.getName()));
                        }
                        printFileNames();
                    }
                });

            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }

    private static class File{

        private Uri filePath;
        private String fileName;

        public File(Uri filePath, String fileName){
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public Uri getFilePath() {
            return filePath;
        }

        public void setFilePath(Uri filePath) {
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
