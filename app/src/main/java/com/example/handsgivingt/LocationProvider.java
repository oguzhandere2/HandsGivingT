package com.example.handsgivingt;

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

public class LocationProvider extends AppCompatActivity implements LocationListener {
    private Button currentLocButton;
    private Button saveLocButton;
    private EditText location;
    LocationManager locationManager;
    private TextView textView_location;
    private String adressOfNeedy;
    private Object Tag;
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_provider);
        currentLocButton = findViewById(R.id.button6);
        saveLocButton = findViewById(R.id.button7);
        textView_location = findViewById(R.id.textView6);
        location = findViewById(R.id.editTextMulti);
        adressOfNeedy = "";
        if (ContextCompat.checkSelfPermission(LocationProvider.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LocationProvider.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        saveLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdress();
            }
        });
    }

    private void saveAdress() {
        if( location.getText().toString().isEmpty()){
            Toast.makeText(this, "Herhangi bir adres girmediniz lütfen adres girdikten sonra tekrar deneyiniz...", Toast.LENGTH_SHORT).show();
        }
        else if(loc == null)
        {
            Toast.makeText(this, "Bulunduğunuz konum tuşuna bastıktan sonra tekrar deneyiniz...", Toast.LENGTH_SHORT).show();
        }
        else {
            adressOfNeedy = location.getText().toString();
            Toast.makeText(this, location.getText().toString(), Toast.LENGTH_SHORT).show();
            Intent mIntent = getIntent();
            String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");
            Intent intent;
            if (previousActivity.equals("SignUp")) {
                intent = new Intent(LocationProvider.this, SignUp.class);
                intent.putExtra("Address", adressOfNeedy);
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                startActivity(intent);
            } /*else {
                intent = new Intent(LocationProvider.this, NeedyRequest.class);
                intent.putExtra("Address", adressOfNeedy);
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                startActivity(intent);
            }*/
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, LocationProvider.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        /*Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();*/
        try {
            loc = location;
            Geocoder geocoder = new Geocoder(LocationProvider.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);
            Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
            adressOfNeedy = address;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
