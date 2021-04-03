package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Button socialButton = findViewById(R.id.homepage_button6);
        Button profileButton = findViewById(R.id.homepage_button8);
        Button signOut = findViewById(R.id.homepage_button1);
        mAuth = FirebaseAuth.getInstance();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked(v);
            }
        });
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
    public void signOutClicked(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent( Homepage.this, MainActivity.class);
        startActivity( intent);
    }
}