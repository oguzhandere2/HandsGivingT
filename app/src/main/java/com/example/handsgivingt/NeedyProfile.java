package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class NeedyProfile extends AppCompatActivity {

    ListView listView;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFunctions = FirebaseFunctions.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> data = new HashMap<>();

        mFunctions.getHttpsCallable("addMessage")
                .call()
                .addOnSuccessListener(this, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);
                            addElements(jsonObject);
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                });



        Button socialButton = findViewById(R.id.profile_button2);
        Button homePageButton = findViewById(R.id.profile_button3);
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyProfile.this, NeedySocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        homePageButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( NeedyProfile.this, NeedyHomepage.class);
                                                  startActivity( intent);
                                              }
                                          }
        );



    }
    private void addElements(JSONObject dat) throws JSONException {
        System.out.println(dat);
        /*listView = findViewById(R.id.completedRequests);
        ArrayList<String> elements = new ArrayList<String>();

        elements.add(dat.getString("email"));
        elements.add("Bilkent1/F1 Blok/Market");
        elements.add("Bilkent1/F1 Blok/Market");
        elements.add("Bilkent3/Ümit Sitesi");
        elements.add("Bilkent2/Yaşam Sitesi A2");
        elements.add("İncek/432. Sokak");
        elements.add("Çilek. Sokak");
        elements.add("Portakal. Sokak");
        elements.add("Ayva. Sokak");
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elements);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=itemsAdapter.getItem(position);
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();

            }
        });*/
    }

}
