package com.example.handsgivingt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerHomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerHomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView;
    private FirebaseFunctions mFunctions;
    public Context context;

    private FirebaseAuth mAuth;

    Button evaluateButton;
    Button askForHelpButton;
    Button settingsButton;

    public VolunteerHomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NeedyHomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VolunteerHomepageFragment newInstance(String param1, String param2) {
        VolunteerHomepageFragment fragment = new VolunteerHomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_volunteer_homepage, container, false);
        listView = view.findViewById(R.id.pendingHelpRequestsListview);
        askForHelpButton = view.findViewById(R.id.request_help_button);
        evaluateButton = view.findViewById(R.id.evaluate_button);

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
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
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


        askForHelpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.wtf("@@@", "to be implemented");

            }
        });

        evaluateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.wtf("@@@", "EVAL CLICKED");
            }
        });

        Button signOut = view.findViewById(R.id.cikisButV);
        mAuth = FirebaseAuth.getInstance();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked(v);
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void signOutClicked(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent( context, MainActivity.class);
        startActivity( intent);
    }

    private void addElements(JSONObject dat, String email, Double lati, Double longi) throws JSONException {
        final List<JSONObject> requestList = new ArrayList<JSONObject>();

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("latitude", lati);
        data.put("longitude", longi);
        final String finalEmailo = email;
        mFunctions.getHttpsCallable("getNearRequests")
                .call(data)
                .addOnSuccessListener( new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject allRequests = new JSONObject(json);


                            Iterator<String> keys = allRequests.keys();
                            List<String> keys2 = new ArrayList<String>();

                            while(keys.hasNext()) {
                                String key = keys.next();
                                if (allRequests.get(key) instanceof JSONObject) {
                                    keys2.add(key);
                                }
                            }
                            for(int a = 0; a < keys2.size(); a++)
                            {
                                requestList.add(allRequests.getJSONObject(keys2.get(a)));

                            }

                            VolunteerNearRequestAdapter adapter = new VolunteerNearRequestAdapter(getActivity(), R.layout.near_request_custom_listview_layout, requestList);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.i("@@@@:", "Position: " + position);
                                }
                            });
                        } catch (Exception e){
                            Log.wtf("Error",e.toString());
                        }
                    }
                });
    }
}