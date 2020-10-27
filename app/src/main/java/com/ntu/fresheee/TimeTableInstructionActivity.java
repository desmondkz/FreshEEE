package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TimeTableInstructionActivity extends AppCompatActivity {

    private Button step_1;
    private Button step_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_instruction);

        getSupportActionBar().hide();

        step_1 = (Button) findViewById(R.id.step_1);
        step_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=1&p2=https://wish.wis.ntu.edu.sg/pls/webexe/aus_stars_check.check_subject_web2&extra=&pg="));
                startActivity(i);
            }
        });

        step_2 = (Button) findViewById(R.id.step_2);
        step_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeTableInstructionActivity.this, TimeTablePasteActivity.class));
            }
        });
    }
}