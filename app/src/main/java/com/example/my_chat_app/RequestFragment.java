package com.example.my_chat_app;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {


    View requestview;
    RecyclerView requestlist;

    DatabaseReference ChatRequestRef,UserRef,ContactsRef;

    private FirebaseAuth mAuth;
    private  String currentUserId;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestview= inflater.inflate(R.layout.fragment_request, container, false);
    requestlist=requestview.findViewById(R.id.request_recyclerlist);
    requestlist.setLayoutManager(new LinearLayoutManager(getContext()));

    ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
    UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
    ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
    mAuth=FirebaseAuth.getInstance();
    currentUserId=mAuth.getCurrentUser().getUid().toString();

    return requestview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestviewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestviewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestviewHolder holder, int position, @NonNull Contacts model) {

                        holder.itemView.findViewById(R.id.Acceptbutton).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.Cancelbutton).setVisibility(View.VISIBLE);


                        final String list_userid=getRef(position).getKey();
                        DatabaseReference ReqtypeRef=getRef(position).child("request_type").getRef();

                        ReqtypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    final String getType=dataSnapshot.getValue().toString();

                                    if (getType.equals("received")){

                                        UserRef.child(list_userid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final String image=dataSnapshot.child("image").getValue().toString();


                                                    Picasso.get().load(image).placeholder(R.drawable.profileimage).into(holder.userrequestimage);


                                                }
                                                final String name=dataSnapshot.child("name").getValue().toString();
                                                final String status=dataSnapshot.child("status").getValue().toString();

                                                holder.userrequestname.setText(name);
                                                holder.userrequeststatus.setText(status);



                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        CharSequence option[]=new CharSequence[]
                                                                {
                                                                      "Accept",
                                                                      "Cancel"
                                                                };

                                                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                        builder.setTitle(name+" Chat Request");
                                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                           if (i==0){

                                                               ContactsRef.child(currentUserId).child(list_userid).child("Contacts")
                                                                       .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                       if (task.isSuccessful()){

                                                                           ContactsRef.child(list_userid).child(currentUserId).child("Contacts")
                                                                                   .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                                   if (task.isSuccessful()){

                                                                                       ChatRequestRef.child(currentUserId).child(list_userid)
                                                                                               .removeValue()
                                                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                   @Override
                                                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                                                       if (task.isSuccessful()){

                                                                                                           ChatRequestRef.child(list_userid).child(currentUserId)
                                                                                                                   .removeValue()
                                                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                       @Override
                                                                                                                       public void onComplete(@NonNull Task<Void> task) {

                                                                                                                           Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
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
                                                           if (i==1){

                                                               ChatRequestRef.child(currentUserId).child(list_userid)
                                                                       .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                       if (task.isSuccessful()){

                                                                           ChatRequestRef.child(list_userid).child(currentUserId)
                                                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                                   Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();

                                                                               }
                                                                           });
                                                                       }
                                                                   }
                                                               });


                                                           }

                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if(getType.equals("sent")){

                                    Button request_sent_btn=holder.itemView.findViewById(R.id.Acceptbutton);
                                    request_sent_btn.setText("Request Sent");

                                    holder.itemView.findViewById(R.id.Cancelbutton).setVisibility(View.INVISIBLE);



                                        UserRef.child(list_userid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final String image=dataSnapshot.child("image").getValue().toString();


                                                    Picasso.get().load(image).placeholder(R.drawable.profileimage).into(holder.userrequestimage);


                                                }
                                                final String name=dataSnapshot.child("name").getValue().toString();
                                                final String status=dataSnapshot.child("status").getValue().toString();

                                                holder.userrequestname.setText(name);
                                                holder.userrequeststatus.setText("You have sent request to "+name);



                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        CharSequence option[]=new CharSequence[]
                                                                {

                                                                        "Cancel Chat Request"
                                                                };

                                                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Already Sent Request");
                                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                                if (i==0){

                                                                    ChatRequestRef.child(currentUserId).child(list_userid)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()){

                                                                                ChatRequestRef.child(list_userid).child(currentUserId)
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        Toast.makeText(getContext(), "You Have Cancelled the Chat Request ", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });


                                                                }

                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public RequestviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplay,parent,false);
                        RequestviewHolder holder=new RequestviewHolder(view);

                        return holder;

                    }
                };

        requestlist.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestviewHolder extends RecyclerView.ViewHolder{

        TextView userrequestname,userrequeststatus;
        CircleImageView userrequestimage;
        Button reqacceptbtn,reqcancelbtn;

        public RequestviewHolder(@NonNull View itemView) {
            super(itemView);

        userrequestname=itemView.findViewById(R.id.userprofilename);
        userrequeststatus=itemView.findViewById(R.id.userprofilestatus);
        userrequestimage=itemView.findViewById(R.id.recycler_userimage);
        reqacceptbtn=itemView.findViewById(R.id.Acceptbutton);
        reqcancelbtn=itemView.findViewById(R.id.Cancelbutton);

        }
    }
}
