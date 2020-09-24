package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewTravelDeclarationActivity extends AppCompatActivity {

    private WebView travelDeclarationwebView;
    private String url = "https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=2&p2=https://wis.ntu.edu.sg/pls/webexe88/declare_overseas.plans&extra=&pg=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_travel_declaration);

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

        travelDeclarationwebView = (WebView) findViewById(R.id.travel_declaration_webview);
        travelDeclarationwebView.setWebViewClient(new WebViewClient());
        travelDeclarationwebView.getSettings().setJavaScriptEnabled(true);
        travelDeclarationwebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(travelDeclarationwebView.canGoBack()) {
            travelDeclarationwebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}