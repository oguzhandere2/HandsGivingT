package com.example.handsgivingt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static org.webrtc.ContextUtils.getApplicationContext;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NeedySocialPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NeedySocialPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView myContactsList;
    ImageView findPeopleBtn, notificationsBtn;
    private DatabaseReference contactsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userName = "", calledBy = "";
    private String currentUserType = "";
    public Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NeedySocialPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NeedySocialPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NeedySocialPageFragment newInstance(String param1, String param2) {
        NeedySocialPageFragment fragment = new NeedySocialPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkForReceivingCall();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsRef.child(currentUserId), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int i, @NonNull Contacts contacts) {
                final String listUserId = getRef(i).getKey();
                usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            String nameStr = dataSnapshot.child("name").getValue().toString();
                            String surNameStr = dataSnapshot.child("surname").getValue().toString();
                            userName = nameStr + " " + surNameStr;
                            holder.userNameTxt.setText(userName);

                            final String userIdStr = dataSnapshot.child("uid").getValue().toString();
                            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Images");
                            StorageReference islandRef = mStorageRef.child(userIdStr + ".jpg");
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    holder.profileImageView.setImageBitmap(bmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    StorageReference islandRef = mStorageRef.child(userIdStr + ".png");
                                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            holder.profileImageView.setImageBitmap(bmp);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            StorageReference islandRef = mStorageRef.child(userIdStr + ".jpeg");
                                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    holder.profileImageView.setImageBitmap(bmp);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                        holder.callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent( context, CallingActivity.class);
                                intent.putExtra("visit_user_id", listUserId);
                                startActivity(intent);
                            }
                        });
                        holder.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String nameStr = dataSnapshot.child("name").getValue().toString();
                                String surNameStr = dataSnapshot.child("surname").getValue().toString();
                                String result = nameStr + " " + surNameStr;

                                Intent intent = new Intent( context, ChatActivity.class);
                                intent.putExtra("visit_user_id", listUserId);
                                intent.putExtra("visit_user_name", result);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        myContactsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTxt;
        Button callBtn, sendMessageBtn;
        ImageView profileImageView;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.name_contact);
            callBtn = itemView.findViewById(R.id.call_btn);
            sendMessageBtn = itemView.findViewById(R.id.message_btn);
            profileImageView = itemView.findViewById(R.id.image_contact);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void checkForReceivingCall() {
        usersRef.child(currentUserId).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("ringing"))
                {
                    calledBy = dataSnapshot.child("ringing").getValue().toString();
                    Intent intent = new Intent( context, CallingActivity.class);
                    intent.putExtra("visit_user_id", calledBy);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        View view = inflater.inflate(R.layout.fragment_needy_social_page, container, false);
        findPeopleBtn = view.findViewById(R.id.find_people_btn);
        notificationsBtn = view.findViewById(R.id.notif_btn);
        myContactsList = view.findViewById(R.id.contact_list);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserType = snapshot.child(currentUserId).child("userType").getValue().toString();
                        if( currentUserType.equals("Needy")) {
                            Intent intent = new Intent(context, FindPeopleActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(context, NotificationsActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


//        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent( ContactsActivity.this, FindPeopleActivity.class);
//                startActivity( intent);
//            }
//        });

//        notificationsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent( ContactsActivity.this, NotificationsActivity.class);
//                startActivity( intent);
//            }
//        });


        return view;
    }
}