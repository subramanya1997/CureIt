package com.example.cureit;

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
import com.google.firebase.database.Query;

public class setappointment extends AppCompatActivity {

    private EditText mSearch;
    private RecyclerView mSearchField;

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
    }

    private void searchDoctors(String searchText){
        Query firebaseSearchQuery = mDatabase.orderByChild( "Username" ).startAt( searchText ).endAt( searchText + "\uf8ff" );


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<accounts> options = new FirebaseRecyclerOptions.Builder<accounts>()
                .setQuery(mDatabase, accounts.class)
                .build();

        FirebaseRecyclerAdapter<accounts, accountViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<accounts, accountViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull accountViewHolder holder, int position, @NonNull accounts model) {

                if (model.getAccountType() == "General"){

                }
                holder.setUsername( model.getUsername() );
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

        public void setUsername(String username){
            TextView descriptionText = (TextView) mView.findViewById( R.id.profileName );
            descriptionText.setText( username );
        }
    }
}
