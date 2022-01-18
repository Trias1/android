package com.example.cengonline.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cengonline.R;
import com.example.cengonline.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SetRoleDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private User user;
    private Button cancelButton;
    private Spinner spinner;

    public SetRoleDialog(Activity activity, User user) {
        super(activity);
        this.activity = activity;
        this.user = user;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_role);
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.cancelButton = (Button)findViewById(R.id.set_role_cancel_button);
        this.spinner = (Spinner)findViewById(R.id.spinner);
        String[] roles = new String[]{User.Role.STUDENT.toString(), User.Role.TEACHER.toString()};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, roles);
        spinner.setAdapter(adapter);

        if(this.user.getRoles().contains(User.Role.TEACHER)){
            spinner.setSelection(adapter.getPosition(User.Role.TEACHER.toString()));
        }
        else{
            spinner.setSelection(adapter.getPosition(User.Role.STUDENT.toString()));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User.Role selectedRole = User.Role.parse(String.valueOf(spinner.getSelectedItem()));
                if((selectedRole == User.Role.STUDENT && user.getRoles().contains(User.Role.TEACHER))
                    || (selectedRole == User.Role.TEACHER && !user.getRoles().contains(User.Role.TEACHER)))
                {
                    updateUserRole(selectedRole, user);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.cancelButton.setOnClickListener(this);
    }

    private void updateUserRole(final User.Role newRole, User user){

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userRef.orderByKey().equalTo(user.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = null;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        user = ds.getValue(User.class);
                    }
                    if(user != null){
                        if(newRole == User.Role.TEACHER){
                            user.getRoles().add(User.Role.TEACHER);
                        }
                        else if(newRole == User.Role.STUDENT){
                            user.getRoles().remove(User.Role.TEACHER);
                        }
                        userRef.child(user.getKey()).setValue(user);
                        SetRoleDialog.this.user = user;
                        makeToastMessage("Role has been updated successfully!");
                    }
                    else{
                        makeToastMessage("An error occurred while updating role!");
                        dismiss();
                    }
                }
                else{
                    makeToastMessage("An error occurred while updating role!");
                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                makeToastMessage("An error occurred while updating role!");
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.set_role_cancel_button: dismiss(); break;
            default: break;
        }
    }

    private void makeToastMessage(String message){
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
    }
}
