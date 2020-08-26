package com.example.my_chat_app;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

 RecyclerView chatrecyclerview;
View view;

    private DatabaseReference Contactref,Userref;
    private FirebaseAuth      mAuth;
    private  String currentUserId;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         view= inflater.inflate(R.layout.fragment_chat, container, false);

        chatrecyclerview=view.findViewById(R.id.chat_recyclerview);
        chatrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth= FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid().toString();
        Contactref=FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        Userref=FirebaseDatabase.getInstance().getReference().child("Users");
    return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactref,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {

                final String UserIds=getRef(position).getKey().toString();
                final String[] profileimage = {"default_image"};

                Userref.child(UserIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            if (dataSnapshot.hasChild("image")) {

                                 profileimage[0] = dataSnapshot.child("image").getValue().toString();

                                Picasso.get().load(profileimage[0]).placeholder(R.drawable.profileimage).into(holder.userimage);

                            }

                            final String profilename = dataSnapshot.child("name").getValue().toString();
                         final    String profilestatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);


                            if(dataSnapshot.child("userState").hasChild("status")){

                                String state=dataSnapshot.child("userState").child("status").getValue().toString();
                                String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                String time=dataSnapshot.child("userState").child("time").getValue().toString();


                                if(state.equals("Online")){

                                    holder.userstatus.setText("Online");
                                }
                                else if (state.equals("Offline")){

                                    holder.userstatus.setText("Last seen: "+ date +" "+time);
                                }

                            }

                            else {

                                holder.userstatus.setText("Offline");
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent=new Intent(getContext(),Chat_Activity.class);
                                    intent.putExtra("visitID",UserIds);
                                    intent.putExtra("visitName",profilename);
                                    intent.putExtra("visitimage", profileimage[0]);
                                    startActivity(intent);
                                }
                            });
                        }


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplay,parent,false);
                ChatViewHolder chatViewHolder=new ChatViewHolder(view);

                return chatViewHolder;
            }
        };

        chatrecyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView username,userstatus;
        CircleImageView userimage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.userprofilename);
            userstatus=itemView.findViewById(R.id.userprofilestatus);
            userimage=itemView.findViewById(R.id.recycler_userimage);

        }
    }
    }

