package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class TimeTablePasteActivity extends AppCompatActivity {

    private EditText editTextPasteTimetable;
    private Button btnGenerate_timetable;

    private FirebaseUser fbuser;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_paste);

        getSupportActionBar().hide();

        LoadingDialog loadingDialog = new LoadingDialog(TimeTablePasteActivity.this);

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
            }
        });

        btnGenerate_timetable = (Button) findViewById(R.id.generate_timetable);
        btnGenerate_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPasteTimetable.length() == 0) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TimeTablePasteActivity.this);
                    builder.setTitle("Generate Timetable");
                    builder.setMessage("Please paste your copied timetable in the area above.");
                    builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
                else {
                    loadingDialog.startLoadingDialog();

                    fbuser = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    userID = fbuser.getUid();

                    final String pasteTimetable = editTextPasteTimetable.getText().toString();
                    TimetableParser timetableParser = new TimetableParser();
                    timetableParser.buildTimetable(pasteTimetable);

                    Intent intent = new Intent(TimeTablePasteActivity.this, TimeTableMainActivity.class);
                    intent.putExtra("timetableParser", (Serializable) timetableParser);

                    reference.child(userID).child("timetable").setValue(timetableParser.courses);

                    startActivity(intent);
                    loadingDialog.dismissDialog();
                    finish();
                }
            }
        });
    }
}