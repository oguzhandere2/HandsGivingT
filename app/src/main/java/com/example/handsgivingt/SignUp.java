package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailText,passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.editEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        Button regbutton = findViewById(R.id.kayitolb);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUpClicked(v);
            }
        });

    }
    public void returnSignIn(View view){
        Intent intent = new Intent( SignUp.this, MainActivity.class);
        startActivity( intent);
    }
    public void signUpClicked( View view){
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent( SignUp.this, NeedyHomepage.class);
                        startActivity( intent);
                        finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
}