package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupchatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendImagebutton;
    private EditText userMessageInput;
    private ScrollView mscrollview;
    private TextView diplayTextMessage;

    private String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;
  private   FirebaseAuth mAuth;
  private DatabaseReference UserRef,GroupNameRef,GroupMessagekeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        currentGroupName= getIntent().getExtras().getString("GroupName").toString();
        Toast.makeText(this, ""+currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid().toString();

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");

        GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


        IntailizeFields();

        GetUserInfo();

        sendImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveMessageInfotoDatabase();

                userMessageInput.setText("");

                mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void saveMessageInfotoDatabase() {

        String sendmessage=userMessageInput.getText().toString();
        String messkey=GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(sendmessage)){

            Toast.makeText(this, "Please write message .....", Toast.LENGTH_SHORT).show();
        }
        else

        {
            Calendar calfordate= Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, yyyy");
            currentDate=simpleDateFormat.format(calfordate.getTime());

            Calendar calforTime= Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=simpleTimeFormat.format(calforTime.getTime());


            HashMap<String,Object> mymessageKey=new HashMap<>();
            GroupNameRef.updateChildren(mymessageKey);
            GroupMessagekeyRef=GroupNameRef.child(messkey);

            HashMap<String,Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",sendmessage);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            GroupMessagekeyRef.updateChildren(messageInfoMap);
            Toast.makeText(this, sendmessage+"Message sent", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){

                    displayAllmessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){

                    displayAllmessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayAllmessages(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();

        String messagedate=(String)((DataSnapshot)iterator.next()).getValue();
        String message=(String)((DataSnapshot)iterator.next()).getValue();
        String messagesendername=(String)((DataSnapshot)iterator.next()).getValue();
        String messagetime=(String)((DataSnapshot)iterator.next()).getValue();

        diplayTextMessage.append(messagesendername+":\n"+message+"\n"+messagedate+"\n"+messagetime+"\n\n");

        mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void GetUserInfo() {

        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    currentUserName=dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void IntailizeFields() {

        mToolbar= findViewById(R.id.chatbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendImagebutton=findViewById(R.id.send_messagebutton);
        userMessageInput=findViewById(R.id.input_groupmessage);
        diplayTextMessage=findViewById(R.id.groupchat_display);
        mscrollview=findViewById(R.id.myscroll_view);

    }


}
