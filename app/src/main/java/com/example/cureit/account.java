package com.example.cureit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class account extends AppCompatActivity {

    private ImageView mProfilePicture;
    private TextView mFullNameText,mUsernameText,mPhoneNumberFieldTextProfile,
            mAddressFieldTextProfile,mGenderFieldTextProfile,mDOBFieldTextProfile,mBloodGroupFieldTextProfile
            ,mAccountTypeFieldTextProfile;

    private Button mLogout;

    private FloatingActionButton mEditProfileButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser mCureentUser;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        //User Data
        mAuth = FirebaseAuth.getInstance();
        mAuthListener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(account.this, login.class ));
                }
            }
        };
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );

        mProfilePicture = (ImageView) findViewById( R.id.profilePicture );

        mFullNameText = (TextView) findViewById( R.id.fullNameText );
        mUsernameText = (TextView) findViewById( R.id.usernameText );
        mPhoneNumberFieldTextProfile = (TextView) findViewById( R.id.phoneNumberFieldTextProfile );
        mAddressFieldTextProfile = (TextView) findViewById( R.id.addressFieldTextProfile );
        mGenderFieldTextProfile = (TextView) findViewById( R.id.genderFieldTextProfile );
        mDOBFieldTextProfile = (TextView) findViewById( R.id.dobFieldTextProfile );
        mBloodGroupFieldTextProfile = (TextView) findViewById( R.id.bloodGroupFieldTextProfile );
        mAccountTypeFieldTextProfile = (TextView) findViewById( R.id.accountTypeFieldTextProfile );

        mLogout = (Button) findViewById( R.id.logout );

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child( "Username" ).getValue(String.class);
                String profilepicture = dataSnapshot.child( "Profile Picture" ).getValue(String.class);
                String fullname = dataSnapshot.child( "Full Name" ).getValue(String.class);
                String phonenumber = dataSnapshot.child( "Phone Number" ).getValue(String.class);
                String address = dataSnapshot.child( "Address" ).getValue(String.class);
                String gender = dataSnapshot.child( "Gender" ).getValue(String.class);
                String bloodgroup = dataSnapshot.child( "Blood Group" ).getValue(String.class);
                String dob = dataSnapshot.child( "DOB" ).getValue(String.class);
                String accounttype = dataSnapshot.child( "Account Type" ).getValue(String.class);
                mUsernameText.setText( username );
                mFullNameText.setText( fullname );
                mPhoneNumberFieldTextProfile.setText( phonenumber );
                mAddressFieldTextProfile.setText( address );
                mGenderFieldTextProfile.setText( gender );
                mDOBFieldTextProfile.setText( dob );
                mBloodGroupFieldTextProfile.setText( bloodgroup );
                mAccountTypeFieldTextProfile.setText( accounttype );

                Picasso.get().load( profilepicture ).fit().centerCrop().into( mProfilePicture );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        mEditProfileButton = (FloatingActionButton) findViewById( R.id.editProfileButton );

        mEditProfileButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(  account.this, editaccount.class );
                intent.putExtra( "activity", "account");
                startActivity(intent);
            }
        } );

        mLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        } );


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Tag","11111111");
        mAuth.addAuthStateListener( mAuthListener );
    }

    private void logout() {
        mAuth.signOut();
    }
}
