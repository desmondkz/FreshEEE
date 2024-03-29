package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp;
    private ImageView logo;
    private EditText editTextEmail,editTextFullName, editTextPassword;
    private TextView tvLogIn;
    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        loadingDialog = new LoadingDialog(SignUpActivity.this);

        // Setup SharedPreferences file to store user information locally
        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        logo = (ImageView) findViewById(R.id.logo);
        logo.setOnClickListener(this);

        btnSignUp = (Button) findViewById(R.id.buttonSignUp);
        btnSignUp.setOnClickListener(this);

        tvLogIn = (TextView) findViewById(R.id.to_login);
        tvLogIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextPassword = (EditText) findViewById(R.id.password);

        sessionManager = new SessionManager(getApplicationContext());

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, WelcomeActivity.class));
            }
        });

        tvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logo:
                startActivity(new Intent(this, WelcomeActivity.class));
                break;
            case R.id.buttonSignUp:
                signupUser();
                break;

        }
    }

    private void signupUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9]+@e.ntu.edu.sg";

        if(email.isEmpty()) {
            editTextEmail.setError("Please Enter Your NTU email");
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
        if(fullName.isEmpty()) {
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
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

        loadingDialog.startLoadingDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(fullName, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

                                        // Store user input user fullname and email into SharedPreferences
                                        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userName", fullName);
                                        editor.putString("email", email);
                                        editor.apply();

                                        sessionManager.setLogin(true);

                                        Toast.makeText(SignUpActivity.this,"You have been registered successfully", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismissDialog();
                                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                        fbuser.sendEmailVerification();
                                        Toast.makeText(SignUpActivity.this, "Please check your email to verify your account!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "Failed to register! Please try again", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismissDialog();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Failed to register! Please try again", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                });
    }
}