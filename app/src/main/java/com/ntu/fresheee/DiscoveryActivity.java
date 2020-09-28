package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiscoveryActivity extends AppCompatActivity {

    private CardView updatePasswordcardView, blackBoardcardView, ntubusNowcardView,  ccacardView,
                    ntuLibrarycardView;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        updatePasswordcardView = (CardView) findViewById(R.id.update_password_cardView);
        updatePasswordcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiscoveryActivity.this, WebViewMainActivity.class);
                i.putExtra("url", "https://pwd.ntu.edu.sg/");
                startActivity(i);
            }
        });

        blackBoardcardView = (CardView) findViewById(R.id.blackboard_cardView);
        blackBoardcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean blackboard_installed = appInstalledOrNot("com.blackboard.android.bbstudent");
                if(blackboard_installed) {
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.blackboard.android.bbstudent"));
                }
                else {
                    Toast.makeText(DiscoveryActivity.this, "App not installed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.blackboard.android.bbstudent")));
                }
            }
        });

        ntubusNowcardView = (CardView) findViewById(R.id.ntu_busnow_cardView);
        ntubusNowcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean busnow_installed = appInstalledOrNot("pw.adithya.ntubusnow");
                if(busnow_installed) {
                    startActivity(getPackageManager().getLaunchIntentForPackage("pw.adithya.ntubusnow"));
                }
                else {
                    Toast.makeText(DiscoveryActivity.this, "App not installed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "pw.adithya.ntubusnow")));
                }
            }
        });

        ccacardView = (CardView) findViewById(R.id.cca_cardView);
        ccacardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiscoveryActivity.this, WebViewMainActivity.class);
                i.putExtra("url", "https://www.ntu.edu.sg/SAO/WhoWeAre/StudentOrganisations/StudentOrganisations/Pages/home.aspx");
                startActivity(i);
            }
        });

        ntuLibrarycardView = (CardView) findViewById(R.id.ntu_library_cardView);
        ntuLibrarycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiscoveryActivity.this, WebViewMainActivity.class);
                i.putExtra("url", "https://www.ntu.edu.sg/library/pages/default.aspx");
                startActivity(i);
            }
        });

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Discovery Selected
        bottomNavigationView.setSelectedItemId(R.id.discovery);

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
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager packageManager = getPackageManager();
        boolean app_installed = false;
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
        }
        else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}