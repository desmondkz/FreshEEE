package com.ntu.fresheee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private TextView greeting;
    private String userID;
    private ImageView scanner;

    private CardView safeEntrycardView, tempDeclarationcardView, timeTablecardView;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        //Initialize SharedPreferences and get stored username from local files and display it
        SharedPreferences preferences = getSharedPreferences("com.ntu.fresheee.users", MODE_PRIVATE);
        final TextView welcomeUsernameTextView = (TextView) findViewById(R.id.welcome_username);
        welcomeUsernameTextView.setText(preferences.getString("userName", null) + "!");

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
//                Intent i = new Intent(HomeActivity.this, WebViewActivity.class);
//                i.putExtra("url", "https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=1&p2=https://wis.ntu.edu.sg/pls/webexe/str_stud.BRANCH_STUD");
//                startActivity(i);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=1&p2=https://wis.ntu.edu.sg/pls/webexe/str_stud.BRANCH_STUD"));
                startActivity(i);
            }
        });

        timeTablecardView = (CardView) findViewById(R.id.timetable_cardView);
        timeTablecardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(HomeActivity.this);
                loadingDialog.startLoadingDialog();

                FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("Users");
                userID = fbuser.getUid();

                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if(userProfile.timetable.size() == 0) {
                            loadingDialog.dismissDialog();
                            startActivity(new Intent(HomeActivity.this, TimeTableIntroActivity.class));
                        }
                        else {
                            TimetableParser timetableParser = new TimetableParser();

                            for(DataSnapshot timetableSnapshot: snapshot.child("timetable").getChildren()) {
                                Course course = timetableSnapshot.getValue(Course.class);
                                timetableParser.courses.add(course);
                            }

                            Intent intent = new Intent(HomeActivity.this, TimeTableMainActivity.class);
                            intent.putExtra("timetableParser", (Serializable) timetableParser);
                            loadingDialog.dismissDialog();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this,"Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        ImageSlider imageSlider = findViewById(R.id.slider);
        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fresheee-2020.appspot.com/o/homepage_slider_image%2Fmask.png?alt=media&token=f8320019-d190-4d4a-827a-380895dcfbe2"));
        slideModelList.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fresheee-2020.appspot.com/o/homepage_slider_image%2Fsafe_entry.png?alt=media&token=b71f32ff-55b5-48f4-88ae-d08afc4e4aec"));
        slideModelList.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fresheee-2020.appspot.com/o/homepage_slider_image%2Fdistancing.png?alt=media&token=4b08f5a2-9e2d-4c0c-90e4-eb29ba2dc83f"));
        slideModelList.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fresheee-2020.appspot.com/o/homepage_slider_image%2Ftemperature.png?alt=media&token=34943cdd-e3aa-426f-a846-ff4c2a9f46ed"));
        slideModelList.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fresheee-2020.appspot.com/o/homepage_slider_image%2Fmax_five.png?alt=media&token=a52833b8-06dd-4526-9a22-20042bfc129a"));


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