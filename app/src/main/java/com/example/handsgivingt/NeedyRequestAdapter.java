package com.example.handsgivingt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class NeedyRequestAdapter extends ArrayAdapter<JSONObject> {

    //the list values in the List of type hero
    List<JSONObject> heroList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;
    private FirebaseFunctions mFunctions;


    //constructor initializing the values
    public NeedyRequestAdapter(Context context, int resource, List<JSONObject> heroList) {
        super(context, resource, heroList);
        this.context = context;
        this.resource = resource;
        this.heroList = heroList;
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
        Button buttonDelete = view.findViewById(R.id.rowAcceptButton);

        //getting the hero of the specified position
        final JSONObject hero = heroList.get(position);

        String reqID = "";
        try {
            reqID = hero.getString("RequestID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalReqID = reqID;

        try {
            if( hero.getBoolean("Accepted"))
            {
                buttonDelete.setBackgroundResource(R.drawable.volnearreqbackaccept);
                buttonDelete.setText("Bitir");
                String volunteerMail = hero.getString("volunteerEmail");


                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishRequest(finalReqID);
                    }
                });

                Map<String, Object> data = new HashMap<>();
                data.put("email", volunteerMail);
                final String finalEmailo = volunteerMail;
                mFunctions.getHttpsCallable("getCurrentUserInfo")
                        .call(data)
                        .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                try{
                                    Gson g = new Gson();
                                    String json = g.toJson(httpsCallableResult.getData());
                                    JSONObject jsonObject = new JSONObject(json);
                                    textViewVolunteerName.setText(jsonObject.getJSONObject(finalEmailo).getString("Name") + " "+ jsonObject.getJSONObject(finalEmailo).getString("Surname")) ;
                                    textViewRequestype.setText(hero.getString("RequestType"));
                                } catch (Exception e){
                                    Log.d("Error",e.toString());
                                }
                            }
                        });
            }
            else
            {
                buttonDelete.setBackgroundResource(R.drawable.neaddel);
                buttonDelete.setText("Sil");

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteRequest(finalReqID);
                    }
                });

                textViewRequestype.setText(hero.getString("RequestType"));
                textViewVolunteerName.setText("Bekliyor");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("@@@@@","LIST ITEM CLICKED");
            }
        });
        return view;
    }

    private void finishRequest(String reqID) {
        Map<String,Object> data = new HashMap<>();
        data.put("requestID", reqID);

        mFunctions.getHttpsCallable("finishRequest")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(context, "Yardım İsteğiniz Tamamlanmıştır...", Toast.LENGTH_SHORT).show();
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

    private void deleteRequest(String reqID) {
        Map<String,Object> data = new HashMap<>();

        data.put("requestID", reqID);

        mFunctions.getHttpsCallable("deleteRequest")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Toast.makeText(context, "Yardım İsteğiniz Silinmiştir...", Toast.LENGTH_SHORT).show();
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
                });
    }
}
