package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference reference;

    private TextView greeting;
    private String userID;
    private ImageView scanner;
    private Button btnMap;

    private CardView safeEntrycardView, healthDeclarationcardView;

    SessionManager sessionManager;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Initialize SharedPreferences and get stored username from local files and display it
        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
        final TextView welcomeUsernameTextView = (TextView) findViewById(R.id.welcome_username);
        welcomeUsernameTextView.setText(preferences.getString("userName", null) + " !");

        greeting = (TextView) findViewById(R.id.greeting);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        int hour = Integer.parseInt(dateTime);
        if(hour >= 0) {
            greeting.setText("Good Morning, ");
        }
        if(hour >= 12) {
            greeting.setText("Good Afternoon, ");
        }
        if(hour >= 18) {
            greeting.setText("Good Evening, ");
        }

        scanner = (ImageView) findViewById(R.id.buttonScanner);
        scanner.setOnClickListener(this);

//        btnMap = (Button) findViewById(R.id.safeEntry_map);
//        btnMap.setOnClickListener(this);

        safeEntrycardView = (CardView) findViewById(R.id.safe_entry_cardView);

        healthDeclarationcardView = (CardView) findViewById(R.id.travel_declaration_cardView);
        healthDeclarationcardView.setOnClickListener(this);

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
//            case R.id.safeEntry_map:
//                startActivity(new Intent(this, MapsActivity.class));
            case R.id.travel_declaration_cardView:
                startActivity(new Intent(this, WebViewHealthDeclarationActivity.class));
                break;
        }
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