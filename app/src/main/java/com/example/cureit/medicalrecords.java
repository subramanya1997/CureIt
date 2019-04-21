package com.example.cureit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class medicalrecords extends AppCompatActivity {


    private RecyclerView mRecordList;
    private FloatingActionButton mAddRecords;

    private DatabaseReference mDatabase;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_medicalrecords );

        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Records" ).child( user_id );

        mRecordList = (RecyclerView) findViewById( R.id.recordList );
        mRecordList.setHasFixedSize(true);
        mRecordList.setLayoutManager( new LinearLayoutManager( this ) );


        mAddRecords = (FloatingActionButton) findViewById( R.id.addRecord );


        mAddRecords.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(medicalrecords.this, add_records.class) );
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v("abc", "1");
        Log.v("abc", "1");



        FirebaseRecyclerOptions<records> options = new FirebaseRecyclerOptions.Builder<records>()
                .setQuery(mDatabase, records.class)
                .build();

        FirebaseRecyclerAdapter<records, recordsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<records, recordsViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull recordsViewHolder holder, int position, @NonNull records model) {

                holder.setDescription( model.getDescription());
                holder.setDate( model.getDate() );
                holder.setDoctorsName( model.getDoctorsName() );

            }

            @NonNull
            @Override
            public recordsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.record_layout, viewGroup, false);


                return new recordsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        mRecordList.setAdapter( firebaseRecyclerAdapter );

    }

    public static class recordsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public recordsViewHolder(@NonNull View itemView) {
            super( itemView );

            mView = itemView;
        }

        public void setDescription(String description){
            TextView descriptionText = (TextView) mView.findViewById( R.id.desc );
            descriptionText.setText( description );
        }

        public void setDoctorsName(String doctorsName){
            TextView doctorsNameText = (TextView) mView.findViewById( R.id.docName );
            doctorsNameText.setText( doctorsName );
        }

        public void setDate(String date){
            TextView dateText = (TextView) mView.findViewById( R.id.dateofRecordAdded );
            dateText.setText( date );
        }


    }
}
