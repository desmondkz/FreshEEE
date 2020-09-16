package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogIn;
    private ImageView logo;
    private EditText editTextEmail, editTextPassword;
    private TextView tvSignUp, forgotPassword;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup SharedPreferences file to store user information locally
        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);


        logo = (ImageView) findViewById(R.id.logo);
        logo.setOnClickListener(this);
        tvSignUp = (TextView) findViewById(R.id.to_signup);
        tvSignUp.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        btnLogIn = (Button) findViewById(R.id.buttonLogIn);
        btnLogIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        //Initialize SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.to_signup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.buttonLogIn:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void userLogin() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9]+@e.ntu.edu.sg";


        if(email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email account");
            editTextEmail.requestFocus();
            return;
        }
        if(!email.matches(emailPattern)) {
            editTextEmail.setError("Please provide valid NTU email account");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                    if(fbuser.isEmailVerified()) {

                        // Store user input user fullname and email into SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", email);
                        editor.apply();
                        //Store login in session
                        sessionManager.setLogin(true);
                        //Redirect activity
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                    else {
                        fbuser.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Please check your email to verify your account and login!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        //If user already logged in
        if(sessionManager.getLogin()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }
}