package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    Button updatesetting;
    EditText username,userstatus;
    CircleImageView userprofileimage;

    private static String downloadurl;
    private  Uri myUri;
    private Uri alluri;
    FirebaseAuth mAuth;
    DatabaseReference Rootref;
    String currentUserId;
    private ProgressDialog loadingbar;
    private Toolbar mToolbar;

    private StorageReference UserprofileimageRef;

    private  int myrequestcode=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UserprofileimageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");



        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        Rootref= FirebaseDatabase.getInstance().getReference();

        intializefields();

    //    username.setVisibility(View.GONE);

        userprofileimage.setVisibility(View.VISIBLE);

        updatesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatesettings();
            }
        });

        RetriveUserInfo();

        userprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(intent,myrequestcode);


            }
        });



    }

    private void RetriveUserInfo() {

        Rootref.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image"))))
                        {

                        String retriveName=dataSnapshot.child("name").getValue().toString();
                        String retriveStatus=dataSnapshot.child("status").getValue().toString();
                        String retriveimage=dataSnapshot.child("image").getValue().toString();

                        username.setText(retriveName);
                        userstatus.setText(retriveStatus);
                            Picasso.get().load(retriveimage).placeholder(R.drawable.profileimage).into(userprofileimage);

//                            Glide.with(SettingActivity.this).load(retriveimage).into(userprofileimage);


                        }

                        else if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){


                            String retriveName=dataSnapshot.child("name").getValue().toString();
                            String retriveStatus=dataSnapshot.child("status").getValue().toString();

                            username.setText(retriveName);
                            userstatus.setText(retriveStatus);

                        }

                        else {

                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingActivity.this, "please set your profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updatesettings() {

     final    String setusername=username.getText().toString();
      final   String setstatus=userstatus.getText().toString();

        if (TextUtils.isEmpty(setusername)){

            Toast.makeText(this, "Please write your username ....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setstatus)){

            Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show();
        }
        else {


            loadingbar.setTitle("Setting  user image");
            loadingbar.setMessage("We are updating user image ...");
            loadingbar.setCanceledOnTouchOutside(false);
      //      loadingbar.show();

            final StorageReference filepath=UserprofileimageRef.child(currentUserId + ".jpg");

            final  UploadTask uploadTask=filepath.putFile(alluri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){

                        throw task.getException();
                    }


                    downloadurl=filepath.getDownloadUrl().toString();

                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    downloadurl=task.getResult().toString();

                    HashMap<String,String> profileMap=new HashMap<>();
                    profileMap.put("uid",currentUserId);
                    profileMap.put("name",setusername);
                    profileMap.put("status",setstatus);
                    profileMap.put("image",downloadurl);

                    Rootref.child("Users").child(currentUserId).setValue(profileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()){

                                        sendUsertoMainActivity();

                                        Toast.makeText(SettingActivity.this, "Your profile has been updated....", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                        String message=task.getException().toString();
                                        Toast.makeText(SettingActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });

/*
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setusername);
            profileMap.put("status",setstatus);
            Rootref.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()){

                                sendUsertoMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String message=task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
*/

        }
    }

    private void intializefields() {

        updatesetting=findViewById(R.id.updatestatus_button);
        username=findViewById(R.id.set_username);
        userstatus=findViewById(R.id.set_status);
        userprofileimage=findViewById(R.id.profile_image);

        loadingbar=new ProgressDialog(this);

        mToolbar=findViewById(R.id.settingpage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }
    private void sendUsertoMainActivity() {

        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==myrequestcode&&resultCode==RESULT_OK&&data!=null) {

            myUri = data.getData();
        //    userprofileimage.setImageURI(myUri);


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            alluri=result.getUri();
        if (resultCode==RESULT_OK){


            userprofileimage.setImageURI(alluri);
        }

        }

        /*
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode== RESULT_OK){


                loadingbar.setTitle("Setting  user image");
                loadingbar.setMessage("We are updating user image ...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                final Uri resultUri=result.getUri();

            final StorageReference filepath=UserprofileimageRef.child(currentUserId + ".jpg");

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(SettingActivity.this, " Profile image uploaded successfully..", Toast.LENGTH_SHORT).show();

                    loadingbar.dismiss();

                    final String downloadurl=task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    Rootref.child("Users").child(currentUserId).child("image").setValue(downloadurl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(SettingActivity.this, "Your image has saved in database successfully...", Toast.LENGTH_SHORT).show();

                                    userprofileimage.setImageURI(resultUri);
                                    }
                                    else {

                                        loadingbar.dismiss();
                                        String message=task.getException().toString();
                                        Toast.makeText(SettingActivity.this,"Error"+message,Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                }
                else {
                    loadingbar.dismiss();

                    String messageerror=task.getException().toString();
                    Toast.makeText(SettingActivity.this, "Error :"+messageerror, Toast.LENGTH_SHORT).show();
                }
                }


            });

            }
        }

    }
    */
}}
