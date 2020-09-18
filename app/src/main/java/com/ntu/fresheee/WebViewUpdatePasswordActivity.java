package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewUpdatePasswordActivity extends AppCompatActivity {

    private WebView updatePasswordwebView;
    private String url = "https://pwd.ntu.edu.sg/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_update_password);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.discovery:
                        startActivity(new Intent(getApplicationContext(), DiscoveryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        updatePasswordwebView = (WebView) findViewById(R.id.update_password_webview);
        updatePasswordwebView.setWebViewClient(new WebViewClient());
        updatePasswordwebView.getSettings().setJavaScriptEnabled(true);
        updatePasswordwebView.loadUrl(url);
    }
    @Override
    public void onBackPressed() {
        if(updatePasswordwebView.canGoBack()) {
            updatePasswordwebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}