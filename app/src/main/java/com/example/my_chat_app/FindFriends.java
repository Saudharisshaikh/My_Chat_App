package com.example.my_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView findfriendlist;
  private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        findfriendlist=findViewById(R.id.findfriends_recyclerlist);
        findfriendlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar=findViewById(R.id.findfriends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UserRef,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adpater=new
                FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {

                        holder.username.setText(model.getName());
                        holder.userstatus.setText(model.getStatus());

//                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profileimage).into(holder.userprofileimage);
                        Glide.with(FindFriends.this).load(model.getImage()).placeholder(R.drawable.profileimage).into(holder.userprofileimage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                String onineuserid=getRef(position).getKey().toString();

                                Intent intent=new Intent(FindFriends.this,ProfileActivity.class);
                                intent.putExtra("userid",onineuserid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplay,parent,false);

                        FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };

        findfriendlist.setAdapter(adpater);

        adpater.startListening();

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;
        CircleImageView userprofileimage;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);


            username=itemView.findViewById(R.id.userprofilename);
            userstatus=itemView.findViewById(R.id.userprofilestatus);
            userprofileimage=itemView.findViewById(R.id.recycler_userimage);




        }
    }
}
