package com.example.muawen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Suggestionitem extends AppCompatActivity {

private DatabaseReference databaseReference ;
private FirebaseAuth firebaseAuth;
private String currentUserID ;
private seggAdapter adapter ;
private RecyclerView recyclerView ;
private items model;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestionitem);
        firebaseAuth =  FirebaseAuth.getInstance();



        fetch();

    }//end onCreate
    private void fetch() {
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID).child("items");


        FirebaseRecyclerOptions<items> options = new FirebaseRecyclerOptions.Builder<items>().setQuery(query, items.class).build();
       adapter = new seggAdapter(options);
       RecyclerView recyclerView = findViewById(R.id.recycler_View);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);



    }// end fetch





public void onStart() {
    super.onStart();
    adapter.startListening();
}

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}//end class
