package com.example.muawen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Customer_ststus extends AppCompatActivity {
    View vieoDivider,viewOrderplaced,viewPlacedSupporter,viewOrderRemaining,viewOrderdeliverd,view1,view2,view3,viewOrderPrepare;
    ImageView imageOrderremaining,imageOrderPlaced,imageOrderdeliverd,imagepreparingNow;
    TextView textorderPlaced,textOrderPreparing,textOrderD,textOrder,textPrepareNow,textView2;
    ListView list;
    Button btn;
    DatabaseReference reference;
    DatabaseReference Ref ,PRef;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    Query query;
    Order p;
    String s,Price;
    ArrayList<String> arrayList = new ArrayList<>();
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_ststus);
        vieoDivider = findViewById(R.id.vieoDivider);
        viewOrderplaced = findViewById(R.id.viewOrderplaced);
        viewOrderRemaining = findViewById(R.id.viewOrderRemaining);
        viewOrderdeliverd = findViewById(R.id.viewOrderdeliverd);
        viewOrderPrepare=findViewById(R.id.viewOrderPrepare);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3=findViewById(R.id.view3);
        //for images
        imageOrderremaining = findViewById(R.id.imageOrderremaining);
        imageOrderPlaced = findViewById(R.id.imageOrderPlaced);
        imageOrderdeliverd = findViewById(R.id.imageOrderdeliverd);
        imagepreparingNow=findViewById(R.id.imagePrepareNow);
        //for text
        textorderPlaced = findViewById(R.id.textorderPlaced);
        textOrderPreparing = findViewById(R.id.textOrderPreparing);
        textOrderD = findViewById(R.id.textOrderD);
        textOrder = findViewById(R.id.textOrder);
        textPrepareNow=findViewById(R.id.textPrepareNow);

        list=findViewById(R.id.list);

        textView2=findViewById(R.id.textView2);

        final String ordernum=getIntent().getExtras().get("the order number ").toString();
        TextView textOrder=findViewById(R.id.textOrder);
        textOrder.setText(ordernum);
        System.out.println("the order number: "+ordernum);


        reference= FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                         String m=child.getKey();
                        if(m.equals(ordernum)){
                                if(child.hasChild("status")){
                                    String Status=child.child("status").getValue().toString();
                            System.out.println("the status here: "+Status);
                            StatusCheck(Status); }}
                        }}

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                         }});
        mRef = FirebaseDatabase.getInstance().getReference("Orders");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String m = child.getKey();
                    if (m.equals(ordernum)) {
                        for (DataSnapshot key : child.getChildren()) {
                            if (key.getKey().equals("products")) {
                                s = key.getKey();
                                System.out.println("the keeeey here: " + s);
                                for (DataSnapshot Value : key.getChildren()) {
                                    String ss = Value.getKey();
                                    System.out.println("the product keeeey here: " + ss);
                                    final Product p=new Product();
                                    for (final DataSnapshot childeren : Value.getChildren()) {

                                        if (childeren.getKey().equals("brand")) {
                                            String b=childeren.getValue().toString();
                                            p.setBrand(childeren.getValue().toString());
                                        }
                                        if (childeren.getKey().equals("name")) {
                                            p.setName(childeren.getValue().toString());
                                        }
                                       /* if (childeren.getKey().equals("price")) {
                                            p.setPrice((Double) childeren.getValue());
                                        }*/
                                        if (childeren.getKey().equals("size")) {
                                            p.setSize((Long) childeren.getValue());
                                        }
                                    PRef=FirebaseDatabase.getInstance().getReference("Product");
                                        PRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                    String key=snapshot1.getKey();
                                                    for (DataSnapshot child:snapshot.getChildren())
                                                    {
                                                        String key1=child.getKey();
                                                        if(childeren.getKey().equals(key))
                                                        if(key1.equals("Price")){
                                                            System.out.print("THe price of the item "+child.getValue());
                                                            p.setPrice((Double) child.getValue());
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }

                                        arrayList.add( " اسم المنتج: "+p.getName()+" الحجم: "+p.getSize()+" نوع المنتج: "+p.getBrand()+" السعر: "+p.getPrice());
                                        System.out.println("the arrrrray: " + arrayList);
                                        ArrayAdapter arrayAdapter = new ArrayAdapter(Customer_ststus.this, android.R.layout.simple_list_item_1, arrayList);
                                        list.setAdapter(arrayAdapter);

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

Ref=FirebaseDatabase.getInstance().getReference("Orders");
Ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot child : snapshot.getChildren()) {
            String m = child.getKey();
            if (m.equals(ordernum)) {
                for (DataSnapshot key : child.getChildren()) {
                    if (key.getKey().equals("totalPrice")) {
                        Price=key.getValue().toString();
                        textView2.setText(Price);
    }}}}}

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

    }


@SuppressLint("UseCompatLoadingForDrawables")
public void StatusCheck(String S) {
            float v = (float) 0.25;
            float a = (float) 1;
            switch(S){
                case"ارسال":
                    viewOrderplaced.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderRemaining.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    imageOrderPlaced.setAlpha(a);
                    textorderPlaced.setAlpha(a);
                    textOrderPreparing.setAlpha(v);
                    viewOrderdeliverd.setAlpha(v);
                    imageOrderremaining.setAlpha(v);
                    imageOrderdeliverd.setAlpha(v);
                    imagepreparingNow.setAlpha(v);
                    textPrepareNow.setAlpha(v);
                    viewOrderdeliverd.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    viewOrderPrepare.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    textOrderD.setAlpha(v);
                    break;
                case "تحضير":
                    viewOrderplaced.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderRemaining.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    imageOrderremaining.setAlpha(a);
                    imageOrderdeliverd.setAlpha(v);
                    textOrderPreparing.setAlpha(a);
                    textorderPlaced.setAlpha(a);
                    textOrderD.setAlpha(v);
                    imagepreparingNow.setAlpha(v);
                    textPrepareNow.setAlpha(v);
                    viewOrderdeliverd.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    viewOrderPrepare.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    textOrderPreparing.setAlpha(a);
                    view1.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    break;
                case "توصيل":
                    viewOrderplaced.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderRemaining.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderPrepare.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    imageOrderremaining.setAlpha(a);
                    imageOrderdeliverd.setAlpha(v);
                    textOrderPreparing.setAlpha(a);
                    textorderPlaced.setAlpha(a);
                    textOrderD.setAlpha(v);
                    imagepreparingNow.setAlpha(a);
                    textPrepareNow.setAlpha(a);
                    viewOrderdeliverd.setBackground(getResources().getDrawable(R.drawable.shape_status_remaining));
                    textOrderPreparing.setAlpha(a);
                    view1.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    view2.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    break;
                case "تم التوصيل":
                    viewOrderplaced.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderRemaining.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    viewOrderPrepare.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    imageOrderremaining.setAlpha(a);
                    textOrderPreparing.setAlpha(a);
                    imagepreparingNow.setAlpha(a);
                    textPrepareNow.setAlpha(a);
                    viewOrderdeliverd.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    view1.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    view2.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    view3.setBackground(getResources().getDrawable(R.drawable.shape_status_complete));
                    textOrderD.setAlpha(a);

            }}





    }

