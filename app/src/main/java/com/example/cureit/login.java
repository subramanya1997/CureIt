package com.example.cureit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class login extends AppCompatActivity {

    private EditText mEmailField,mPasswordField;
    private Button mLoginButton;
    private TextView mSignupText;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login );

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null ){
                    startActivity(new Intent(login.this, dashboard.class ));
                }
            }
        };

        //Get Data from fields
        mEmailField = (EditText) findViewById( R.id.emailField );
        mPasswordField = (EditText) findViewById( R.id.passwordField );
        mLoginButton =  (Button) findViewById( R.id.loginButton );
        mSignupText = (TextView) findViewById( R.id.signupText );
        mProgressDialog = new ProgressDialog( this );


        //On Signup Clicked
        mSignupText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, signup.class));
            }
        } );

        //On Login Clicked
        mLoginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignin();
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener( mAuthListener );
    }

    private void startSignin() {
        mProgressDialog.setMessage( "Login.." );
        mProgressDialog.show();

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty( email ) || TextUtils.isEmpty( password )){
            Toast.makeText( login.this, "Fields are empty", Toast.LENGTH_LONG ).show();
            mProgressDialog.dismiss();
        }else {
            mAuth.signInWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText( login.this, "No Account found. Sign up please..", Toast.LENGTH_LONG ).show();
                        startActivity(new Intent(login.this, signup.class));
                        mProgressDialog.dismiss();
                    }
                }
            } );
        }
    }
}
