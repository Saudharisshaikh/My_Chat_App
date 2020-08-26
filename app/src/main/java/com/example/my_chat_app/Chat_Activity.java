package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Activity extends AppCompatActivity {

    private String receiverId,receverName,receiveImage,messagesenderId;

    private TextView textViewname,textViewlastseen;
    private CircleImageView myimageview;
    private Toolbar chatToolbar;

    private EditText inputMessage;
    private ImageButton sendbtn;
    private ImageButton sendfilebtn;
    private FirebaseAuth mAuth;
    private DatabaseReference Root;

    private ProgressDialog loadingbar;



    String checker="";
    String MyUrl="";
    Uri fileuri;
    private StorageTask UploadTask;




    private List<Messages> Messagelist=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager ;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;

    String savecurrenTime,savecurrentDate;
    Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        Root= FirebaseDatabase.getInstance().getReference();

        receiverId=getIntent().getStringExtra("visitID").toString();
        receverName=getIntent().getStringExtra("visitName").toString();
        receiveImage=getIntent().getStringExtra("visitimage").toString();



    intializewidgets();

    textViewname.setText(receverName);
        Picasso.get().load(receiveImage).placeholder(R.drawable.profileimage).into(myimageview);


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });


        displayLastSeen();


        sendfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CharSequence [] options=new CharSequence[]
                        {
                         "Image","MS Word Files","PDF Files"
                        };

                AlertDialog.Builder builder=new AlertDialog.Builder(Chat_Activity.this);

                builder.setTitle("Select the file");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i==0){

                            checker="image";

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"select the image"),438);


                        }

                        if(i==1){

                            checker="pdf";

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"select the PDF file"),438);


                        }
                        if(i==2){

                            checker="docx";


                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"select the Ms Word  file"),438);
                        }
                    }
                });

                builder.show();
            }
        });
    }

    private void sendMessage() {

        String message=inputMessage.getText().toString();

        if (TextUtils.isEmpty(message)){

            Toast.makeText(this, "Please write your message first ....", Toast.LENGTH_SHORT).show();
        }
        else {

            String messageSenderref="Messages/"+ messagesenderId+"/"+receiverId;
            String messageReceivererref="Messages/"+ receiverId+"/"+messagesenderId;
        DatabaseReference usermessagekeyref=Root.child("Messages").child(messagesenderId).child(receiverId).push();

        String MessagePushId=usermessagekeyref.getKey();

            Map MessageTextBody=new HashMap();
            MessageTextBody.put("message",message);
            MessageTextBody.put("type","text");
            MessageTextBody.put("From",messagesenderId);
            MessageTextBody.put("to",receiverId);
            MessageTextBody.put("messageId",MessagePushId);
            MessageTextBody.put("date",savecurrentDate);
            MessageTextBody.put("time",savecurrenTime);




        Map MessageDetails=new HashMap();
        MessageDetails.put(messageSenderref+"/"+MessagePushId,MessageTextBody);
            MessageDetails.put(messageReceivererref+"/"+MessagePushId,MessageTextBody);

            Root.updateChildren(MessageDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        Toast.makeText(Chat_Activity.this, "Task is successful", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        String messageerror=task.getException().toString();
                        Toast.makeText(Chat_Activity.this, "Error "+messageerror, Toast.LENGTH_SHORT).show();
                    }

                    inputMessage.setText("");
                }
            });
        }
    }

    private void intializewidgets() {



        chatToolbar=findViewById(R.id.mychatbartoolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionview=layoutInflater.inflate(R.layout.customchatbar,null);
        actionBar.setCustomView(actionview);

        textViewname=findViewById(R.id.custom_profilename);
        textViewlastseen=findViewById(R.id.custom_lastseen);
        myimageview=findViewById(R.id.custom_imageview);
        mAuth=FirebaseAuth.getInstance();
        messagesenderId=mAuth.getCurrentUser().getUid().toString();

        inputMessage=findViewById(R.id.messageinput);
        sendbtn=findViewById(R.id.sendmessage_btn);

        sendfilebtn=findViewById(R.id.sendfile_btn);
        messageAdapter=new MessageAdapter(Messagelist);
        recyclerView=findViewById(R.id.chat_recyclerview);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        loadingbar=new ProgressDialog(Chat_Activity.this);


        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd, yyyy");
        savecurrentDate=currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        savecurrenTime=currentTime.format(calendar.getTime());
    }

    private void displayLastSeen(){

        Root.child("Users").child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("userState").hasChild("status")){

                            String state=dataSnapshot.child("userState").child("status").getValue().toString();
                            String date=dataSnapshot.child("userState").child("date").getValue().toString();
                            String time=dataSnapshot.child("userState").child("time").getValue().toString();


                            if(state.equals("Online")){

                                textViewlastseen.setText("Online");
                            }
                            else if (state.equals("Offline")){

                                textViewlastseen.setText("Last seen: "+ date +" "+time);
                            }

                        }

                        else {
                            textViewlastseen.setText("Offline");
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Root.child("Messages").child(messagesenderId).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);
                        Messagelist.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438&&resultCode==RESULT_OK&&data.getData()!=null){



            loadingbar.setTitle("Sending file");
            loadingbar.setMessage("We are sending file ...");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            fileuri=data.getData();
            if (!checker.equals("image")){

                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document  file");

                final String messageSenderref="Messages/"+ messagesenderId+"/"+receiverId;
                final String messageReceivererref="Messages/"+ receiverId+"/"+messagesenderId;
                DatabaseReference usermessagekeyref=Root.child("Messages").child(messagesenderId).child(receiverId).push();

                final   String MessagePushId=usermessagekeyref.getKey();

                final StorageReference filepath=storageReference.child(MessagePushId+"."+checker);

                filepath.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){


                            Map MessageTextBody=new HashMap();
                            MessageTextBody.put("message",task.getResult().getMetadata().getReference().getDownloadUrl().toString());
                            MessageTextBody.put("name",fileuri.getLastPathSegment());

                            MessageTextBody.put("type",checker);
                            MessageTextBody.put("From",messagesenderId);
                            MessageTextBody.put("to",receiverId);
                            MessageTextBody.put("messageId",MessagePushId);
                            MessageTextBody.put("date",savecurrentDate);
                            MessageTextBody.put("time",savecurrenTime);




                            Map MessageDetails=new HashMap();
                            MessageDetails.put(messageSenderref+"/"+MessagePushId,MessageTextBody);
                            MessageDetails.put(messageReceivererref+"/"+MessagePushId,MessageTextBody);


                        Root.updateChildren(MessageDetails);
                        loadingbar.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loadingbar.dismiss();
                        Toast.makeText(Chat_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {

                        double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        loadingbar.setMessage((int)p+" %  Uploading ....");
                    }
                });

            }
            else if (checker.equals("image")){


                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image file");

                final String messageSenderref="Messages/"+ messagesenderId+"/"+receiverId;
                final String messageReceivererref="Messages/"+ receiverId+"/"+messagesenderId;
                DatabaseReference usermessagekeyref=Root.child("Messages").child(messagesenderId).child(receiverId).push();

              final   String MessagePushId=usermessagekeyref.getKey();

                final StorageReference filepath=storageReference.child(MessagePushId+"."+"Jpg");
                UploadTask=filepath.putFile(fileuri);

                UploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()){

                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {


                        if (task.isSuccessful()){

                            Uri downloadUrl=task.getResult();
                            MyUrl=downloadUrl.toString();

                            Map MessageTextBody=new HashMap();
                            MessageTextBody.put("message",MyUrl);
                            MessageTextBody.put("name",fileuri.getLastPathSegment());
                            MessageTextBody.put("type",checker);
                            MessageTextBody.put("From",messagesenderId);
                            MessageTextBody.put("to",receiverId);
                            MessageTextBody.put("messageId",MessagePushId);
                            MessageTextBody.put("date",savecurrentDate);
                            MessageTextBody.put("time",savecurrenTime);




                            Map MessageDetails=new HashMap();
                            MessageDetails.put(messageSenderref+"/"+MessagePushId,MessageTextBody);
                            MessageDetails.put(messageReceivererref+"/"+MessagePushId,MessageTextBody);

                            Root.updateChildren(MessageDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if(task.isSuccessful()){

                                        loadingbar.dismiss();
                                        Toast.makeText(Chat_Activity.this, "Task is successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                        loadingbar.dismiss();

                                        String messageerror=task.getException().toString();
                                        Toast.makeText(Chat_Activity.this, "Error "+messageerror, Toast.LENGTH_SHORT).show();
                                    }

                                    inputMessage.setText("");
                                }
                            });

                        }
                    }
                });


            }
            else {

                loadingbar.dismiss();
                Toast.makeText(this, "Nothing selected yet ....", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
