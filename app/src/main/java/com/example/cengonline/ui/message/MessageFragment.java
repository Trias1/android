package com.example.cengonline.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.cengonline.DatabaseCallback;
import com.example.cengonline.DatabaseUtility;
import com.example.cengonline.R;
import com.example.cengonline.Utility;
import com.example.cengonline.model.Conversation;
import com.example.cengonline.model.User;
import com.example.cengonline.ui.dialog.NewMessageDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MessageFragment extends Fragment {


    private FloatingActionButton fab;
    private LinearLayout linearLayout;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        fab = (FloatingActionButton)view.findViewById(R.id.add_message_fab);
        linearLayout = (LinearLayout)view.findViewById(R.id.scroll_view_linear_layout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMessageDialog nmD = new NewMessageDialog(getActivity());
                nmD.show();
            }
        });

        getConversations();

        return view;
    }

    private void getConversations(){
        DatabaseUtility.getInstance().getConversations(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                if(result != null){
                    linearLayout.removeAllViews();
                    List<Conversation> conversations = (ArrayList<Conversation>)result;
                    if(conversations.size() == 0 && getActivity() != null){
                        TextView tv = new TextView(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(lp);
                        tv.setTextAppearance(getActivity(), R.style.fontForEmptyMessage);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        tv.setText("You haven't got any message!");
                        linearLayout.setGravity(Gravity.CENTER);
                        linearLayout.addView(tv, lp);
                    }
                    else{
                        linearLayout.setGravity(Gravity.TOP);
                    }
                    Collections.sort(conversations);
                    for(Conversation conversation : conversations){
                        drawConversation(conversation);
                    }
                }
            }
            @Override
            public void onFailed(String message) {
                makeToastMessage(message);
            }
        });
    }

    private void drawConversation(final Conversation conversation){

        if(getContext() == null){
            return;
        }

        if(conversation.getMessages().size() == 0){
            return;
        }

        Utility util = Utility.getInstance();

        TextView imageText = new TextView(getContext());
        LinearLayout.LayoutParams imageTextLayoutParams = new LinearLayout.LayoutParams(util.DPtoPX(40, getActivity()), util.DPtoPX(40, getActivity()));
        imageText.setLayoutParams(imageTextLayoutParams);
        imageText.setTextAppearance(getContext(), R.style.fontForImageTextOnCard);
        imageText.setBackgroundResource(R.drawable.rounded_textview);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(conversation.getReceiver().getUid()))
            imageText.setText(String.valueOf(conversation.getSender().getDisplayName().toUpperCase().charAt(0)));
        else
            imageText.setText(String.valueOf(conversation.getReceiver().getDisplayName().toUpperCase().charAt(0)));
        imageText.setGravity(Gravity.CENTER);

        LinearLayout insideLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams insideLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(util.DPtoPX(225, getActivity()) ,LinearLayout.LayoutParams.WRAP_CONTENT);
        insideLinearLayout.setLayoutParams(insideLinearLayoutLayoutParams);
        insideLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView displayNameText = new TextView(getContext());
        LinearLayout.LayoutParams displayNameTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        displayNameTextLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        displayNameTextLayoutParams.rightMargin = util.DPtoPX(30, getActivity());
        displayNameText.setLayoutParams(displayNameTextLayoutParams);
        displayNameText.setTextAppearance(getContext(), R.style.fontForDisplayNameOnCard);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(conversation.getReceiver().getUid()))
            displayNameText.setText(conversation.getSender().getDisplayName());
        else
            displayNameText.setText(conversation.getReceiver().getDisplayName());

        TextView dateText = new TextView(getContext());
        LinearLayout.LayoutParams dateTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        dateTextLayoutParams.rightMargin = util.DPtoPX(20, getActivity());
        dateText.setLayoutParams(dateTextLayoutParams);
        dateText.setTextAppearance(getContext(), R.style.fontForDateOnCard);
        dateText.setText(conversation.getMessages().get(conversation.getMessages().size() - 1).getSentAt().toString());


        LinearLayout middleLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams middleLinarLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        middleLinarLayoutLayoutParams.topMargin = util.DPtoPX(10, getActivity());
        middleLinarLayoutLayoutParams.bottomMargin = util.DPtoPX(10, getActivity());
        middleLinarLayoutLayoutParams.leftMargin = util.DPtoPX(15, getActivity());
        middleLinearLayout.setLayoutParams(middleLinarLayoutLayoutParams);
        middleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        middleLinearLayout.setGravity(Gravity.CENTER);

        TextView bodyText = new TextView(getContext());
        LinearLayout.LayoutParams bodyTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyTextLayoutParams.leftMargin = util.DPtoPX(20, getActivity());
        bodyTextLayoutParams.rightMargin = util.DPtoPX(30, getActivity());
        bodyText.setTextAppearance(getContext(), R.style.fontForDateOnMessagesCard);
        bodyText.setLines(1);
        bodyText.setEllipsize(TextUtils.TruncateAt.END);
        bodyText.setText(conversation.getMessages().get(conversation.getMessages().size() - 1).getBody());


        LinearLayout outerLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams outerLinearLayoutLayoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLayoutParams);
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);


        insideLinearLayout.addView(displayNameText, displayNameTextLayoutParams);
        insideLinearLayout.addView(bodyText, bodyTextLayoutParams);
        middleLinearLayout.addView(imageText, imageTextLayoutParams);
        middleLinearLayout.addView(insideLinearLayout, insideLinearLayoutLayoutParams);
        middleLinearLayout.addView(dateText, dateTextLayoutParams);
        outerLinearLayout.addView(middleLinearLayout, middleLinarLayoutLayoutParams);


        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.bottomMargin = util.DPtoPX(7, getActivity());
        cardViewLayoutParams.leftMargin = util.DPtoPX(8, getActivity());
        cardViewLayoutParams.rightMargin = util.DPtoPX(8, getActivity());
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setClickable(true);
        cardView.setRadius(util.DPtoPX(8, getActivity()));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(conversation.getReceiver().getUid()))
                    goToMessage(conversation.getSender(), conversation.getKey());
                else
                    goToMessage(conversation.getReceiver(), conversation.getKey());
            }
        });

        cardView.addView(outerLinearLayout, outerLinearLayoutLayoutParams);

        this.linearLayout.addView(cardView, cardViewLayoutParams);
    }

    private void goToMessage(User user, String conversationKey){

        Intent intent = new Intent(getActivity(), SpecialMessageFragment.class);
        intent.putExtra("sendUser", user);
        intent.putExtra("conversationKey", conversationKey);
        getActivity().startActivity(intent);
    }

    private void makeToastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
