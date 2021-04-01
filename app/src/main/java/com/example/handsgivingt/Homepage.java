package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Button socialButton = findViewById(R.id.homepage_button6);
        Button profileButton = findViewById(R.id.homepage_button8);
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( Homepage.this, SocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( Homepage.this, Profile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );
    }
}