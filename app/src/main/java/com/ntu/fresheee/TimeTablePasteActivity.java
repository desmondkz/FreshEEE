package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class TimeTablePasteActivity extends AppCompatActivity {

    private EditText editTextPasteTimetable;
    private Button btnGenerate_timetable;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_paste);

        editTextPasteTimetable = (EditText) findViewById(R.id.editTextTextMultiLine);
        editTextPasteTimetable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                try {
                    CharSequence clipboardText = clipboard.getPrimaryClip().getItemAt(0).getText();
                    editTextPasteTimetable.removeTextChangedListener(this);
                    editTextPasteTimetable.setText(clipboardText.toString());
                } catch (Exception e) {
                    return;
                }
//                System.out.println(editable);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btnGenerate_timetable = (Button) findViewById(R.id.generate_timetable);
        btnGenerate_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pasteTimetable = editTextPasteTimetable.getText().toString();
                Intent i = new Intent(TimeTablePasteActivity.this, TimeTableMainActivity.class);
                i.putExtra("pasteTimetable", pasteTimetable);
                startActivity(i);
            }
        });
    }
}