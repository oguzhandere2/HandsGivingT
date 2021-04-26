package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Homepage extends AppCompatActivity {
    private FirebaseFunctions mFunctions;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mFunctions = FirebaseFunctions.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailo = "";
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                emailo = profile.getEmail();
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("email", emailo);
        final String finalEmailo = emailo;
        mFunctions.getHttpsCallable("getCurrentUserInfo")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);

                            JSONObject loc = jsonObject.getJSONObject(finalEmailo).getJSONObject("Location");
                            Double lati = loc.getDouble("_latitude");
                            Double longi = loc.getDouble("_longitude");

                            addElements(jsonObject, finalEmailo, lati, longi);
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                });


        Button acceptReq = findViewById(R.id.acceptReq);
        acceptReq.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                acceptReq(finalEmailo, "1hakankaraagach@gmail.com");
                                            }
                                        }
        );

        Button socialButton = findViewById(R.id.homepage_button6);
        Button profileButton = findViewById(R.id.homepage_button8);
        Button signOut = findViewById(R.id.homepage_button1);
        mAuth = FirebaseAuth.getInstance();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked(v);
            }
        });
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( Homepage.this, SocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( Homepage.this, Profile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );
    }
    public void signOutClicked(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent( Homepage.this, MainActivity.class);
        startActivity( intent);
    }

    private void addElements(JSONObject dat, String email, Double lati, Double longi) throws JSONException {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("latitude", lati);
        data.put("longitude", longi);
        mFunctions.getHttpsCallable("getNearRequests")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);
                            System.out.println(jsonObject);

                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("hako");
                        Toast.makeText(Homepage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void acceptReq(String email, String reqID)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("requestID", reqID);

        mFunctions.getHttpsCallable("acceptRequest")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(Homepage.this, "Accepted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("hako");
                        Toast.makeText(Homepage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}