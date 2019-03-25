package com.example.cureit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private EditText mEmailFieldReg,mPasswordFieldReg, mUsernameFieldReg;
    private Button mSignupButton;
    private TextView mLogintext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" );

        // Progress bar
        mProgress = new ProgressDialog( this );

        //Get Data from fields
        mUsernameFieldReg = (EditText) findViewById( R.id.usernameFieldReg );
        mEmailFieldReg = (EditText) findViewById( R.id.emailFieldReg );
        mPasswordFieldReg = (EditText) findViewById( R.id.passwordFieldReg );
        mSignupButton =  (Button) findViewById( R.id.signupButton );
        mLogintext = (TextView) findViewById( R.id.loginText );

        //On sign up Clicked
        mSignupButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignup();
            }
        } );

        //On login Clicked
        mLogintext.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, login.class ));
            }
        } );

    }

    private void startSignup() {
        mProgress.setMessage( "Signing Up.." );
        mProgress.show();

        final String username = "@"+mUsernameFieldReg.getText().toString().trim();
        String email = mEmailFieldReg.getText().toString().trim();
        String password = mPasswordFieldReg.getText().toString().trim();

        if (TextUtils.isEmpty( username ) || TextUtils.isEmpty( email ) || TextUtils.isEmpty( password ) ){

        }else{


            mAuth.createUserWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child( user_id );
                        current_user_db.child( "Username" ).setValue( username );
                        mProgress.dismiss();
                        Intent intent = new Intent(  signup.this, editaccount.class);
                        intent.putExtra( "activity", "signup");
                        startActivity(intent);
                    }
                }
            } );
        }


    }


}