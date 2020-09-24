package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewNTULibraryActivity extends AppCompatActivity {

    private WebView ntuLibrarywebView;
    private String url = "https://www.ntu.edu.sg/library/pages/default.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_ntu_library);

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

        ntuLibrarywebView = (WebView) findViewById(R.id.ntu_library_webview);
        ntuLibrarywebView.setWebViewClient(new WebViewClient());
        ntuLibrarywebView.getSettings().setJavaScriptEnabled(true);
        ntuLibrarywebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(ntuLibrarywebView.canGoBack()) {
            ntuLibrarywebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}