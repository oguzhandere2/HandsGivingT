package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listView = findViewById(R.id.completedRequests);

        ArrayList<String> elements = new ArrayList<String>();
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
        });

        Button socialButton = findViewById(R.id.profile_button2);
        Button homePageButton = findViewById(R.id.profile_button3);
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( Profile.this, SocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        homePageButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( Profile.this, Homepage.class);
                                                startActivity( intent);
                                            }
                                        }
        );



    }

}
