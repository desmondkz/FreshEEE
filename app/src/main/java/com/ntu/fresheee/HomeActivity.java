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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference reference;

    private TextView greeting;
    private String userID;
    private ImageView scanner;

    private CardView safeEntrycardView, tempDeclarationcardView;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        int hour = Integer.parseInt(dateTime);
        if(hour >= 0 && hour <= 12) {
            greeting.setText("Good Morning, ");
        }
        if(hour >= 12 && hour <= 18) {
            greeting.setText("Good Afternoon, ");
        }
        if(hour >= 18) {
            greeting.setText("Good Evening, ");
        }

        scanner = (ImageView) findViewById(R.id.buttonScanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ScannerActivity.class));
            }
        });

        safeEntrycardView = (CardView) findViewById(R.id.safe_entry_cardView);
        safeEntrycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MapBoxActivity.class));
            }
        });

        tempDeclarationcardView = (CardView) findViewById(R.id.temp_declaration_cardView);
        tempDeclarationcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, WebViewActivity.class);
                i.putExtra("url", "https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=1&p2=https://wis.ntu.edu.sg/pls/webexe/str_stud.BRANCH_STUD");
                startActivity(i);
            }
        });

        ImageSlider imageSlider = findViewById(R.id.slider);
        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel("https://raw.githubusercontent.com/desmondkz/FreshEEE/master/img/mask.png?token=AJKB37SDSQEGSC2KR2MVYL27QUZ2E"));
        slideModelList.add(new SlideModel("https://raw.githubusercontent.com/desmondkz/FreshEEE/master/img/distancing.png?token=AJKB37QFNGPGDSO6OIGQ5VC7QUZ2M"));
        slideModelList.add(new SlideModel("https://raw.githubusercontent.com/desmondkz/FreshEEE/master/img/safe_entry.png?token=AJKB37UVNOHWECF5RYOEHMC7QUZ3A"));
        slideModelList.add(new SlideModel("https://raw.githubusercontent.com/desmondkz/FreshEEE/master/img/temperature.png?token=AJKB37R3KTHHQ3YHX3WRMOK7QUZ3E"));
        slideModelList.add(new SlideModel("https://raw.githubusercontent.com/desmondkz/FreshEEE/master/img/max_five.png?token=AJKB37XZTV5XWAGUHPEIG4K7QUZ2S"));


        imageSlider.setImageList(slideModelList, true);


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