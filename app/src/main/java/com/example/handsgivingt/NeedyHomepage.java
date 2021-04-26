/*package com.example.handsgivingt;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NeedyHomepage extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    Button socialButton;
    Button profileButton;
    Button homepageButton;

    Fragment needyHomepageFragment;
    Fragment needySocialFragment;
    Fragment needyProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_homepage);

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
        mFunctions.getHttpsCallable("getUserOngoingRequests")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);
                            addElements(jsonObject, finalEmailo);
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                });

        Button socialButton = findViewById(R.id.homepage_button6);
        Button profileButton = findViewById(R.id.homepage_button8);
        Button reqBB = findViewById(R.id.needyReq);
        Button feedbackButton = findViewById(R.id.homepage_button2);
        Button delBut = findViewById(R.id.nhomepagedel);

        delBut.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
         reqFinisher("1hakankaraagach@gmail.com");
                                              }
                                          }
        );
=======
        socialButton = findViewById(R.id.needyhomepage_social_button);
        profileButton = findViewById(R.id.needyhomepage_profile_button);
        homepageButton = findViewById(R.id.needyhomepage_homepage_button);

        needyHomepageFragment = NeedyHomepageFragment.newInstance("","");




        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragments_frame, needyHomepageFragment, needyHomepageFragment.getTag())
                .commit();
>>>>>>> origin/oguzhan

        /*
        feedbackButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyHomepage.this, FeedbackUser.class);
                                                startActivity( intent);
                                            }
                                        }
        );

         */
        /*
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                needySocialFragment = NeedySocialPageFragment.newInstance("","");
                                                getSupportFragmentManager().beginTransaction()
                                                        .setReorderingAllowed(true)
                                                        .replace(R.id.fragments_frame, needySocialFragment, needySocialFragment.getTag())
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                        }
        );

        homepageButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 getSupportFragmentManager().beginTransaction()
                                                         .setReorderingAllowed(true)
                                                         .replace(R.id.fragments_frame, needyHomepageFragment, needyHomepageFragment.getTag())
                                                         .addToBackStack(null)
                                                         .commit();
                                             }
                                         }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 needyProfileFragment = NeedyProfileFragment.newInstance("","");
                                                 getSupportFragmentManager().beginTransaction()
                                                         .setReorderingAllowed(true)
                                                         .replace(R.id.fragments_frame, needyProfileFragment, needyProfileFragment.getTag())
                                                         .addToBackStack(null)
                                                         .commit();

                                             }
                                         }
        );


    }

    private void addElements(JSONObject dat, String email) throws JSONException {
        List<JSONObject> datL = new ArrayList<>();

        Iterator<String> keys = dat.keys();
        List<String> keys2 = new ArrayList<String>();

        while(keys.hasNext()) {
            String key = keys.next();
            if (dat.get(key) instanceof JSONObject) {
                keys2.add(key);
            }
        }
        for(int a = 0; a < keys2.size(); a++)
        {
            datL.add(dat.getJSONObject(keys2.get(a)));
            System.out.println(datL.get(a));
        }
    }

    private void reqDeleter()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("requestID", "1hakankaraagach@gmail.com");
        mFunctions.getHttpsCallable("deleteRequest")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(NeedyHomepage.this, "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("hako");
                Toast.makeText(NeedyHomepage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reqFinisher(String reqID)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("requestID", reqID);
        mFunctions.getHttpsCallable("finishRequest")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(NeedyHomepage.this, "Başarıyla Bitirildi", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("hako");
                Toast.makeText(NeedyHomepage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}*/