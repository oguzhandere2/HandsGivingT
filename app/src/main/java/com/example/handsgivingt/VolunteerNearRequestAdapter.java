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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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

        //getting the hero of the specified position
        final JSONObject hero = heroList.get(position);

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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("@@@@@","LIST ITEM CLICKED");
            }
        });
        return view;
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
                            Toast.makeText(context, "Yardım Kabul Edilmiştir...", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });;
    }

}
