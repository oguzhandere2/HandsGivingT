package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID = "", receiverUserName = "";
    private ImageView background_profile_view;
    private TextView name_profile;
    private Button add_friend, decline_friend_request;
    private FirebaseAuth mAuth;
    private String senderUserId;
    private String currentState = "new";
    private DatabaseReference friendRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();
        background_profile_view = findViewById(R.id.background_profile_view);
        name_profile = findViewById(R.id.name_profile);
        add_friend = findViewById(R.id.add_friend);
        decline_friend_request = findViewById(R.id.decline_friend_request);
        name_profile.setText(receiverUserName);

        final String userIdStr = receiverUserID;
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        StorageReference islandRef = mStorageRef.child(userIdStr + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                background_profile_view.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                StorageReference islandRef = mStorageRef.child(userIdStr + ".png");
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        background_profile_view.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        StorageReference islandRef = mStorageRef.child(userIdStr + ".jpeg");
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                background_profile_view.setImageBitmap(bmp);
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

        manageClickEvents();
    }

    private void manageClickEvents()
    {
        friendRequestRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserID))
                {
                    String requestType = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();
                    if (requestType.equals("sent"))
                    {
                        currentState = "request_sent";
                        add_friend.setText("Cancel Friend Request");
                    }
                    else if (requestType.equals("received"))
                    {
                        currentState = "request_received";
                        add_friend.setText("Accept Friend Request");
                        decline_friend_request.setVisibility(View.VISIBLE);
                        decline_friend_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelFriendRequest();
                            }
                        });
                    }
                }
                else
                {
                    contactsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiverUserID))
                            {
                                currentState = "friends";
                                add_friend.setText("Delete Contact");
                            }
                            else
                            {
                                currentState = "new";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (senderUserId.equals(receiverUserID))
        {
            add_friend.setVisibility(View.GONE);
        }
        else
        {
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentState.equals("new"))
                    {
                        SendFriendRequest();
                    }
                    if (currentState.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (currentState.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (currentState.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                }
            });
        }
    }

    private void AcceptFriendRequest()
    {
        contactsRef.child(senderUserId).child(receiverUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    contactsRef.child(receiverUserID).child(senderUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                friendRequestRef.child(senderUserId).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            friendRequestRef.child(receiverUserID).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        currentState = "friends";
                                                        add_friend.setText("Delete Contact");
                                                        decline_friend_request.setVisibility(View.GONE);
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
            }
        });
    }

    private void CancelFriendRequest()
    {
        friendRequestRef.child(senderUserId).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendRequestRef.child(receiverUserID).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                currentState = "new";
                                add_friend.setText("Add Friend");
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendFriendRequest()
    {
        friendRequestRef.child(senderUserId).child(receiverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    friendRequestRef.child(receiverUserID).child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                currentState = "request_sent";
                                add_friend.setText("Cancel Friend Request");
                                Toast.makeText(ProfileActivity.this, "Friend Request Sent.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}