package com.example.my_chat_app;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessageslist;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public MessageAdapter(List<Messages> userMessageslist){

        this.userMessageslist=userMessageslist;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custommessagelayout,parent,false);

        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        String senderId=mAuth.getCurrentUser().getUid();
        Messages message=userMessageslist.get(position);

        String fromUserId=message.getFrom();
        String messageType=message.getType();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image")){

                    String receiverimage=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverimage).placeholder(R.drawable.profileimage).into(holder.usermessageimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        holder.receivermessage.setVisibility(View.GONE);
        holder.usermessageimage.setVisibility(View.GONE);
        holder.sendermessage.setVisibility(View.GONE);
        holder.MessageSenderimageview.setVisibility(View.GONE);
        holder.MessageReceiverimageview.setVisibility(View.GONE);

        if (messageType.equals("text")){


            if (fromUserId.equals(senderId)){

                holder.sendermessage.setVisibility(View.VISIBLE);
                holder.sendermessage.setBackgroundResource(R.drawable.sendermessagelayout);

                holder.sendermessage.setText(message.getMessage()+"\n \n"+message.getTime()+"-"+message.getDate());
            }
            else {


                holder.receivermessage.setVisibility(View.VISIBLE);
                holder.usermessageimage.setVisibility(View.VISIBLE);

                holder.receivermessage.setBackgroundResource(R.drawable.receivermessagelayout);
                holder.receivermessage.setText(message.getMessage()+"\n \n"+message.getTime()+"-"+message.getDate());
            }
        }

        else if(messageType.equals("image")){

            if(fromUserId.equals(senderId)){

                holder.MessageSenderimageview.setVisibility(View.VISIBLE);

                Picasso.get().load(message.getMessage()).into(holder.MessageSenderimageview);
            }
            else {

                holder.MessageReceiverimageview.setVisibility(View.VISIBLE);
                holder.receivermessage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.MessageReceiverimageview);
            }
        }

        else {

            if(fromUserId.equals(senderId)) {

                holder.MessageSenderimageview.setVisibility(View.VISIBLE);
                holder.MessageSenderimageview.setBackgroundResource(R.drawable.file);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                     //   String amsg=userMessageslist.get(position).getMessage().toString();

                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageslist.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);

                    }
                });
            }

            else {


                holder.MessageReceiverimageview.setVisibility(View.VISIBLE);
                holder.receivermessage.setVisibility(View.VISIBLE);

                holder.receivermessage.setBackgroundResource(R.drawable.file);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(userMessageslist.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
return         userMessageslist.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView sendermessage,receivermessage;
        CircleImageView usermessageimage;

        public ImageView MessageSenderimageview;
        public ImageView MessageReceiverimageview;



        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sendermessage=itemView.findViewById(R.id.messagesendertextview);
            receivermessage=itemView.findViewById(R.id.messagereceivertextview);
            usermessageimage=itemView.findViewById(R.id.messageImage);
            MessageReceiverimageview=itemView.findViewById(R.id.messagereceiverimageview);
            MessageSenderimageview=itemView.findViewById(R.id.messagesenderimageview);



        }
    }
}
