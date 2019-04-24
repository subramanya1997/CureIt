package com.example.cureit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class setappointment extends AppCompatActivity {

    private RecyclerView mSearchField;
    private EditText mSearchText;

    private DatabaseReference mDatabase;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseUser mCureentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setappointment );

        mAuth = FirebaseAuth.getInstance();
        mCureentUser = mAuth.getCurrentUser();
        user_id = mCureentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" );

        mSearchField = (RecyclerView) findViewById( R.id.searchField );
        mSearchField.setHasFixedSize(true);
        mSearchField.setLayoutManager( new LinearLayoutManager( this ) );

        mSearchText = (EditText) findViewById( R.id.search );
        mSearchText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchDoctors( s.toString() );
            }
        } );

    }

    private void searchDoctors(String searchText){

        Query firebaseSearchQuery = mDatabase.orderByChild( "accountType_username" ).startAt( "Doctor_@" + searchText ).endAt( "Doctor_@" + searchText + "\uf8ff");

        FirebaseRecyclerOptions<accounts> options = new FirebaseRecyclerOptions.Builder<accounts>()
                .setQuery(firebaseSearchQuery, accounts.class)
                .build();

        FirebaseRecyclerAdapter<accounts, accountViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<accounts, accountViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull accountViewHolder holder, int position, @NonNull accounts model) {

                        final String doctorsProfile = getRef( position ).getKey();

                        holder.setFullName( model.getFullName() );

                        holder.mView.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent singleReportIntent = new Intent( setappointment.this, doctors_profile.class );
                                singleReportIntent.putExtra( "doctorsID", doctorsProfile );
                                startActivity( singleReportIntent );
                            }
                        } );
                    }

                    @NonNull
                    @Override
                    public accountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.profile_layout, viewGroup, false);


                        return new accountViewHolder( view );
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mSearchField.setAdapter( firebaseRecyclerAdapter );

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mAppointment = FirebaseDatabase.getInstance().getReference().child( "Appointment" ).child( user_id );
        FirebaseRecyclerOptions<accounts> options = new FirebaseRecyclerOptions.Builder<accounts>()
                .setQuery(mAppointment, accounts.class)
                .build();

        FirebaseRecyclerAdapter<accounts, accountViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<accounts, accountViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull accountViewHolder holder, int position, @NonNull accounts model) {

                        final String doctorsProfile = getRef( position ).getKey();

                        holder.setFullName( model.getFullName() );

                        holder.mView.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent singleReportIntent = new Intent( setappointment.this, doctors_profile.class );
                                singleReportIntent.putExtra( "doctorsID", doctorsProfile );
                                startActivity( singleReportIntent );
                            }
                        } );
                    }

                    @NonNull
                    @Override
                    public accountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.profile_layout, viewGroup, false);


                        return new accountViewHolder( view );
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
