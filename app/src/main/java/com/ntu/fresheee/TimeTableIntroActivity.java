package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TimeTableIntroActivity extends AppCompatActivity {

    private Button btnaddTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_intro);

        btnaddTimetable = (Button) findViewById(R.id.buttonAddtimetable);
        btnaddTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeTableIntroActivity.this, TimeTableInstructionActivity.class));
            }
        });

    }
}