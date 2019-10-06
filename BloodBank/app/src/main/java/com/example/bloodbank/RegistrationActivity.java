package com.example.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText emailEditText;
    EditText password;
    Button submitBtn;
    Button signOutBtn;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.name);
        emailEditText =  findViewById(R.id.email);
//        password = findViewById(R.id.passwordEditText);
        submitBtn = findViewById(R.id.sendOtpBtn);
//        signOutBtn = findViewById(R.id.);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
//                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
