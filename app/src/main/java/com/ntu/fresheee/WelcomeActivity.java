package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

        sessionManager = new SessionManager(getApplicationContext());

        // Initiate Login button
        toLogin = findViewById(R.id.to_login);

        if(sessionManager.getLogin()) {
            toLogin.setVisibility(View.INVISIBLE);

            SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
            final TextView welcomeBackTextView = (TextView) findViewById(R.id.user_name);
            welcomeBackTextView.setText(preferences.getString("userName", null));
        }

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

        //Create BiometricManager and let's check is the user can use fingerprint sensor or not
        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            //If phone does not have biometric sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                toLogin.setVisibility(View.VISIBLE);
                bioLogin.setVisibility(View.GONE);
                break;
            //If phone's biometric sensor faulty
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                toLogin.setVisibility(View.VISIBLE);
                bioLogin.setVisibility(View.GONE);
                break;
            //If phone does not have any biometric data stored
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                toLogin.setVisibility(View.VISIBLE);
                bioLogin.setVisibility(View.GONE);
                break;
        }


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