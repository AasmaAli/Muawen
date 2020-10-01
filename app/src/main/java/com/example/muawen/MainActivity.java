package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    private RecyclerView itemRecyclerView;
    private NavigationView navigationView;
    private TextView NavProfileUserName;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mToolbar;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference UsersRef;
    private DatabaseReference ItemRef;



    String currentUserID;
    FirebaseRecyclerAdapter<items, itemsView> firebaseUsersAdapter = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //RecyclerView
        itemRecyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setHasFixedSize(false);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("الصفحة الرئيسة");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigtion_header);
        NavProfileUserName = (TextView) navView.findViewById(R.id.Username);


        FirebaseUser currentUser = mAuth.getCurrentUser();



     if(currentUser != null) {

       currentUserID = mAuth.getCurrentUser().getUid();
       UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
       UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   if (dataSnapshot.hasChild("Username")) {
                       String username = dataSnapshot.child("Username").getValue().toString();
                       NavProfileUserName.setText(username);
                   }
                   else
                   {
                       Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               UserMenuSelector(item);
               return false;
           }
       });


     }//not currentUser null
     else {
       Intent loginIntent = new Intent(MainActivity.this, login.class);
       loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(loginIntent);
       finish();
     }


    }//on Create

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }//for Bar onOptionsItemSelected



    private void UserMenuSelector(MenuItem item) {
        Intent Goto;
        switch (item.getItemId())
        {
            case R.id.nav_Shoppinglist:
                Goto = new Intent(MainActivity.this,ViewShoppingList.class);
                startActivity(Goto);
                break;

            case R.id.nav_Orders:
                Goto = new Intent(MainActivity.this,ViewOrders.class);
                startActivity(Goto);
                break;

            case R.id.nav_Suggesteditem:
                Goto = new Intent(MainActivity.this,Suggestionitem.class);
                startActivity(Goto);
                break;

            case R.id.nav_AddItem:
                Goto = new Intent(MainActivity.this,AddItem.class);
                startActivity(Goto);
                break;

            case R.id.nav_DeleteItem:
                Toast.makeText(this, "Delete Item", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Editprofile:
                Goto = new Intent(MainActivity.this,EditProfile.class);
                startActivity(Goto);
                break;

            case R.id.nav_loguot:
                mAuth.signOut();
                Intent loginIntent = new Intent(MainActivity.this,login.class);

                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
                break;
        }

    }//UserMenuSelector

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null  ) {
            Intent loginIntent = new Intent(MainActivity.this, login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();

        }//if end

        else {


            //Check User Existence
            final String current_user_id = mAuth.getCurrentUser().getUid();

            UsersRef.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(current_user_id)) {
                        // Send User To Setup Activity

                        Intent setupIntent = new Intent(MainActivity.this, Setup.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(setupIntent);
                        //finish();
                    }//end if

                }

                @Override
                public void onCancelled( DatabaseError databaseError) {
                    Intent setupIntent = new Intent(MainActivity.this, AddItem.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(setupIntent);
                }



            });
           viewitem();

        }//else end


    }//On Start


    private void viewitem() {


        ItemRef =UsersRef.child(currentUserID).child("items");

        FirebaseRecyclerOptions<items> options = new FirebaseRecyclerOptions.Builder<items>()
                .setQuery(ItemRef, items.class)
                .build();

        firebaseUsersAdapter = new FirebaseRecyclerAdapter<items, itemsView>(
                options) {
            @Override
            protected void onBindViewHolder(final itemsView holder, int position, items model) {

               //display the Name of Product
                String Product_ID = model.getProduct_ID();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference Product = rootRef.child("Product");

                Product.child(Product_ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("Name")) {
                                String Name= dataSnapshot.child("Name").getValue().toString();
                                holder.setTite(Name);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //display the remaining day
                long days =0;
                try {
                    days = RemainingDaymathed(model.getExp_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String RemainingDay;
                if(days>= 0) {
                    RemainingDay = ("المتبقي: " + days + " أيام ");
                }
                else {
                    RemainingDay = ("لقد أنتهى تاريخ المنتج");
                    //AddtoShoppingList
                }
                holder.setRemainingDay(RemainingDay);


                //display the remaining wieght
                String Current_wieght = "الوزن المتبقي : "+ model.getCurrent_wieght()+ " غرام";

                holder.setcurrentweight(Current_wieght);

                //display the remaining quantity
                String Current_quantity = "الكمية المتبقية: "+ model.getCurrent_quantity();
                holder.setquantity(Current_quantity);

                //display icon
                holder.setimageitem(model.getCurrent_wieght() , model.getOriginal_weight());
            }
            @Override
            public itemsView onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitemsmain, parent, false);
                return new itemsView(view);
            }
        };

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerView.setAdapter(firebaseUsersAdapter);
        firebaseUsersAdapter.startListening();
    }// viwe item

    public static class itemsView extends RecyclerView.ViewHolder{
        View mView;
         public itemsView(View itemView){
           super(itemView);
           mView =itemView;

         }

        public void setTite(String title){
           TextView item_name = (TextView)mView.findViewById(R.id.Item_name);
           item_name.setText(title);
         }
        public void setRemainingDay(String RemainingDay){
            TextView item_RemainingDay = (TextView)mView.findViewById(R.id.Item_RemainingDay);
            item_RemainingDay.setText(RemainingDay);
        }
        public void setcurrentweight(String currentweight){
            TextView item_currentweight = (TextView) mView.findViewById(R.id.Item_currentweight);
            item_currentweight.setText(String.valueOf(currentweight));
        }
        public void setquantity(String getCurrent_quantity){
            TextView item_getCurrent_quantity = (TextView) mView.findViewById(R.id.quantity);
            item_getCurrent_quantity.setText(String.valueOf(getCurrent_quantity));
        }
        public void setimageitem(long current_wieght, long original_weight){
             //display icon
            ImageView item_icon = (ImageView) mView.findViewById(R.id.imageitem);
            if(current_wieght > original_weight/2)
                item_icon.setImageResource(R.drawable.fullicon);
            else if (current_wieght <= original_weight/ 2 && current_wieght > original_weight/ 4)
                item_icon.setImageResource(R.drawable.halficon);
            else if(current_wieght <= original_weight/ 4)
                item_icon.setImageResource(R.drawable.alerticon);
            //AddtoShoppingList
            
        }//setimageitem
    }// class itemsView

    public long RemainingDaymathed(String Exp_date) throws ParseException {
        //this method to calculation the remaining day then returned it
        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());

        Calendar Calendartoday = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendartoday.setTime(mDateFormatter.parse(now));

        Calendar CalendarExe_date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        CalendarExe_date.setTime(mDateFormatter.parse(Exp_date));


        long diff = CalendarExe_date.getTimeInMillis() - Calendartoday.getTimeInMillis(); //result in millis

        long days = diff / (24 * 60 * 60 * 1000);

        return days;

    }



}//big class