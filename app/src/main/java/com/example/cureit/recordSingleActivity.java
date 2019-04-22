package com.example.cureit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class recordSingleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_record_single );

        String recordID = getIntent().getExtras().getString("recordID");

        // display independent records
    }
}
