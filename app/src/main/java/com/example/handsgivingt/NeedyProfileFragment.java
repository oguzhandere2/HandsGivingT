package com.example.handsgivingt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NeedyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NeedyProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView listView;
    TextView userNameSurname, userMail;
    private FirebaseFunctions mFunctions;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NeedyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NeedyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NeedyProfileFragment newInstance(String param1, String param2) {
        NeedyProfileFragment fragment = new NeedyProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_needy_profile, container, false);
        listView = view.findViewById(R.id.previousHelpsListview);
        userNameSurname = view.findViewById(R.id.userNameSurnameTW);
        userMail = view.findViewById(R.id.userMailTW);
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
        mFunctions.getHttpsCallable("getUserFinishedRequests")
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

                            NeedyPreviousRequestsAdapter adapter = new NeedyPreviousRequestsAdapter(getActivity(), R.layout.previous_request_custom_listview_layout, requestList);
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
        Map<String, Object> data2 = new HashMap<>();
        data2.put("email", emailo);
        mFunctions.getHttpsCallable("getCurrentUserInfo")
                .call(data2)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
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





        return view;
    }

    private void addElements(JSONObject dat, String email) throws JSONException {
        userNameSurname.setText(dat.getJSONObject(email).getString("Name") + " " + dat.getJSONObject(email).getString("Surname"));
        userMail.setText(dat.getJSONObject(email).getString("Email"));
    }

}