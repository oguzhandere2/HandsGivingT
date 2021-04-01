package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Button registerButton2 = findViewById(R.id.kayitolbg);
        Button loginButton2 = findViewById(R.id.girisolb);
        loginButton2.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( MainActivity.this, Homepage.class);
                                                  startActivity( intent);
                                              }
                                          }
        );
        registerButton2.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View view)
                                               {
                                                   Intent intent = new Intent( MainActivity.this, SignUp.class);
                                                   startActivity( intent);
                                               }
                                           }
        );
    }
}
