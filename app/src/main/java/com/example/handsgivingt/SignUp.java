package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import java.util.List;
import java.util.Locale;


public class SignUp extends AppCompatActivity implements LocationListener {

    private FirebaseAuth firebaseAuth;
    private EditText emailText,passwordText,userName,userSurname;
    private FirebaseFirestore fStore;
    private RadioGroup radioGroup;
    private String emailAd;
    private String userType;
    private Button locButton;
    private Location loc;
    LocationManager locationManager;
    String userN;
    String userS;
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
        loc = null;

        if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SignUp.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locButton.setText("Konum alınıyor...");
                //gifView.setVisibility( View.VISIBLE);
                locButton.setClickable(false);

                getLocation();
            }
        });
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpClicked(v);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, SignUp.this);

        }catch (Exception e){
            e.printStackTrace();
        }

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

        userN = userName.getText().toString();
        userS = userSurname.getText().toString();

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
        if(userType.matches("") || emailAd.matches("") || password.matches("") || loc == null
                || userN.matches("") || userS.matches(""))
        {
            Toast.makeText(SignUp.this,
                    "Lütfen bütün bilgileri girdiğinizden emin olun.",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
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


    }
    public void createUserColl(){

        double def = 0.0;

        HashMap<String,Object> postdata = new HashMap<>();
        postdata.put("Email", emailAd);
        postdata.put("Name", userN);
        postdata.put("Surname",userS);
        postdata.put("UserType", userType);


        postdata.put("Location", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
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
                    Intent intent = new Intent(SignUp.this, VolunteerMainBottomNav.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            loc = location;
            locButton.setText("Konum başarıyla alındı.");
            locButton.setTextColor(Color.WHITE);
            locButton.setBackgroundColor(Color.GREEN);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}