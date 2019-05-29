package com.example.cureit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cureit.common.GraphicOverlay;
import com.example.cureit.common.VisionImageProcessor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

public class add_records extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private ImageView mPrescription;
    private EditText mDoctorsName, mDescription;
    private TextView mDate;
    private FloatingActionButton mDoneAddRecord;
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgress;


    private GraphicOverlay graphicOverlay;
    private static final String KEY_IMAGE_URI = "com.googletest.firebase.ml.demo.KEY_IMAGE_URI";
    private static final String KEY_IMAGE_MAX_WIDTH =
            "com.googletest.firebase.ml.demo.KEY_IMAGE_MAX_WIDTH";
    private static final String KEY_IMAGE_MAX_HEIGHT =
            "com.googletest.firebase.ml.demo.KEY_IMAGE_MAX_HEIGHT";
    private static final String KEY_SELECTED_SIZE =
            "com.googletest.firebase.ml.demo.KEY_SELECTED_SIZE";
    private static final String SIZE_PREVIEW = "w:max";
    private String selectedSize = SIZE_PREVIEW;
    private VisionImageProcessor imageProcessor;
    private Bitmap bitmapForDetection;
    boolean isLandScape;
    // Max width (portrait mode)
    private Integer imageMaxWidth;
    // Max height (portrait mode)
    private Integer imageMaxHeight;

    Uri imageUri = null;

    private DatabaseReference mDatabase, mDatabaseCur;
    private String user_id;
    private FirebaseAuth mAuth;
    private String mCureentUserID;
    private StorageReference mStorage;
    String record_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_records );

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCureentUserID = mAuth.getCurrentUser().getUid();
        user_id = getIntent().getExtras().getString("userID" );
        mDatabaseCur = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mCureentUserID );
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Records" ).child( user_id ).push();
        mStorage = FirebaseStorage.getInstance().getReference();
        record_uid = mDatabase.getKey();

        mPrescription = (ImageView) findViewById( R.id.prescription );
        mDoctorsName  = (EditText) findViewById( R.id.doctorName );
        mDescription  = (EditText) findViewById( R.id.description );
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

        mDatabaseCur.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!user_id.contentEquals(mCureentUserID)){
                    String fullname = dataSnapshot.child( "fullName" ).getValue(String.class);
                    mDoctorsName.setText( fullname );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        Calendar calendar =  Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format( calendar.getTime() );
        mDate.setText( currentDate );

        graphicOverlay = (GraphicOverlay) findViewById(R.id.previewOverlay);

        imageProcessor = new CloudDocumentTextRecognitionProcessor();
        ((CloudDocumentTextRecognitionProcessor) imageProcessor).setTemp( record_uid, user_id );
        isLandScape =
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

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
            mPrescription.setImageURI( imageUri );
        }
    }

    private void addRecord(){
        mProgress.setMessage( "Adding Record..." );
        mProgress.show();

        final String description = mDescription.getText().toString().trim();
        final String doctorsName = mDoctorsName.getText().toString().trim();
        final String date = mDate.getText().toString().trim();
        tryReloadAndDetectInImage();

        if(TextUtils.isEmpty( description ) || TextUtils.isEmpty( doctorsName ) || TextUtils.isEmpty( date )
        || imageUri == null){

        }else{
            final StorageReference filepath = mStorage.child( user_id ).child( record_uid ).child( "prescription" );
            UploadTask uploadTask = filepath.putFile( imageUri );
            Task<Uri> urlTask = uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        String prescription_link = downloadUri.toString();

                        DatabaseReference record = mDatabase;
                        record.child( "prescription" ).setValue( prescription_link );
                        record.child( "description" ).setValue( description );
                        record.child( "doctorsName" ).setValue( doctorsName );
                        record.child( "date" ).setValue( date );

                        Intent singleReportIntent = new Intent( add_records.this, medicalrecords.class );
                        singleReportIntent.putExtra( "userID", user_id );
                        startActivity( singleReportIntent );

                        mProgress.dismiss();

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }


        mProgress.dismiss();
    }

    public String getRecordUID(){
        return this.mDatabase.getKey();
    }

    private void tryReloadAndDetectInImage() {
        try {
            if (imageUri == null) {
                return;
            }

            // Clear the overlay first
            graphicOverlay.clear();

            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) imageBitmap.getWidth() / (float) targetWidth,
                            (float) imageBitmap.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            imageBitmap,
                            (int) (imageBitmap.getWidth() / scaleFactor),
                            (int) (imageBitmap.getHeight() / scaleFactor),
                            true);

            mPrescription.setImageBitmap(resizedBitmap);
            bitmapForDetection = resizedBitmap;

            imageProcessor.process(bitmapForDetection, graphicOverlay);
        } catch (IOException e) {
            Log.e("aaa", "Error retrieving saved image");
        }
    }

    private Integer getImageMaxWidth() {
        if (imageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            if (isLandScape) {
                imageMaxWidth =
                        ((View) mPrescription.getParent()).getHeight();
            } else {
                imageMaxWidth = ((View) mPrescription.getParent()).getWidth();
            }
        }

        return imageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (imageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            if (isLandScape) {
                imageMaxHeight = ((View) mPrescription.getParent()).getWidth();
            } else {
                imageMaxHeight =
                        ((View) mPrescription.getParent()).getHeight();
            }
        }

        return imageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;

        switch (selectedSize) {
            case SIZE_PREVIEW:
                int maxWidthForPortraitMode = getImageMaxWidth();
                int maxHeightForPortraitMode = getImageMaxHeight();
                targetWidth = isLandScape ? maxHeightForPortraitMode : maxWidthForPortraitMode;
                targetHeight = isLandScape ? maxWidthForPortraitMode : maxHeightForPortraitMode;
                break;
            default:
                throw new IllegalStateException("Unknown size");
        }

        return new Pair<>(targetWidth, targetHeight);
    }

}
