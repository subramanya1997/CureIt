package com.example.cureit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterActivity;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class doctors extends AppCompatActivity {

    private Button mAccessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_doctors );

        if (ContextCompat.checkSelfPermission( doctors.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            startActivity(new Intent(  doctors.this, map.class ));
            return;
        }

        mAccessButton = (Button) findViewById( R.id.grantAccess );

        mAccessButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity( doctors.this).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        startActivity(new Intent(  doctors.this, map.class ));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder( doctors.this );
                            builder.setTitle( "Permission Denied" )
                                    .setMessage( "Permission Denied.. " )
                                    .setNegativeButton( "Cancel", null )
                                    .setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(  );
                                            intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                                            intent.setData( Uri.fromParts("package", getPackageName(),null) );
                                        }
                                    } ).show();
                        }else{
                            startActivity(new Intent(  doctors.this, dashboard.class ));
                            Toast.makeText( doctors.this, "Permission Denied...", Toast.LENGTH_LONG ).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                } ).check();
            }
        } );

    }
}
