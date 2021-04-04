package com.example.handsgivingt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackUser extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button saveButton;
    private double rating;
    private String fbUser;
    private EditText feedbackUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        saveButton = findViewById(R.id.save_button);
        feedbackUser = findViewById(R.id.editMultiLine);
        ratingBar = (RatingBar) findViewById(R.id.simpleRatingBar);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnFeedback();
            }
        });
    }

    private void returnFeedback() {
        if( feedbackUser.getText().toString().isEmpty() || ratingBar.getRating() == 0.0){
            Toast.makeText(this,"Puanlama yapmayı veya geri bildirim yazmayı unuttunuz. Lütfen kontrol ediniz." , Toast.LENGTH_SHORT).show();
        }
        else{
            fbUser = feedbackUser.getText().toString();
            rating = ratingBar.getRating();
            startActivity(new Intent( FeedbackUser.this,NeedyHomepage.class));
            finish();
        }
    }
}
