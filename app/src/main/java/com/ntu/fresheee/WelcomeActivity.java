package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class WelcomeActivity extends AppCompatActivity {

    private Button toLogin;
    private Button bioLogin;

    SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        // Initiate Login button
        toLogin = findViewById(R.id.to_login);

        // when click on login button, go to login page
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If user already logged in, direct to home page
                if(sessionManager.getLogin()) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                //If not, direct to login page
                else {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
            }
        });

        // Initiate biometric button and textview
        bioLogin = (Button) findViewById(R.id.biometric_login);

        sessionManager = new SessionManager(getApplicationContext());

        bioLogin.setVisibility(View.VISIBLE);

        Executor executor = ContextCompat.getMainExecutor(this);
        final BiometricPrompt biometricPrompt = new BiometricPrompt(WelcomeActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if(sessionManager.getLogin()) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please use your email to login first", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use your fingerprint to login to the app")
                .setNegativeButtonText("Cancel")
                .build();

        bioLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }
}