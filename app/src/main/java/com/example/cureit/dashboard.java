package com.example.cureit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class dashboard extends AppCompatActivity {

    private CardView mMedicalRecordsButton, mDoctorsButton, mRenewMedicinesButton, mSetAppointmentsButton, mPatientButton;
    private FloatingActionButton mAddAlarmButton;
    private LinearLayout mProfilePicture;
    private ImageView mPicture;
    private TextView mUsername;


    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(dashboard.this, login.class ));
                }
            }
        };

        mPatientButton = (CardView) findViewById( R.id.setPatientButton );
        mSetAppointmentsButton = (CardView) findViewById( R.id.setAppointmentsButton );

        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );
        //Get Reference by Id
        mUsername = (TextView) findViewById( R.id.usernameText );
        mProfilePicture = (LinearLayout) findViewById( R.id.profilePicture );
        mPicture = (ImageView) findViewById( R.id.picture ) ;


        //Assign or set Username
        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child( "username" ).getValue(String.class);
                String profilepicture = dataSnapshot.child( "profilePicture" ).getValue(String.class);
                String accountType = dataSnapshot.child( "accountType" ).getValue(String.class).trim();
                mUsername.setText( username );
                Picasso.get().load( profilepicture ).fit().centerCrop().into( mPicture );
                if (!accountType.contentEquals( "Doctor" )){
                    mPatientButton.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        mMedicalRecordsButton = (CardView) findViewById( R.id.medicalRecordsButton );
        mDoctorsButton = (CardView) findViewById( R.id.doctorsButton );
        //mRenewMedicinesButton = (CardView) findViewById( R.id.renewMedicinesButton );

        mAddAlarmButton = (FloatingActionButton ) findViewById( R.id.addAlarmButton );



                // On click of each of the buttons
        mProfilePicture.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, account.class) );
            }
        } );

        mMedicalRecordsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleReportIntent = new Intent( dashboard.this, medicalrecords.class );
                singleReportIntent.putExtra( "userID", user_id );
                startActivity( singleReportIntent );
            }
        } );

        mDoctorsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, doctors.class) );
            }
        } );

        /*
        mRenewMedicinesButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, renewmedicines.class) );
            }
        } );*/

        mSetAppointmentsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, setappointment.class) );
            }
        } );


        mAddAlarmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, setalarm.class) );
            }
        } );

        mPatientButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(dashboard.this, patients.class) );
            }
        } );


    }
}
