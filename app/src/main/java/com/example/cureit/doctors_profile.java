package com.example.cureit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class doctors_profile extends AppCompatActivity {

    private ImageView mProfilePicture;
    private TextView mFullNameText,mUsernameText,mPhoneNumberFieldTextProfile,
            mAddressFieldTextProfile,mGenderFieldTextProfile;

    private Button mAppointment;

    private String doctorsID;
    private String user_id;
    String docUsername, docFullname, docAccountType_Username;
    String username, fullname, accountType_username;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase, mDocDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_doctors_profile );

        mProgress = new ProgressDialog(this);

        doctorsID = getIntent().getExtras().getString("doctorsID" );
        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Appointment" );

        mDocDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( doctorsID );

        mProfilePicture = (ImageView) findViewById( R.id.docProfilePicture );

        mFullNameText = (TextView) findViewById( R.id.docFullNameText );
        mUsernameText = (TextView) findViewById( R.id.docUsernameText );
        mPhoneNumberFieldTextProfile = (TextView) findViewById( R.id.docPhoneNumberFieldTextProfile );
        mAddressFieldTextProfile = (TextView) findViewById( R.id.docAddressFieldTextProfile );
        mGenderFieldTextProfile = (TextView) findViewById( R.id.docGenderFieldTextProfile );

        mAppointment = (Button) findViewById( R.id.appointment );
        mAppointment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppointment();
            }
        } );

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild( doctorsID )){
                    if (!dataSnapshot.hasChild( user_id )){
                        mAppointment.setText( "Get Appointment" );
                    }
                    else{
                        mAppointment.setText( "Cancel Appointment" );
                    }
                }else {
                    mAppointment.setText( "Cancel Appointment" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mDocDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docUsername = dataSnapshot.child( "username" ).getValue(String.class);
                String profilepicture = dataSnapshot.child( "profilePicture" ).getValue(String.class);
                docFullname = dataSnapshot.child( "fullName" ).getValue(String.class);
                String phonenumber = dataSnapshot.child( "phoneNumber" ).getValue(String.class);
                String address = dataSnapshot.child( "address" ).getValue(String.class);
                String gender = dataSnapshot.child( "gender" ).getValue(String.class);
                docAccountType_Username = dataSnapshot.child( "accountType_username" ).getValue(String.class);
                mUsernameText.setText( docUsername );
                mFullNameText.setText( docFullname );
                mPhoneNumberFieldTextProfile.setText( phonenumber );
                mAddressFieldTextProfile.setText( address );
                mGenderFieldTextProfile.setText( gender );
                Picasso.get().load( profilepicture ).fit().centerCrop().into( mProfilePicture );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void getAppointment(){
        mProgress.setMessage( mAppointment.getText() );
        mProgress.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat( "HH:mm:ss" );
        String currentTime = format.format( calendar.getTime() );

        DatabaseReference mUserData = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );

        mUserData.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child( "username" ).getValue(String.class);
                fullname = dataSnapshot.child( "fullName" ).getValue(String.class);
                accountType_username = dataSnapshot.child( "accountType_username" ).getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        if (mAppointment.getText() == "Get Appointment"){
            mDatabase.child( doctorsID ).child( user_id ).child( "currentTime" ).setValue( currentTime );
            mDatabase.child( doctorsID ).child( user_id ).child( "username" ).setValue( username );
            mDatabase.child( doctorsID ).child( user_id ).child( "accountType_username" ).setValue( accountType_username );
            mDatabase.child( doctorsID ).child( user_id ).child( "fullName" ).setValue( fullname );
            mDatabase.child( user_id ).child( doctorsID ).child( "currentTime" ).setValue( currentTime );
            mDatabase.child( user_id ).child( doctorsID ).child( "username" ).setValue( docUsername );
            mDatabase.child( user_id ).child( doctorsID ).child( "accountType_username" ).setValue( docAccountType_Username );
            mDatabase.child( user_id ).child( doctorsID ).child( "fullName" ).setValue( docFullname );

        }else{
            mDatabase.child( doctorsID ).child( user_id ).removeValue();
            mDatabase.child( user_id ).child( doctorsID ).removeValue();
        }

        Intent singleReportIntent = new Intent( doctors_profile.this, dashboard.class );
        startActivity( singleReportIntent );

        mProgress.dismiss();
    }
}
