package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.my_chat_app.R.color.colorPrimaryDark;

public class MainActivity extends AppCompatActivity {

  private   Toolbar mtoolbar;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private TabAdapter tabAdapter;


    private FirebaseAuth mAuth;

    private String currentUserId;

    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RootRef= FirebaseDatabase.getInstance().getReference();

        mtoolbar=findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("My_What's_App");


        mAuth=FirebaseAuth.getInstance();


        viewPager=findViewById(R.id.maintabs_pager);
        tabLayout=findViewById(R.id.main_tabs);

        tabAdapter=new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Basically ViewPager is handled by tabAdapter what we
        // create and inside tabAdapter we pass all fragment we create
        // that why we pass tabAdapter to viewpager
        // and finally we pass the viewpager to tablayout because
        // tablayout is handled by Viewpager.
    }



    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser=mAuth.getCurrentUser();

        if (currentUser==null){

            sendtoLoginActivity();
        }
        else {

            UpdateUserState("Online");
            verifyExistence();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser!=null){

            UpdateUserState("Offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser!=null){

            UpdateUserState("Offline");
        }

    }

    private void verifyExistence() {

        String  currentUserId=mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("name").exists())){

                    Toast.makeText(MainActivity.this, "Welcome !!!", Toast.LENGTH_SHORT).show();
                }
                else {

                    sendtoSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendtoLoginActivity() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {



         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.option_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);


         if (item.getItemId()==R.id.findfriends){


            sendtoFindFriendActivity();
         }

        if (item.getItemId()==R.id.settings){


            sendtoSettingsActivity();
        }

        if (item.getItemId()==R.id.createGroup_option){

            RequestNewGroup();

        }

        if (item.getItemId()==R.id.logout){




            UpdateUserState("Offline");
            mAuth.signOut();
            sendtoLoginActivity();
        }
        return true;
    }

    private void sendtoFindFriendActivity() {

        Intent intent=new Intent(MainActivity.this,FindFriends.class);
        startActivity(intent);

    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);

        builder.setTitle("Enter Group Name :");

        final EditText editTextgroup=new EditText(MainActivity.this);
        editTextgroup.setHint("e.g Friends Zone");

        editTextgroup.setTextColor(getResources().getColor(R.color.black));
        editTextgroup.setHintTextColor(getResources().getColor(colorPrimaryDark));
        builder.setView(editTextgroup);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String groupname=editTextgroup.getText().toString();

                if (TextUtils.isEmpty(groupname)){

                    Toast.makeText(MainActivity.this, "Please write Group Name ....", Toast.LENGTH_SHORT).show();
                }
                else {


                    CreateNewGroup(groupname);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupname) {

        RootRef.child("Groups").child(groupname).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(MainActivity.this, ""+groupname+" group is created successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void sendtoSettingsActivity() {

        Intent intent=new Intent(MainActivity.this,SettingActivity.class);


        startActivity(intent);

    }

    public void UpdateUserState(String state){


        String savecurrenTime,savecurrentDate;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd, yyyy");
        savecurrentDate=currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        savecurrenTime=currentTime.format(calendar.getTime());

        HashMap<String,Object> onlineStatus=new HashMap<>();
        onlineStatus.put("time",savecurrenTime);
        onlineStatus.put("date",savecurrentDate);
        onlineStatus.put("status",state);

        currentUserId=mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserId).child("userState")
                .updateChildren(onlineStatus);

    }
}
