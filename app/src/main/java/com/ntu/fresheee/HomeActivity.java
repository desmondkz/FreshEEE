package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference reference;

    private String userID;
    private ImageView scanner;
    private Button btnMap;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Initialize SharedPreferences and get stored username from local files and display it
        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
        final TextView welcomeUsernameTextView = (TextView) findViewById(R.id.welcome_username);
        welcomeUsernameTextView.setText(preferences.getString("userName", null));

        scanner = (ImageView) findViewById(R.id.buttonScanner);
        scanner.setOnClickListener(this);

        btnMap = (Button) findViewById(R.id.safeEntry_map);
        btnMap.setOnClickListener(this);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Discovery Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
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


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonScanner:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.safeEntry_map:
                startActivity(new Intent(this, MapsActivity.class));
        }
    }
}