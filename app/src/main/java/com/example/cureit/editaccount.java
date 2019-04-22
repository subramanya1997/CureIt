package com.example.cureit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

public class editaccount extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageButton mEditProfilePicture;
    private static final int GALLERY_REQUEST = 1;
    private EditText mEditFullNameText,mEditPhoneNumberFieldTextProfile,
            mEditAddressFieldTextProfile,mEditGenderFieldTextProfile,mEditBloodGroupFieldTextProfile
            ,mEditAccountTypeFieldTextProfile;
    private TextView mEditDOBFieldTextProfile,mEditUsernameText;

    private String user_id;
    private ProgressDialog mProgress;

    private FloatingActionButton mDoneEditProfileButton;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;
    private Intent intent;


    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editaccount );

        mProgress = new ProgressDialog(this);

        //firebase references
        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( user_id );

        //data
        mEditProfilePicture = (ImageButton) findViewById( R.id.editProfilePictureButton );

        mEditFullNameText = (EditText) findViewById( R.id.editFullNameText );
        mEditUsernameText = (TextView) findViewById( R.id.editUsernameText );
        mEditPhoneNumberFieldTextProfile = (EditText) findViewById( R.id.editPhoneNumberFieldTextProfile );
        mEditAddressFieldTextProfile = (EditText) findViewById( R.id.editAddressFieldTextProfile );
        mEditGenderFieldTextProfile = (EditText) findViewById( R.id.editGenderFieldTextProfile );
        mEditDOBFieldTextProfile = (TextView) findViewById( R.id.editDobFieldTextProfile );
        mEditBloodGroupFieldTextProfile = (EditText) findViewById( R.id.editBloodGroupFieldTextProfile );
        mEditAccountTypeFieldTextProfile = (EditText) findViewById( R.id.editAccountTypeFieldTextProfile );


        mDoneEditProfileButton = (FloatingActionButton) findViewById( R.id.doneEditProfileButton );

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child( "username" ).getValue(String.class);
                mEditUsernameText.setText( username );
                if(dataSnapshot.hasChild( "fullName" )){
                    String profilepicture = dataSnapshot.child( "profilePicture" ).getValue(String.class);
                    String fullname = dataSnapshot.child( "fullName" ).getValue(String.class);
                    String phonenumber = dataSnapshot.child( "phoneNumber" ).getValue(String.class);
                    String address = dataSnapshot.child( "address" ).getValue(String.class);
                    String gender = dataSnapshot.child( "gender" ).getValue(String.class);
                    String bloodgroup = dataSnapshot.child( "bloodGroup" ).getValue(String.class);
                    String dob = dataSnapshot.child( "DOB" ).getValue(String.class);
                    String accounttype = dataSnapshot.child( "accountType" ).getValue(String.class);
                    mEditFullNameText.setText( fullname );
                    mEditPhoneNumberFieldTextProfile.setText( phonenumber );
                    mEditAddressFieldTextProfile.setText( address );
                    mEditGenderFieldTextProfile.setText( gender );
                    mEditBloodGroupFieldTextProfile.setText( bloodgroup );
                    mEditDOBFieldTextProfile.setText( dob );
                    mEditAccountTypeFieldTextProfile.setText( accounttype );

                    Picasso.get().load( profilepicture ).fit().centerCrop().into( mEditProfilePicture );
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        // Profile picture picker
        mEditProfilePicture.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent( Intent.ACTION_GET_CONTENT );
                galleryIntent.setType( "image/*" );
                startActivityForResult( galleryIntent, GALLERY_REQUEST );

            }
        } );

        // Calender Date Picker
        mEditDOBFieldTextProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show( getSupportFragmentManager(), "Date Picker" );
            }
        } );

        // Submit Button
        mDoneEditProfileButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfile();
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = this.getIntent();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar =  Calendar.getInstance();
        calendar.set( Calendar.YEAR, year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format( calendar.getTime() );
        mEditDOBFieldTextProfile.setText( currentDate );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super .onActivityResult( requestCode,resultCode,data );

        if( requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            Picasso.get().load( imageUri ).fit().centerCrop().into( mEditProfilePicture );

        }
    }

    private void addProfile() {
        mProgress.setMessage( "Updateing..." );
        mProgress.show();

        final String fullname = mEditFullNameText.getText().toString().trim();
        final String phone_no = mEditPhoneNumberFieldTextProfile.getText().toString().trim();
        final String address = mEditAddressFieldTextProfile.getText().toString().trim();
        final String gender = mEditGenderFieldTextProfile.getText().toString().trim();
        final String blood_group = mEditBloodGroupFieldTextProfile.getText().toString().trim();
        final String dob = mEditDOBFieldTextProfile.getText().toString().trim();
        final String account_type = mEditAccountTypeFieldTextProfile.getText().toString().trim();

        if(TextUtils.isEmpty( fullname ) || TextUtils.isEmpty( phone_no ) || TextUtils.isEmpty( address )
                || TextUtils.isEmpty( gender ) || TextUtils.isEmpty( blood_group ) || TextUtils.isEmpty( dob )
                || TextUtils.isEmpty( account_type ) ){

            mProgress.setMessage( "Empty..." );
            mProgress.dismiss();


        } else{
            user_id = mAuth.getCurrentUser().getUid();
            final StorageReference filepath = mStorage.child( user_id ).child("profilePicture");

            UploadTask uploadTask = filepath.putFile( imageUri );
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String profile_picture_link = downloadUri.toString();

                        DatabaseReference account = mDatabase;
                        account.child( "profilePicture" ).setValue( profile_picture_link );
                        account.child( "fullName" ).setValue( fullname );
                        account.child( "phoneNumber" ).setValue( phone_no );
                        account.child( "address" ).setValue( address );
                        account.child( "gender" ).setValue( gender );
                        account.child( "bloodGroup" ).setValue( blood_group );
                        account.child( "DOB" ).setValue( dob );
                        account.child( "accountType" ).setValue( account_type );
                        if(intent !=null)
                        {
                            String strdata = intent.getExtras().getString("activity");
                            if (strdata.equals("account")){
                                startActivity(new Intent( editaccount.this, account.class ) );
                            }
                            if (strdata.equals("signup")){
                                startActivity(new Intent( editaccount.this, dashboard.class ) );
                            }
                        }

                        mProgress.dismiss();

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }


}
