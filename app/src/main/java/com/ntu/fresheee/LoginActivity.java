package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    ImageView logo;
    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    ProgressBar progressBar;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.buttonSignIn);
        tvSignUp = findViewById(R.id.textView);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please Log In", Toast.LENGTH_SHORT).show();
                }
            }
        };

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()) {
                    emailId.setError("Please Enter Your NTU email");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please Enter Your password");
                    password.requestFocus();
                }
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                if(mFirebaseUser.isEmailVerified()) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                }
                                else {
                                    mFirebaseUser.sendEmailVerification();
                                    Toast.makeText(LoginActivity.this,"Check you NTU email to verify your account!", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSignUp =  new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}