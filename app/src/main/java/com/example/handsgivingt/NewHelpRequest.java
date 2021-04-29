package com.example.handsgivingt;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.functions.FirebaseFunctions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewHelpRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewHelpRequest extends Fragment implements LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button locationButton, submitButton;
    private RadioGroup radioGroup;
    private FirebaseFunctions mFunctions;
    private TextView locationDesc, requestDesc;
    private ImageView gifView;
    LocationManager locationManager;
    Location loc;
    private String adressOfNeedy;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewHelpRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewHelpRequest.
     */
    // TODO: Rename and change types and number of parameters
    public static NewHelpRequest newInstance(String param1, String param2) {
        NewHelpRequest fragment = new NewHelpRequest();
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
        View view = inflater.inflate(R.layout.fragment_new_help_request, container, false);

        locationButton = view.findViewById(R.id.locationButton);
        submitButton = view.findViewById(R.id.requestButton);
        requestDesc = view.findViewById(R.id.requestDesc2);
        locationDesc = view.findViewById(R.id.requestDesc3);
        gifView = view.findViewById(R.id.loadingImageView);
        radioGroup = view.findViewById(R.id.requestType);

        gifView.setVisibility(View.INVISIBLE);
        mFunctions = FirebaseFunctions.getInstance();

        locationButton.setTextColor(Color.BLACK);
        locationButton.setText("Konumumu al");
        locationButton.setBackgroundColor(Color.GRAY);
        locationButton.setClickable(true);


        loc = null;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        locationButton.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View view)
                                               {

                                                   locationButton.setText("Konum alınıyor...");
                                                   //gifView.setVisibility( View.VISIBLE);
                                                   locationButton.setClickable(false);

                                                   getLocation();

                                               }
                                           }
        );

        submitButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                String requestDescString = requestDesc.getText().toString();
                                                String locationDescString = locationDesc.getText().toString();
                                                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                String emailo = "";
                                                String requestType = "";
                                                if (user != null)
                                                {
                                                    for (UserInfo profile : user.getProviderData())
                                                    {
                                                          emailo = profile.getEmail();
                                                    }
                                                }
                                                if (checkedRadioButtonId != -1)
                                                {
                                                    if (checkedRadioButtonId == R.id.radioButton3)
                                                    {
                                                        requestType = "Maddi Yardım";
                                                    }
                                                    else
                                                    {
                                                          requestType = "Fiziksel Yardım";
                                                    }
                                                }


                                                if(requestDescString.matches("") || locationDescString.matches("") ||
                                                    emailo.matches("") || requestType.matches("") || loc == null)
                                                {
                                                    Toast.makeText(getContext(), "Lütfen gerekli alanları doldurunuz.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Map<String,Object> data = new HashMap<>();
                                                    data.put("LocationDesc", locationDescString);
                                                    data.put("Latitude", loc.getLatitude());
                                                    data.put("Longitude", loc.getLongitude());
                                                    data.put("RequestType", requestType);
                                                    data.put("RequestDesc", requestDescString);
                                                    data.put("email", emailo);
                                                    mFunctions.getHttpsCallable("addNewRequest")
                                                            .call(data)
                                                            .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                                                @Override
                                                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                                                    try{

                                                                        Toast.makeText(getContext(), "Yardım İsteğiniz Kaydedilmiştir.", Toast.LENGTH_SHORT).show();
                                                                        Fragment fragment = new NeedyHomepageFragment();
                                                                        loadFragment(fragment);

                                                                    } catch (Exception e){
                                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                }

                                              }
                                          }
        );

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, NewHelpRequest.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private boolean loadFragment(Fragment fragment)
    {

        if (fragment != null) {
            ((AppCompatActivity)getActivity()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        try {
            loc = location;
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            adressOfNeedy = address;
            gifView.setVisibility(View.INVISIBLE);
            locationButton.setText("Konum başarıyla alındı.");
            locationButton.setTextColor(Color.WHITE);
            locationButton.setBackgroundColor(Color.GREEN);
            locationButton.setVisibility(View.VISIBLE);
            locationButton.setClickable(false);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}