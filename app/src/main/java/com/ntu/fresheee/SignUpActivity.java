package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ImageView logo;
    EditText emailId, password, fullName;
    Button btnSignUp;
    TextView tvSignIn;
    ProgressBar progressBar;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        fullName = findViewById(R.id.editTextTextPersonFullName);
        password = findViewById(R.id.editTextTextPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvSignIn = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailId.getText().toString().trim();
                final String fullname = fullName.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                if(email.isEmpty()) {
                    emailId.setError("Please Enter Your NTU email");
                    emailId.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailId.setError("Please provide valid email account");
                    emailId.requestFocus();
                    return;
                }

                if(fullname.isEmpty()) {
                    fullName.setError("Full Name is required");
                    fullName.requestFocus();
                    return;
                }

                if(pwd.isEmpty()) {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }
                if(pwd.length() < 6) {
                    password.setError("Min password length should be 6 characters");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(fullname, email);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this,"You have been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                Toast.makeText(SignUpActivity.this, "Failed to register, Try again!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    mFirebaseUser.sendEmailVerification();
                                    Toast.makeText(SignUpActivity.this,"Check you NTU email to verify your account!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Email already registered, Plase Login or try another email", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}