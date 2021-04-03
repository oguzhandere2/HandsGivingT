package com.example.handsgivingt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button ResetEmailButton;
    private EditText mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        TextView textView = (TextView) findViewById(R.id.textView5);
        textView.setText(" Lütfen sağlanan alana uygulamada kayıtlı olan mail adresini giriniz.");
        ResetEmailButton = (Button) findViewById(R.id.button2);
        mail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        mAuth = FirebaseAuth.getInstance();


        ResetEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMail = mail.getText().toString();
                if (TextUtils.isEmpty(userMail)){
                    Toast.makeText(ForgetPassword.this, "Lütfen Mail Adresinizi giriniz.", Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if( task.isSuccessful()){
                                Toast.makeText(ForgetPassword.this, "Şifrenizi güncellemek için lütfen Mail Adresinizi kontrol ediniz.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgetPassword.this, MainActivity.class));
                            }
                            else{
                                Toast.makeText(ForgetPassword.this, "Hata gerçekleşti, lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
