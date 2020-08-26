package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private  String receiveUserid,currentstate,senderUserid;

    private FirebaseAuth mAuth;
    CircleImageView imageViewprofile;
    TextView textViewprofilename,textViewprofilestatus;
    Button sendRequestbtn,declineRequestbtn;



    DatabaseReference UserRef,ChatRequestRef,ContactsRef,NotificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        mAuth=FirebaseAuth.getInstance();
        imageViewprofile=findViewById(R.id.visitprofile_image);
        textViewprofilename=findViewById(R.id.visituser_name);
        textViewprofilestatus=findViewById(R.id.visituser_status);
        sendRequestbtn=findViewById(R.id.sendrequest_button);
        declineRequestbtn=findViewById(R.id.declinerequest_button);
        currentstate="new";

        senderUserid=mAuth.getCurrentUser().getUid().toString();
        receiveUserid=getIntent().getExtras().get("userid").toString();

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        NotificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        ChatRequestRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        RetriveUserinfo();

    }

    private void RetriveUserinfo() {


        UserRef.child(receiveUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("image"))){

                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();
                    String userimage=dataSnapshot.child("image").getValue().toString();

                //    Glide.with(getApplicationContext()).load(userimage).placeholder(R.drawable.profileimage).into(imageViewprofile);

                    Picasso.get().load(userimage).placeholder(R.drawable.profileimage).into(imageViewprofile);
                    textViewprofilename.setText(username);
                    textViewprofilestatus.setText(userstatus);

                    ManageChatRequest();
                }

                else {

                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();

                    textViewprofilename.setText(username);
                    textViewprofilestatus.setText(userstatus);

                    ManageChatRequest();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {

        ChatRequestRef.child(senderUserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild(receiveUserid)){

                            String requestype=dataSnapshot.child(receiveUserid).child("request_type").getValue().toString();

                            if (requestype.equals("sent")){

                                currentstate="request_sent";
                                sendRequestbtn.setText("Cancel Request");
                            }

                            else if (requestype.equals("received")){

                                currentstate="request_received";
                                sendRequestbtn.setText("Accept Request");
                                declineRequestbtn.setVisibility(View.VISIBLE);
                                declineRequestbtn.setEnabled(true);
                                declineRequestbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        cancelchatRequest();
                                    }
                                });
                            }
                        }
                        else {

                            ContactsRef.child(senderUserid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild(receiveUserid)){

                                                currentstate="friends";
                                                sendRequestbtn.setText("Remove this Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        if(!senderUserid.equals(receiveUserid)){

            sendRequestbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                sendRequestbtn.setEnabled(false);

                if (currentstate.equals("new")){

                    sendchatRequest();
                }

                if (currentstate.equals("request_sent")){

                    cancelchatRequest();
                }

                if (currentstate.equals("request_received")){


                    AcceptRequest();
                }

                    if (currentstate.equals("friends")){


                        RemovespecificContact();
                    }
                }
            });
        }

        else {

            sendRequestbtn.setVisibility(View.GONE);
        }


    }

    private void RemovespecificContact() {


        ContactsRef.child(senderUserid).child(receiveUserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){

                            ContactsRef.child(receiveUserid).child(senderUserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()){

                                                sendRequestbtn.setEnabled(true);
                                                currentstate="new";
                                                sendRequestbtn.setText("Send Request");

                                                declineRequestbtn.setVisibility(View.GONE);
                                                declineRequestbtn.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptRequest() {

        ContactsRef.child(senderUserid).child(receiveUserid)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){

                            ContactsRef.child(receiveUserid).child(senderUserid)
                                    .child("Contacts")
                                    .setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                ChatRequestRef.child(senderUserid)
                                                        .child(receiveUserid)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {


                                                        if (task.isSuccessful()){

                                                            ChatRequestRef.child(receiveUserid)
                                                                    .child(senderUserid)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    sendRequestbtn.setEnabled(true);
                                                                    currentstate="friends";
                                                                    sendRequestbtn.setText("Remove this Contact");

                                                                    declineRequestbtn.setVisibility(View.GONE);
                                                                    declineRequestbtn.setEnabled(false);

                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelchatRequest() {

        ChatRequestRef.child(senderUserid).child(receiveUserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){

                            ChatRequestRef.child(receiveUserid).child(senderUserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()){

                                                sendRequestbtn.setEnabled(true);
                                                currentstate="new";
                                                sendRequestbtn.setText("Send Request");

                                                declineRequestbtn.setVisibility(View.GONE);
                                                declineRequestbtn.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendchatRequest() {


        ChatRequestRef.child(senderUserid).child(receiveUserid)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {

                        if (task.isSuccessful()){

                            ChatRequestRef.child(receiveUserid).child(senderUserid)
                                    .child("request_type").setValue("received")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            if (task.isSuccessful()){

                                                HashMap<String,String> chatnotification=new HashMap();
                                                chatnotification.put("from",senderUserid);
                                                chatnotification.put("type","request");

                                                NotificationRef.child(receiveUserid).push()
                                                        .setValue(chatnotification)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){

                                                                    currentstate="request_sent";
                                                                    sendRequestbtn.setEnabled(true);
                                                                    sendRequestbtn.setText("Cancel Request");

                                                                }
                                                            }
                                                        });



                                                currentstate="request_sent";
                                                sendRequestbtn.setEnabled(true);
                                                sendRequestbtn.setText("Cancel Request");


                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
