package com.example.cureit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class patient_profile extends AppCompatActivity {

    private String patientID;

    private ImageView mProfilePicture;
    private TextView mFullNameText,mUsernameText,mBloodGroupFieldTextProfile
            ,mGenderFieldTextProfile;

    String patientUsername, patientFullname;

    private DatabaseReference mPatientDatabase;
    private Button mViewRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_patient_profile );

        patientID = getIntent().getExtras().getString("doctorsID" );

        mPatientDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( patientID );

        mProfilePicture = (ImageView) findViewById( R.id.patientProfilePicture );

        mFullNameText = (TextView) findViewById( R.id.patientFullNameText );
        mUsernameText = (TextView) findViewById( R.id.patientUsernameText );
        mBloodGroupFieldTextProfile = (TextView) findViewById( R.id.patientBloodGroupFieldTextProfile );
        mGenderFieldTextProfile = (TextView) findViewById( R.id.patientGenderFieldTextProfile );

        mPatientDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientUsername = dataSnapshot.child( "username" ).getValue(String.class);
                String profilepicture = dataSnapshot.child( "profilePicture" ).getValue(String.class);
                patientFullname = dataSnapshot.child( "fullName" ).getValue(String.class);
                String bloodGroup = dataSnapshot.child( "bloodGroup" ).getValue(String.class);
                String gender = dataSnapshot.child( "gender" ).getValue(String.class);
                mUsernameText.setText( patientUsername );
                mFullNameText.setText( patientFullname );
                mBloodGroupFieldTextProfile.setText( bloodGroup );
                mGenderFieldTextProfile.setText( gender );
                Picasso.get().load( profilepicture ).fit().centerCrop().into( mProfilePicture );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mViewRecord = (Button) findViewById( R.id.viewRecords );
        mViewRecord.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleReportIntent = new Intent( patient_profile.this, medicalrecords.class );
                singleReportIntent.putExtra( "userID", patientID );
                startActivity( singleReportIntent );
            }
        } );

    }
}
