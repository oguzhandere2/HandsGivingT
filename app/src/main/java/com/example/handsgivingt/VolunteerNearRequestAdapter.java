package com.example.handsgivingt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


//we need to extend the ArrayAdapter class as we are building an adapter
public class VolunteerNearRequestAdapter extends ArrayAdapter<JSONObject> {

    //the list values in the List of type hero
    List<JSONObject> heroList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;
    private FirebaseFunctions mFunctions;
    private DatabaseReference contactsRef;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private String listUserId, userName, userSurname = "";
    String emailC;


    //constructor initializing the values
    public VolunteerNearRequestAdapter(Context context, int resource, List<JSONObject> heroList) {
        super(context, resource, heroList);
        this.context = context;
        this.resource = resource;
        this.heroList = heroList;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailo = "";
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                emailo = profile.getEmail();
            }
        }

        this.emailC = emailo;


    }

    //this will return the ListView Item as a View

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mFunctions = FirebaseFunctions.getInstance();
        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        final TextView textViewRequestype = view.findViewById(R.id.rowRequestType);
        final TextView textViewVolunteerName = view.findViewById(R.id.rowVolunteerName);
        Button buttonAccept = view.findViewById(R.id.rowAcceptButton);
        mAuth = FirebaseAuth.getInstance();

        //getting the hero of the specified position
        final JSONObject hero = heroList.get(position);


        Map<String, Object> data2 = new HashMap<>();
        String emailK = "";
        try {
            emailK = hero.getString("userEmail");
            data2.put("email", emailK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalEmailK = emailK;
        mFunctions.getHttpsCallable("getCurrentUserInfo")
                .call(data2)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);
                            userName = jsonObject.getJSONObject(finalEmailK).getString("Name");
                            userSurname = jsonObject.getJSONObject(finalEmailK).getString("Surname");
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                });

        try {
            textViewRequestype.setText(hero.getString("RequestType"));
            textViewVolunteerName.setText(hero.getString("LocationDesc"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String reqID = "";
        try {
            reqID = hero.getString("RequestID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalReqID = reqID;

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest(emailC, finalReqID);
            }
        });

        //finally returning the view

        String reqType = "";
        String reqDesc = "";
        String locDesc = "";
        Double lati = 0.0;
        Double longi = 0.0;
        final String volunteerName = "";
        final String requestStatus = "Bekliyor";

        try {
            reqType = hero.getString("RequestType");
            reqDesc = hero.getString("Description");
            locDesc = hero.getString("LocationDesc");
            lati = hero.getJSONObject("Location").getDouble("_latitude");
            longi = hero.getJSONObject("Location").getDouble("_longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String finalReqType = reqType;
        final String finalReqDesc = reqDesc;
        final Double finalLati = lati;
        final Double finalLongi = longi;
        final String finalLocDesc = locDesc;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new RequestDetailFragment(finalReqType, finalReqDesc, finalLati, finalLongi, finalLocDesc, volunteerName, requestStatus);
                loadFragment(fragment);
            }
        });
        return view;
    }
    private boolean loadFragment(Fragment fragment)
    {

        if (fragment != null) {
            ((AppCompatActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void acceptRequest(String email, String reqID) {
        Map<String,Object> data = new HashMap<>();

        data.put("requestID", reqID);
        data.put("email", email);

        mFunctions.getHttpsCallable("acceptRequest")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            addToContacts();
                        } catch (Exception e){
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addToContacts()
    {
        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();

        contactsRef = database2.getReference().child("Contacts");
        currentUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference ref = database2.getReference().child("Users").getRef();


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        System.out.println(ds);



                        Gson g = new Gson();
                        String json = g.toJson(ds.getValue());
                        JSONObject userI = new JSONObject();
                        String allName = "";
                        String allSurname = "";

                        try {
                            userI = new JSONObject(json);
                            allName = userI.getString("name");
                            allSurname = userI.getString("surname");

                            if(allName.equals(userName) && allSurname.equals(userSurname))
                            {
                                try {
                                    listUserId = userI.getString("uid");
                                    contactsRef.child(currentUserId).child(listUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                contactsRef.child(listUserId).child(currentUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            Toast.makeText(context, "Yardım Kabul Edilmiştir. Sosyal Sayfasından İhtiyaç Sahibiyle İletişime Geçebilirsiniz.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
