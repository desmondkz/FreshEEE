package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewHealthDeclarationActivity extends AppCompatActivity {

    private WebView healthDeclarationwebView;
    private String url = "https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=2&p2=https://wis.ntu.edu.sg/pls/webexe88/declare_overseas.plans&extra=&pg=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_health_declaration);

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

        healthDeclarationwebView = (WebView) findViewById(R.id.health_declaration_webview);
        healthDeclarationwebView.setWebViewClient(new WebViewClient());
        healthDeclarationwebView.getSettings().setJavaScriptEnabled(true);
        healthDeclarationwebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(healthDeclarationwebView.canGoBack()) {
            healthDeclarationwebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}