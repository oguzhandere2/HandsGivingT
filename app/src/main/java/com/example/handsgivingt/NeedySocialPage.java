package com.example.handsgivingt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NeedySocialPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_social_page);
        Button homePageButton = findViewById(R.id.social_button7);
        Button profileButton = findViewById(R.id.social_button8);
        homePageButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedySocialPage.this, NeedyHomepage.class);
                                                startActivity( intent);
                                            }
                                        }
        );
        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedySocialPage.this, NeedyProfile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );


    }
}