package com.example.handsgivingt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
public class NeedyPreviousRequestsAdapter extends ArrayAdapter<JSONObject> {

    //the list values in the List of type hero
    List<JSONObject> heroList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;
    private FirebaseFunctions mFunctions;


    //constructor initializing the values
    public NeedyPreviousRequestsAdapter(Context context, int resource, List<JSONObject> heroList) {
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
        final TextView textViewRequestype = view.findViewById(R.id.rowPrevRequestType);
        final TextView textViewVolunteerName = view.findViewById(R.id.rowPrevVolunteerName);


        //getting the hero of the specified position
        final JSONObject hero = heroList.get(position);

        try {
            if( hero.getBoolean("Accepted"))
            {
                String volunteerMail = hero.getString("volunteerEmail");

                Map<String, Object> data = new HashMap<>();
                data.put("email", volunteerMail);
                final String finalEmailo = volunteerMail;
                mFunctions.getHttpsCallable("getCurrentUserInfo")
                        .call(data)
                        .addOnSuccessListener((Executor) this, new OnSuccessListener<HttpsCallableResult>() {
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
                textViewRequestype.setText(hero.getString("RequestType"));
                textViewVolunteerName.setText("Bekliyor");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //finally returning the view

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("@@@@@","LIST ITEM CLICKED");
            }
        });
        return view;
    }


}
