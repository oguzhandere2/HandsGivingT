package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NeedyRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_request);

        Button locationButton = findViewById(R.id.button5);
        Button socialButton = findViewById(R.id.helprequest_button6);
        Button homePageButton = findViewById(R.id.helprequest_button7);
        Button profileButton = findViewById(R.id.helprequest_button8);
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyRequest.this, SocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        homePageButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( NeedyRequest.this, NeedyHomepage.class);
                                                  startActivity( intent);
                                              }
                                          }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyRequest.this, Profile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );
        locationButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyRequest.this, LocationProvider.class);
                                                 intent.putExtra("FROM_ACTIVITY", "NeedyRequest");
                                                 startActivity( intent);
                                             }
                                         }
        );
    }
}