package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phone_login extends AppCompatActivity {

    EditText phoneno,verifycode;
    Button   phoneverifybutton,verifybuttone;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken ResendToken;
    private String VerificationId;

    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        loadingbar=new ProgressDialog(phone_login.this);
        mAuth=FirebaseAuth.getInstance();

        phoneverifybutton=findViewById(R.id.sendverificaioncode_btn);
        verifybuttone=findViewById(R.id.verify_btn);
        phoneno=findViewById(R.id.phone_input);
        verifycode=findViewById(R.id.verificationcode_input);

        phoneverifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                String phoneNumber=phoneno.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)){

                    Toast.makeText(phone_login.this, "Please write your phone number ...", Toast.LENGTH_SHORT).show();
                }
                else {

                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please wait, we are authentication your phone...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,phone_login.this,mCallbacks);


                }
            }
        });


        verifybuttone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifybuttone.setVisibility(View.VISIBLE);
                verifycode.setVisibility(View.VISIBLE);


                String myverifycode=verifycode.getText().toString();
                if (TextUtils.isEmpty(myverifycode)){

                    Toast.makeText(phone_login.this, "Please write verification code", Toast.LENGTH_SHORT).show();
                }
                else {

                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("Please wait, we are verifying  your verification code...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, myverifycode);

                signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                loadingbar.dismiss();

                Toast.makeText(phone_login.this, "Please enter valid phone and verification code....", Toast.LENGTH_SHORT).show();

                phoneverifybutton.setVisibility(View.VISIBLE);
                phoneno.setVisibility(View.VISIBLE);

                verifybuttone.setVisibility(View.GONE);
                verifycode.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationid, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationid, forceResendingToken);

                VerificationId=verificationid;
                ResendToken=forceResendingToken;


                Toast.makeText(phone_login.this, "Code has been sent", Toast.LENGTH_SHORT).show();

                phoneverifybutton.setVisibility(View.GONE);
                phoneno.setVisibility(View.GONE);


                verifybuttone.setVisibility(View.VISIBLE);
                verifycode.setVisibility(View.VISIBLE);
                loadingbar.dismiss();

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        loadingbar.dismiss();
                            Intent intent=new Intent(phone_login.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else {

                            String message=task.getException().toString();

                            Toast.makeText(phone_login.this, "Error "+message, Toast.LENGTH_SHORT).show();

                            }
                        }

                });
    }
}
