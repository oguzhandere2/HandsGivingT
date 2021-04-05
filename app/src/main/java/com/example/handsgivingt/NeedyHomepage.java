package com.example.handsgivingt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NeedyHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_homepage);
        Button socialButton = findViewById(R.id.homepage_button6);
        Button profileButton = findViewById(R.id.homepage_button8);
        Button reqBB = findViewById(R.id.needyReq);
        Button feedbackButton = findViewById(R.id.homepage_button2);

        feedbackButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyHomepage.this, FeedbackUser.class);
                                                startActivity( intent);
                                            }
                                        }
        );
        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyHomepage.this, NeedySocialPage.class);
                                                startActivity( intent);
                                            }
                                        }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyHomepage.this, NeedyProfile.class);
                                                 startActivity( intent);
                                             }
                                         }
        );

        reqBB.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 Intent intent = new Intent( NeedyHomepage.this, NeedyRequest.class);
                                                 startActivity( intent);
                                             }
                                         }
        );
    }
}