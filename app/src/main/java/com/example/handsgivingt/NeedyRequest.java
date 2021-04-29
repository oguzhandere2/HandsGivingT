/*package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class NeedyRequest extends AppCompatActivity {
    private TextView locationDesc, latitude, longitude, request;
    private RadioGroup radioGroup;
    private Button requestButton;
    private FirebaseFunctions mFunctions;
    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_request);

        radioGroup = (RadioGroup)findViewById(R.id.requestType);
        request = findViewById(R.id.requestDesc);
        requestButton = findViewById(R.id.requestButton);
        mFunctions = FirebaseFunctions.getInstance();



        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAdd();
            }
        });

        Button locationButton = findViewById(R.id.button5);
        Button socialButton = findViewById(R.id.helprequest_button6);
        Button homePageButton = findViewById(R.id.helprequest_button7);
        Button profileButton = findViewById(R.id.helprequest_button8);
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyRequest.this, NeedySocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        homePageButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( NeedyRequest.this, NeedyHomepage.class);
                                                  startActivity( intent);
                                              }
                                          }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyRequest.this, NeedyProfile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );
        locationButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyRequest.this, LocationProvider.class);
                                                 intent.putExtra("FROM_ACTIVITY", "NeedyRequest");
                                                 startActivity( intent);
                                             }
                                         }
        );
    }

    private void requestAdd()
    {
        String requestS = request.getText().toString();
        String requestType = "";
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        double def = 0.0;
        Map<String,Object> data = new HashMap<>();
        Intent mIntent = getIntent();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailo = "";
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                emailo = profile.getEmail();
            }
        }

        if (checkedRadioButtonId != -1) {
            if (checkedRadioButtonId == R.id.radioButton) {
                requestType = "Maddi Yardim";
            } else {
                requestType = "Fiziksel Yardim";
            }
        }

        data.put("LocationDesc", mIntent.getStringExtra("Address"));
        data.put("Latitude", mIntent.getDoubleExtra("Latitude", def));
        data.put("Longitude", mIntent.getDoubleExtra("Longitude", def));
        data.put("RequestType", requestType);
        data.put("RequestDesc", requestS);
        data.put("email", emailo);

        mFunctions.getHttpsCallable("addNewRequest")
                .call(data)
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(NeedyRequest.this, "Yardım İsteğiniz Kaydedilmiştir", Toast.LENGTH_SHORT).show();
                            Fragment fragment = new Needy
                            loadFragment(fragment);
                            startActivity( intent);
                        } catch (Exception e){
                            Toast.makeText(NeedyRequest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NeedyRequest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}*/