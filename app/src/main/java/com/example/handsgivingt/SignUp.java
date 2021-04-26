package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;


public class SignUp extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailText,passwordText,userName,userSurname;
    private FirebaseFirestore fStore;
    private RadioGroup radioGroup;
    private String emailAd;
    private String userType;
    private Button locButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.editEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        userName = findViewById(R.id.editTextTextPersonName);
        userSurname = findViewById(R.id.editTextTextPersonName2);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroupMode);
        locButton = findViewById(R.id.button8);
        Button regbutton = findViewById(R.id.kayitolb);
        fStore = FirebaseFirestore.getInstance();
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locButtonClicked(v);
            }
        });
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpClicked(v);
            }
        });

    }

    private void locButtonClicked(View v) {
        Intent intent = new Intent( SignUp.this, LocationProvider.class);
        intent.putExtra("FROM_ACTIVITY", "SignUp");
        startActivity( intent);
    }

    public void returnSignIn(View view){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if( user != null){
            FirebaseAuth.getInstance().signOut();
        }
        Intent intent = new Intent( SignUp.this, MainActivity.class);
        startActivity( intent);
    }
    public void signUpClicked( View view){
        emailAd = emailText.getText().toString();
        String password = passwordText.getText().toString();
        userType = "";
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            // No item selected
        }
        else{
            if (checkedRadioButtonId == R.id.radioButton) {
                userType = "Needy";
            }
            else{
                userType = "Volunteer";
            }

        }
        firebaseAuth.createUserWithEmailAndPassword(emailAd,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        createUserColl();
                        Toast.makeText(SignUp.this,
                                "Hesap doğrulama linkiniz mail adresinize gönderilmiştir.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void createUserColl(){


        final String userN = userName.getText().toString();
        final String userS = userSurname.getText().toString();
        double def = 0.0;

        HashMap<String,Object> postdata = new HashMap<>();
        postdata.put("Email", emailAd);
        postdata.put("Name", userN);
        postdata.put("Surname",userS);
        postdata.put("UserType", userType);

        Intent mIntent = getIntent();

        postdata.put("LocationDesc", mIntent.getStringExtra("Address"));
        postdata.put("Location", new GeoPoint(mIntent.getDoubleExtra("Latitude", def), mIntent.getDoubleExtra("Longitude", def)));
        postdata.put("AveragePoint", 0);
        fStore.collection("User").add(postdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

            @Override
            public void onSuccess(DocumentReference documentReference) {

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                HashMap<String, Object> profileMap = new HashMap<>();
                profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                profileMap.put("name", userN);
                profileMap.put("surname", userS);
                profileMap.put("userType", userType);
                userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap);

                if(userType == "Needy")
                {
                    Intent intent = new Intent(SignUp.this, NeedyMainBottomNav.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(SignUp.this, NeedyMainBottomNav.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}