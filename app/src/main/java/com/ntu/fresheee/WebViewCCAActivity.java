package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewCCAActivity extends AppCompatActivity {

    private WebView ccawebView;
    private String url = "https://www.ntu.edu.sg/SAO/WhoWeAre/StudentOrganisations/StudentOrganisations/Pages/home.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_cca);

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

        ccawebView = (WebView) findViewById(R.id.cca_webview);
        ccawebView.setWebViewClient(new WebViewClient());
        ccawebView.getSettings().setJavaScriptEnabled(true);
        ccawebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(ccawebView.canGoBack()) {
            ccawebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}