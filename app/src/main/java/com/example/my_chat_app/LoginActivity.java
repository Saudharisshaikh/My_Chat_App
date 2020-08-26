package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.UploadTask;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    Button buttonlogin,phonelogin;
    EditText Email, Password;

    TextView forgetpassword,needNewaccount;

    private ProgressDialog Loadingbar;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();
       currentUser=mAuth.getCurrentUser();

        buttonlogin=findViewById(R.id.login_button);
        phonelogin=findViewById(R.id.phone_loginbutton);
        Email=findViewById(R.id.login_email);
        Password=findViewById(R.id.login_password);
        forgetpassword=findViewById(R.id.forget_passwordlink);
        needNewaccount=findViewById(R.id.neednew_accountlink);

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");


        Loadingbar=new ProgressDialog(this);

//        intializeFields();

        needNewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   sendtoRegisterActivity();

                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




     //               AllowUsertzoLogin();

                   // String email=Email.getText().toString();
                  //  forgetpassword.setText(""+email);

                    String email=Email.getText().toString();
                    String password=Password.getText().toString();

                    if (TextUtils.isEmpty(email)){

                        Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    }

                    if (TextUtils.isEmpty(password)){

                        Toast.makeText(LoginActivity.this, "Please enter password...", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Loadingbar.setTitle("Sign In");
                        Loadingbar.setMessage("Please wait for a while !!!");
                        Loadingbar.setCanceledOnTouchOutside(true);
                        Loadingbar.show();

                     //   Toast.makeText(LoginActivity.this, ""+email+" "+password, Toast.LENGTH_SHORT).show();


                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    String currentUserId=mAuth.getCurrentUser().getUid();
                                    final String deviceToken= FirebaseInstanceId.getInstance().getToken();

                                    UserRef.child(currentUserId).child("device_token")
                                            .setValue(deviceToken)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){


                                                        Toast.makeText(LoginActivity.this, ""+deviceToken, Toast.LENGTH_SHORT).show();

                                                        Loadingbar.dismiss();
                                                        //   sendUsertoMainActivity();

                                                        sendUsertoMainActivity();
                                                        Toast.makeText(LoginActivity.this, "Logged in Successfully..", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });




                                }
                                else {
                                    Loadingbar.dismiss();

                                    String error=task.getException().toString();
                                    Toast.makeText(LoginActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });



                }


            }
        });


        phonelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,phone_login.class);

            startActivity(intent);
            }
        });

    }

    /*
    private void AllowUsertoLogin() {

         String email=Email.getText().toString();
        String password=Password.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please enter an email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {

            Loadingbar.setTitle("Sign In");
            Loadingbar.setMessage("Please wait for a while !!!");
            Loadingbar.setCanceledOnTouchOutside(true);
            Loadingbar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        Loadingbar.dismiss();
                        sendUsertoMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged in Successfully..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Loadingbar.dismiss();

                        String error=task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }
    */

    private void sendtoRegisterActivity() {

        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

  //  private void intializeFields() {

        /*
        buttonlogin=findViewById(R.id.login_button);
        phonelogin=findViewById(R.id.phone_button);
        UserEmail=findViewById(R.id.login_email);
        UserPassword=findViewById(R.id.login_password);
        forgetpassword=findViewById(R.id.forget_passwordlink);
        needNewaccount=findViewById(R.id.neednew_accountlink);

        Loadingbar=new ProgressDialog(this);
        */
    //}




    private void sendUsertoMainActivity() {

        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser!=null){

            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

}

