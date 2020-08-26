package com.example.my_chat_app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    View Contactview;
    RecyclerView contactlist;
    DatabaseReference Contactsref,UserRef;

    FirebaseAuth mAuth;
    String currentUserId;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Contactview= inflater.inflate(R.layout.fragment_contact, container, false);
        contactlist=Contactview.findViewById(R.id.contacts_recyclerlist);
        contactlist.setLayoutManager(new LinearLayoutManager(getContext()));


        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid().toString();
        Contactsref= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);


    return Contactview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ContactsviewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ContactsviewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsviewHolder holder, int position, @NonNull Contacts model) {

                        String userIds=getRef(position).getKey();

                        UserRef.child(userIds).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (dataSnapshot.exists()){


                                    if(dataSnapshot.child("userState").hasChild("status")){

                                        String state=dataSnapshot.child("userState").child("status").getValue().toString();
                                        String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time=dataSnapshot.child("userState").child("time").getValue().toString();


                                        if(state.equals("Online")){

                                            holder.onlinestatus.setVisibility(View.VISIBLE);
                                        }
                                        else if (state.equals("Offline")){

                                            holder.onlinestatus.setVisibility(View.INVISIBLE);
                                        }

                                    }

                                    else {

                                        holder.onlinestatus.setVisibility(View.INVISIBLE);
                                    }



                                    if (dataSnapshot.hasChild("image")){

                                        String profileImage=dataSnapshot.child("image").getValue().toString();
                                        String profilename=dataSnapshot.child("name").getValue().toString();
                                        String profilestatus=dataSnapshot.child("status").getValue().toString();

                                        holder.contactname.setText(profilename);
                                        holder.contactstatus.setText(profilestatus);

                                        Picasso.get().load(profileImage).placeholder(R.drawable.profileimage).into(holder.contactimage);

                                    }

                                    else {

                                        String profilename=dataSnapshot.child("name").getValue().toString();
                                        String profilestatus=dataSnapshot.child("status").getValue().toString();

                                        holder.contactname.setText(profilename);
                                        holder.contactstatus.setText(profilestatus);

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
                    public ContactsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View viewhold=LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplay,parent,false);

                        ContactsviewHolder contactsviewHolder=new ContactsviewHolder(viewhold);

                        return contactsviewHolder;
                    }
                };

        contactlist.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsviewHolder extends RecyclerView.ViewHolder{

        TextView contactname,contactstatus;
        CircleImageView contactimage;
        ImageView onlinestatus;

        public ContactsviewHolder(@NonNull View itemView) {
            super(itemView);

            contactname=itemView.findViewById(R.id.userprofilename);
            contactstatus=itemView.findViewById(R.id.userprofilestatus);
            contactimage=itemView.findViewById(R.id.recycler_userimage);
            onlinestatus=itemView.findViewById(R.id.useronlinestatus);
        }
    }
}
