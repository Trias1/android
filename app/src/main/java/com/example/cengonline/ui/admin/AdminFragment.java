package com.example.cengonline.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.User;
import com.example.cengonline.ui.dialog.SetRoleDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminFragment extends Fragment {

    private TableLayout tableLayout;
    private Spinner spinner;
    private LinearLayout scrollLinearLayout;
    private Map<String, User> users;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        users = new HashMap<String, User>();
        this.scrollLinearLayout = view.findViewById(R.id.linearLayout);

        getAllUsers();
        return view;
    }

    private void drawUsers(){

        this.scrollLinearLayout.removeAllViews();
        if(getActivity() != null){
            for(User user : this.users.values()){
                drawUser(user);
            }
        }
    }

    private void drawUser(final User user){

        Utility util = Utility.getInstance();

        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams linearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearLayoutLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView username = new TextView(getActivity());
        LinearLayout.LayoutParams usernameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        usernameLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        usernameLayoutParams.topMargin = util.DPtoPX(15, getActivity());
        username.setLayoutParams(usernameLayoutParams);
        username.setLines(1);
        username.setTextAppearance(getActivity(), R.style.fontForDisplayNameOnCard);
        username.setEllipsize(TextUtils.TruncateAt.END);
        username.setText(user.getDisplayName());

        TextView email = new TextView(getActivity());
        LinearLayout.LayoutParams emailLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        emailLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        email.setLayoutParams(emailLayoutParams);
        email.setLines(1);
        email.setTextAppearance(getActivity(), R.style.fontForDisplayNameOnCard);
        email.setEllipsize(TextUtils.TruncateAt.END);
        email.setText(user.getEmail());

        TextView roles = new TextView(getActivity());
        LinearLayout.LayoutParams rolesLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rolesLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        rolesLayoutParams.bottomMargin = util.DPtoPX(15, getActivity());
        roles.setLayoutParams(rolesLayoutParams);
        roles.setLines(1);
        roles.setTextAppearance(getActivity(), R.style.fontForDisplayNameOnCard);
        roles.setEllipsize(TextUtils.TruncateAt.END);
        roles.setText(user.getRoles().toString());


        linearLayout.addView(username, usernameLayoutParams);
        linearLayout.addView(email, emailLayoutParams);
        linearLayout.addView(roles, rolesLayoutParams);

        CardView cardView = new CardView(getActivity());
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.leftMargin = util.DPtoPX(13, getActivity());
        cardViewLayoutParams.rightMargin = util.DPtoPX(13, getActivity());
        cardViewLayoutParams.bottomMargin = util.DPtoPX(7, getActivity());
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setClickable(true);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetRole(user.getKey());
            }
        });


        cardView.addView(linearLayout, linearLayoutLayoutParams);
        this.scrollLinearLayout.addView(cardView, cardViewLayoutParams);
    }

    private void goToSetRole(String key){

        User user = this.users.get(key);

        if(user != null && user.getRoles().contains(User.Role.ADMIN)){
            makeToastMessage("You can't set role of admins!");
            return;
        }
        SetRoleDialog srD = new SetRoleDialog(getActivity(), user);
        srD.show();
    }

    private void getAllUsers(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userRef.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new HashMap<String, User>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if(user != null)
                            users.put(user.getKey(), user);
                    }
                    drawUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void makeToastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
