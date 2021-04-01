package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SocialPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_page);
        Button homePageButton = findViewById(R.id.social_button7);
        Button profileButton = findViewById(R.id.social_button8);
        homePageButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( SocialPage.this, Homepage.class);
                                                startActivity( intent);
                                            }
                                        }
        );
        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( SocialPage.this, Profile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );


    }
}