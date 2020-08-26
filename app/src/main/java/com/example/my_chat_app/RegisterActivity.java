package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    EditText UserEmail,UserPassword;
    Button RegisterUser;
    TextView alreadyAccount;
private ProgressDialog Loadingbar;
    FirebaseAuth mAuth;

    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RootRef=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        intializeWidgets();

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                sendtoLoginActivity();
            }
        });

        RegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {

        final String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please enter an email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {


            Loadingbar.setTitle("Creating New Account");
            Loadingbar.setMessage("Please wait for a while we are creating account !!!");
            Loadingbar.setCanceledOnTouchOutside(true);
            Loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()){

                     //   sendtoLoginActivity();

                        final String deviceToken= FirebaseInstanceId.getInstance().getToken();



                        String id=mAuth.getCurrentUser().getUid().toString();
                        RootRef.child("Users").child(id).setValue("");

                        RootRef.child("Users").child(id).child("device_token")
                                .setValue(deviceToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            Toast.makeText(RegisterActivity.this, ""+deviceToken, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        sendtoMainActivity();

                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                        Loadingbar.dismiss();

                    }
                    else {

                        Loadingbar.dismiss();
                        String error=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void sendtoMainActivity() {

        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendtoLoginActivity() {

        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void intializeWidgets() {

        UserEmail=findViewById(R.id.Register_email);
        UserPassword=findViewById(R.id.Register_password);
        RegisterUser=findViewById(R.id.Register_button);
        alreadyAccount=findViewById(R.id.already_accountlink);
        Loadingbar=new ProgressDialog(this);
    }
}
