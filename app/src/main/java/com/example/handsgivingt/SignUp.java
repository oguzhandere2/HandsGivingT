package com.example.handsgivingt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button registerButton = findViewById(R.id.kayitolb);
        Button loginButton = findViewById(R.id.girisolbk);
        registerButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( SignUp.this, NeedyHomepage.class);
                                                  startActivity( intent);
                                              }
                                          }
        );
        loginButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent intent = new Intent( SignUp.this, MainActivity.class);
                                                  startActivity( intent);
                                              }
                                          }
        );
    }
}