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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class WelcomeActivity extends AppCompatActivity {

    private Button toLogin;
    private Button bioLogin;
    private TextView msg_txt;


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
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // Initiate biometric button and textview
        bioLogin = (Button) findViewById(R.id.biometric_login);
        msg_txt = (TextView) findViewById(R.id.txt_msg);

        //Create BiometricManager and let's check is the user can use fingerprint sensor or not
        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                msg_txt.setText("You can use the fingerprint sensor to login");
                msg_txt.setTextColor(Color.parseColor("#000000"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                msg_txt.setText("The device does not have a fingerprint sensor");
                bioLogin.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msg_txt.setText("The biometric sensor is not available");
                bioLogin.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msg_txt.setText("Your device does not have any fingerprint saved");
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
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
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