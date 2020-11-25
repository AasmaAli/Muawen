package com.example.muawen;

import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Product {



        String Brand ;
        String Name ;
        String Describe;
        long size ;
        double Price ;



    public Product(String Brand, String Name, String Describe, long size, double Price) {

            this.Brand = Brand;
            this.Name = Name;
            this.size = size;
            this.Price = Price;
            this.Describe = Describe;
        }

    public Product() {
    }





        public String getBrand() {
            return Brand;
        }

        public void setBrand(String brand) {
            Brand = brand;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String describe) {
        Describe = describe;
    }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double price) {
            Price = price;
        }

        public void InfoProduct(String Product_ID){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference Product = rootRef.child("Product");

            Product.child(Product_ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.hasChild("Brand")) {
                            Brand = dataSnapshot.child("Brand").getValue().toString();

                        }
                        if (dataSnapshot.hasChild("Name")) {
                            Name = dataSnapshot.child("Name").getValue().toString();

                        }
                        if (dataSnapshot.hasChild("Price")) {
                            setPrice( Double.parseDouble(dataSnapshot.child("Price").getValue().toString()));


                        }
                        if (dataSnapshot.hasChild("Size")) {
                            size = Long.parseLong(dataSnapshot.child("Size").getValue().toString());

                        }

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            });
        }
    

}
