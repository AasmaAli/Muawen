package com.example.muawen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class Suggestionitem extends AppCompatActivity {
    private RecyclerView SuggRecyclerView;
    FirebaseRecyclerAdapter<items, SuggView> firebaseUsersAdapter = null;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, User;
    private DatabaseReference ItemRef;
    String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestionitem);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
        User =UsersRef.child(currentUserID);
        //ItemRef = User.child("items");

        SuggRecyclerView = (RecyclerView) findViewById(R.id.suggestRecyclerView);
        SuggRecyclerView.setHasFixedSize(false);
        SuggRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewitemsuggest();

    }

    private void viewitemsuggest() {

            ItemRef =UsersRef.child(currentUserID).child("items");
        toastMessage("in 1");


        FirebaseRecyclerOptions<items> options = new FirebaseRecyclerOptions.Builder<items>()
                    .setQuery(ItemRef.orderByChild("Suggestion_flag").equalTo("1"), items.class)
                    .build();
            firebaseUsersAdapter = new FirebaseRecyclerAdapter<items, SuggView>(
                    options) {
                @Override
                protected void onBindViewHolder(final SuggView holder, final int position, final items model) {
                   // if(!model.getSuggestion_flag().equals("0") && !model.getSuggestion_flag().equals("3")  ) {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                        if(model.getSuggested_item().equals("-1")){
                            if(model.getSuggestion_flag().equals("1")){
                                getRef(position).child("Suggested_item").setValue(replacing(model.getQuantity()));
                            }else{
                                getRef(position).child("Suggested_item").setValue(upgrade());
                            }
                        }

                        if(model.getSuggestion_flag().equals("1")){
                            toastMessage("in 1");

                            holder.suggested("إستبدال");

                        }
                            else if(model.getSuggestion_flag().equals("2")){
                            toastMessage("in 2");

                            holder.suggested("ترقية");


                        }else{
                            toastMessage(model.getSuggestion_flag());

                        }

                        //Current_wieght:It takes the value from the sensor
                        DatabaseReference Product = rootRef.child("Product");

                        //item info
                        Product.child(model.getProduct_ID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("Name")) {
                                        String Name = dataSnapshot.child("Name").getValue().toString();
                                        Name = " " + Name + " ";
                                        holder.setItemName(Name);

                                    }
                                    if (dataSnapshot.hasChild("Brand")) {
                                        String Brand = dataSnapshot.child("Brand").getValue().toString();
                                        Brand = " " + Brand + " ";
                                        holder.setItemBrand(Brand);

                                    }
                                    if (dataSnapshot.hasChild("Size")) {
                                        String Size = dataSnapshot.child("Size").getValue().toString();
                                        Size = " الذي حجمه " + Size + "مل ";
                                        holder.setItemSize(Size);

                                    }
                                    if (dataSnapshot.hasChild("Price")) {
                                        String Price = dataSnapshot.child("Price").getValue().toString();
                                        Price = " وسعره " + Price + "ريال ";
                                        holder.setItemPrice(Price);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        ////item info________________________________________
                    if(model.getSuggested_item().equals("Negativeone")){

                        holder.setSugBrand("قلل الكميه إلى " + (model.getQuantity()-1) +" بدلاً من "+model.getQuantity());

                    }else {

                        Product.child(model.getSuggested_item()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("Name")) {
                                        String Name = dataSnapshot.child("Name").getValue().toString();
                                        Name = " إلى " + Name + " ";
                                        holder.setSugName(Name);

                                    }
                                    if (dataSnapshot.hasChild("Brand")) {
                                        String Brand = dataSnapshot.child("Brand").getValue().toString();
                                        Brand = " " + Brand + " ";
                                        holder.setSugBrand(Brand);

                                    }
                                    if (dataSnapshot.hasChild("Size")) {
                                        String Size = dataSnapshot.child("Size").getValue().toString();
                                        Size = " الذي حجمه " + Size + "مل ";
                                        holder.setSugSize(Size);

                                    }
                                    if (dataSnapshot.hasChild("Price")) {
                                        String Price = dataSnapshot.child("Price").getValue().toString();
                                        Price = " وسعره " + Price + "ريال ";
                                        holder.setSugPrice(Price);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }//not Negativeone
                  //  }//if flag

                }//onBindViewHolder


                @Override
                public Suggestionitem.SuggView onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestionitemview, parent, false);
                    return new SuggView(view);
                }



            };

        SuggRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SuggRecyclerView.setAdapter(firebaseUsersAdapter);
        firebaseUsersAdapter.startListening();




    }

    public static class SuggView extends RecyclerView.ViewHolder{
        View mView;
        public SuggView(View SuggView){
            super(SuggView);
            mView =SuggView;

        }
        @SuppressLint("ResourceAsColor")
        public void suggested(String sugg){
            TextView item_sugg = (TextView)mView.findViewById(R.id.suggested);
            item_sugg.setText(sugg);
                if(sugg.equals("إستبدال")){
                    item_sugg.setTextColor(Color.parseColor("#CBED1808"));
                }else{
                    item_sugg.setTextColor(Color.parseColor("#FF669900"));

                }


        }

        public void setItemName(String name){
            TextView item_name = (TextView)mView.findViewById(R.id.Item_name_Sugg);
            item_name.setText(name);
        }
        public void setItemBrand(String brand) {
            TextView item_brand = (TextView)mView.findViewById(R.id.Item_brand);
            item_brand.setText(brand);
        }

        public void setItemSize(String size) {
            TextView item_size= (TextView)mView.findViewById(R.id.Item_Size);
            item_size.setText(size);
        }

        public void setItemPrice(String Price) {
            TextView item_Price= (TextView)mView.findViewById(R.id.Item_Price);
            item_Price.setText(Price);
        }

        public void setSugName(String name) {
            TextView item_name = (TextView)mView.findViewById(R.id.suggest_name);
            item_name.setText(name);
        }
        public void setSugBrand(String brand) {
            TextView item_brand = (TextView)mView.findViewById(R.id.suggest_brand);
            item_brand.setText(brand);
        }

        public void setSugSize(String size) {
            TextView item_size= (TextView)mView.findViewById(R.id.suggest_Size);
            item_size.setText(size);
        }

        public void setSugPrice(String Price) {
            TextView item_Price= (TextView)mView.findViewById(R.id.suggest_Price);
            item_Price.setText(Price);
        }

    }// class itemsView


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


    private String replacing(long quantity) {
        if(quantity!= 1){
            return "Negativeone";
        }else{

        }
        return null;
    }

    private String upgrade() {
        return null;
    }

}