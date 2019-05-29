package com.example.cureit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class patients extends AppCompatActivity {

    private RecyclerView mSearchField;

    private DatabaseReference mDatabase;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_patients );

        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" );

        mSearchField = (RecyclerView) findViewById( R.id.patientslist );
        mSearchField.setHasFixedSize(true);
        mSearchField.setLayoutManager( new LinearLayoutManager( this ) );
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mAppointment = FirebaseDatabase.getInstance().getReference().child( "Appointment" ).child( user_id );
        FirebaseRecyclerOptions<accounts> options = new FirebaseRecyclerOptions.Builder<accounts>()
                .setQuery(mAppointment, accounts.class)
                .build();

        FirebaseRecyclerAdapter<accounts, setappointment.accountViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<accounts, setappointment.accountViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull setappointment.accountViewHolder holder, int position, @NonNull accounts model) {

                        final String patientProfile = getRef( position ).getKey();

                        holder.setFullName( model.getFullName() );

                        holder.mView.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent singleReportIntent = new Intent( patients.this, patient_profile.class );
                                singleReportIntent.putExtra( "doctorsID", patientProfile );
                                startActivity( singleReportIntent );
                            }
                        } );
                    }

                    @NonNull
                    @Override
                    public setappointment.accountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.profile_layout, viewGroup, false);


                        return new setappointment.accountViewHolder( view );
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mSearchField.setAdapter( firebaseRecyclerAdapter );
    }

    public static class accountViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public accountViewHolder(@NonNull View itemView) {
            super( itemView );

            mView = itemView;
        }

        public void setFullName(String fullName){
            TextView descriptionText = (TextView) mView.findViewById( R.id.profileName );
            descriptionText.setText( fullName );
        }
    }
}
