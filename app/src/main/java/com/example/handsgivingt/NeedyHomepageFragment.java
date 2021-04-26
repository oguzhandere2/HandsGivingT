package com.example.handsgivingt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NeedyHomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NeedyHomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView;
    private FirebaseFunctions mFunctions;

    Button evaluateButton;
    Button askForHelpButton;
    Button settingsButton;

    public NeedyHomepageFragment() {
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
    public static NeedyHomepageFragment newInstance(String param1, String param2) {
        NeedyHomepageFragment fragment = new NeedyHomepageFragment();
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
        final List<JSONObject> requestList = new ArrayList<JSONObject>();

        View view = inflater.inflate(R.layout.fragment_needy_homepage, container, false);
        listView = view.findViewById(R.id.pendingHelpRequestsListview);

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

                            NeedyRequestAdapter adapter = new NeedyRequestAdapter(getActivity(), R.layout.pending_request_custom_listview_layout, requestList);
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



        evaluateButton = view.findViewById(R.id.evaluate_button);
        askForHelpButton = view.findViewById(R.id.request_help_button);
        settingsButton = view.findViewById(R.id.settings_button);
        evaluateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.wtf("@@@", "EVAL CLICKED");
            }
        });

        askForHelpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.wtf("@@@", "askforhelp CLICKED");
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.wtf("@@@", "settingsbutton CLICKED");
            }
        });


        return view;
    }

}