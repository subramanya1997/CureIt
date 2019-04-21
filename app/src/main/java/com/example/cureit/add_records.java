package com.example.cureit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

public class add_records extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private ImageButton mPrescription;
    private EditText mDoctorsName, mDescription;
    private TextView mDate;
    private FloatingActionButton mDoneAddRecord;
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgress;

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_records );

        mProgress = new ProgressDialog(this);

        mPrescription = (ImageButton) findViewById( R.id.prescription );
        mDoctorsName  = (EditText) findViewById( R.id.doctorName );
        mDescription  = (EditText) findViewById( R.id.doctorName );
        mDate = (TextView) findViewById( R.id.dateOfRecord );
        mDoneAddRecord = (FloatingActionButton) findViewById( R.id.doneAddRecordButton );


        mDoneAddRecord.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord();
            }
        } );

        mDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show( getSupportFragmentManager(), "Date Picker" );
            }
        } );

        mPrescription.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent( Intent.ACTION_GET_CONTENT );
                galleryIntent.setType( "image/*" );
                startActivityForResult( galleryIntent, GALLERY_REQUEST );

            }
        } );
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar =  Calendar.getInstance();
        calendar.set( Calendar.YEAR, year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format( calendar.getTime() );
        mDate.setText( currentDate );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super .onActivityResult( requestCode,resultCode,data );

        if( requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            Picasso.get().load( imageUri ).fit().centerCrop().into( mPrescription );

        }
    }

    private void addRecord(){
        mProgress.setMessage( "Adding Record..." );
        mProgress.show();

        final String description = mDescription.getText().toString().trim();
        final String doctorsName = mDoctorsName.getText().toString().trim();
        final String date = mDate.getText().toString().trim();

        mProgress.dismiss();
    }
}
