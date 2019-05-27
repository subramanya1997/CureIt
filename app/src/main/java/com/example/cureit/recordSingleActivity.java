package com.example.cureit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recordSingleActivity extends AppCompatActivity {

    private TextView mPrescriptionText, mDoctorsnameText, mDateText, mDescriptionText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser mCureentUser;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_record_single );

        String recordID = getIntent().getExtras().getString("recordID");

        //User Data
        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Records" ).child( user_id ).child( recordID );

        mDescriptionText = (TextView) findViewById( R.id.descriptionText );
        mDoctorsnameText = (TextView) findViewById( R.id.doctorNameText );
        mDateText = (TextView) findViewById( R.id.dateOfRecordText );
        mPrescriptionText = (TextView) findViewById( R.id.prescriptionText );
        // display independent records

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String description = dataSnapshot.child( "description" ).getValue(String.class);
                String doctorName = dataSnapshot.child( "doctorsName" ).getValue(String.class);
                String dateOfRecord = dataSnapshot.child( "date" ).getValue(String.class);
                String prescription = dataSnapshot.child( "prescription_Text" ).getValue(String.class);

                mDescriptionText.setText( description );
                mDoctorsnameText.setText( doctorName );
                mDateText.setText( dateOfRecord );
                mPrescriptionText.setText( prescription );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
