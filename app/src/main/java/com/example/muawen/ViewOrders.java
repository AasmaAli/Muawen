package com.example.muawen;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewOrders extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    ArrayList<Order> list;
    MyAdapter adapter;
    FirebaseUser user;
    Order o;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_order);

        reference= FirebaseDatabase.getInstance().getReference("Orders");
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.RecyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Order>();
        activity = this;
        //for order
        final String oID = reference.getKey();
        //for user
        final String uID= mAuth.getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count1=0;
                for (DataSnapshot child : snapshot.getChildren()){

                    for (DataSnapshot Value : child.getChildren())

                        if (Value.getKey().equals("customer_id")) {
                            String id = Value.getValue().toString();
                            System.out.println("id: " + id + " uid " + uID);
                            if (id.equals(uID)) {
                                System.out.println("snapshot " + snapshot.toString());
                                System.out.println("value: " + Value.getValue());
                                o = new Order();
                                o.setName(child.getKey());
                                String date=child.child("date").getValue().toString();
                                o.setD(date);
                                String time=child.child("time").getValue().toString();
                                o.setT(time);
                                if (!list.contains(o)) {
                                    list.add(o);
                                }
                            } else {
                                Log.d("TAG", "You don't have Orders ");
                            }
                        }}
                Collections.sort(list, new Comparator<Order>(){
                    public int compare(Order date1, Order date2){
                        if (date1.getD() == null || date2.getD() == null)
                            return 0;
                        return date1.getD().compareTo(date2.getD());
                    }
                });
                Collections.sort(list, new Comparator<Order>() {
                    public int compare(Order o1, Order o2) {
                        if (o1.getT() == null || o2.getT() == null)
                            return 0;
                        return o1.getT().compareTo(o2.getT());
                    }
                });



                adapter=new MyAdapter(ViewOrders.this,list);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }}