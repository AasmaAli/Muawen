package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
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

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    //update item
    DatePickerDialog.OnDateSetListener setListener;

    //local database for shopping list
    DB mDatabaseSL= new DB(this);
    SQLiteDatabase db ;

    String Product_Name, Product_brand, Product_price, Product_size,quantity , Exp_Date;
    double Original_weight_rnew;


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
    DatabaseReference ref;



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

                       // Intent setupIntent = new Intent(MainActivity.this, Setup.class);
                        //setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //startActivity(setupIntent);
                        //finish();
                    }//end if

                }

                @Override
                public void onCancelled( DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "أسوم أنا هنا ساعديني", Toast.LENGTH_SHORT).show();

                    //Intent setupIntent = new Intent(MainActivity.this, AddItem.class);
                    //setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //startActivity(setupIntent);
                }



            });

            db = mDatabaseSL.getWritableDatabase();
            //mDatabaseSL.onUpgrade(db,db.getVersion() , db.getVersion() +1);

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
            protected void onBindViewHolder(final itemsView holder, final int position, final items model) {
                toastMessage("THIS IS XXX "+getRef(position));


                if(Integer.parseInt(model.getSuggestion_flag()) ==-1){
                    getRef(position).child("Suggestion_flag").setValue("0");
                    //toastMessage("On Original_weight_rnew in Suggestion_flag "+ Original_weight_rnew);


                }
               // if(model.getExp_date().equals("-1")){
                 //   getRef(position).child("Exp_date").setValue(Exp_Date);

                //}

                if(model.getCurrent_quantity() == 0){
                    getRef(position).child("Current_quantity").setValue(model.getQuantity());


               // if(model.getOriginal_weight() ==-100){
                 //   String Sensornum =  model.getSensor();
                   // takeWeight(Sensornum) ;
                    /*
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference SensorRef = rootRef.child("Sensors");
                    SensorRef.child(Sensornum).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild("Weight")) {
                                    double Original_weight_rnew2 = Double.parseDouble(dataSnapshot.child("Weight").getValue().toString());
                                    if(Original_weight_rnew2>= 2 && model.getOriginal_weight() == -100 ){

                                        getRef(position).child("Original_weight").setValue(Original_weight_rnew2);
                                        getRef(position).child("Current_wieght").setValue(Original_weight_rnew2-5);
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                     */

               // }// if weight
                     }



                holder.Updateitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        //holder.thelayout.setVisibility(View.GONE);
                        Update_item(getRef(position) , model.getQuantity() , model.getProduct_ID() , model.getSensor());
                       // Update_item1(getRef(position));
                    }

                    //=@NotNull
                    //private View.OnClickListener getOnClickListener() {
                      //  return this;
                    //}
                });


                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                //Current_wieght:It takes the value from the sensor
                DatabaseReference SensorRef = rootRef.child("Sensors");


                SensorRef.child(model.getSensor()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("Weight")) {
                                double SensorWeight= Double.parseDouble(dataSnapshot.child("Weight").getValue().toString());
                                toastMessage(model.getSensor()+" بشكل صحيح");

                                if (SensorWeight >= model.getOriginal_weight()) {
                                    if( !(model.getOriginal_weight()- model.getCurrent_wieght()<6) ) {
                                        if( model.getCurrent_quantity()>1 ){
                                            try {
                                            getRef(position).child("Current_wieght").setValue(model.getOriginal_weight()-5);
                                            getRef(position).child("Current_quantity").setValue(model.getCurrent_quantity()-1);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }else if (!(SensorWeight <= 2)){
                                    try {
                                        getRef(position).child("Current_wieght").setValue(SensorWeight);
                                    } catch (Exception e) {
                                    }

                                }


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //_________________


                //display the Name of Product
                String Product_ID = model.getProduct_ID();
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
/*
                    try {
                        AddtoShoppingList(model , 1 , getRef(position), model.getAdd_day());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    */

                }
                holder.setRemainingDay(RemainingDay);
                //holder.deleteitem();

                //display the remaining wieght
                String Current_wieght = "الوزن المتبقي : "+ model.getCurrent_wieght()+ " غرام";

                holder.setcurrentweight(Current_wieght);

                //display the remaining quantity
                String Current_quantity = "الكمية المتبقية: "+ model.getCurrent_quantity();
                holder.setquantity(Current_quantity);

                //display icon
               /* if(model.getCurrent_wieght() <= model.getOriginal_weight()/ 4) {
                    try {
                        AddtoShoppingList(model, 2, getRef(position), model.getAdd_day());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/


                holder.setimageitem(model);

                holder.deleteitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        delete_item(getRef(position));
                    }

                    //@NotNull
                    //private View.OnClickListener getOnClickListener() {
                      //  return this;
                    //}
                });



                //call Add to shoping list if neeed
                int Suggestion_flag_Check= Integer.parseInt(model.getSuggestion_flag());
                if( Suggestion_flag_Check == 0 ) {
                    if (days < 0) {
                        try {

                            AddtoShoppingList(model, 1, getRef(position), model.getAdd_day());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (model.getCurrent_wieght() <= model.getOriginal_weight() / 4 && model.getCurrent_quantity() == 1) {
                        try {
                            AddtoShoppingList(model, 2, getRef(position), model.getAdd_day());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }//big if
            }//onBindViewHolder
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
        ImageButton deleteitem , Updateitem;
         public itemsView(View itemView){
           super(itemView);
           mView =itemView;
           deleteitem = (ImageButton) mView.findViewById(R.id.Buttons_DeleteItem);
           Updateitem = (ImageButton) mView.findViewById(R.id.Buttons_Update);

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
        public void setimageitem(items item){
             //display icon
            ImageView item_icon = (ImageView) mView.findViewById(R.id.imageitem);

            if(item.getCurrent_wieght()  > item.getOriginal_weight()/2)
                item_icon.setImageResource(R.drawable.fullicon);
            else if (item.getCurrent_wieght() <= item.getOriginal_weight()/ 2 && item.getCurrent_wieght() > item.getOriginal_weight()/ 4)
                item_icon.setImageResource(R.drawable.halficon);
            else if(item.getCurrent_wieght() <= item.getOriginal_weight()/ 4) {
                item_icon.setImageResource(R.drawable.alerticon);
            }

        }//setimageitem



    }// class itemsView

    public long RemainingDaymathed(String time) throws ParseException {
        //this method to calculation the remaining day then returned it
        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());

        Calendar Calendartoday = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendartoday.setTime(mDateFormatter.parse(now));

        Calendar CalendarExe_date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        CalendarExe_date.setTime(mDateFormatter.parse(time));


        long diff = CalendarExe_date.getTimeInMillis() - Calendartoday.getTimeInMillis(); //result in millis

        long days = diff / (24 * 60 * 60 * 1000);

        return days;

    }


    private  String AddtoShoppingList(items item, int flag, DatabaseReference refItem, String add_day) throws ParseException {



        //flag in databaes
        if(flag ==1){
            //expired replac

            refItem.child("Suggestion_flag").setValue("1");
        }
        else if(flag ==2){
            long days=0;
            try {
                days = RemainingDaymathed(add_day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(days>=-3){
                //upgrade
                refItem.child("Suggestion_flag").setValue("2");
            }else{
                //replenishment
                refItem.child("Suggestion_flag").setValue("3");
            }
        }

        final String Product_ID = item.getProduct_ID();
        quantity = String.valueOf(item.getQuantity());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference Product = rootRef.child("Product");

        Product.child(Product_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("Brand")) {
                        Product_brand = dataSnapshot.child("Brand").getValue().toString();

                    }
                    if (dataSnapshot.hasChild("Name")) {
                        Product_Name = dataSnapshot.child("Name").getValue().toString();

                    }
                    if (dataSnapshot.hasChild("Price")) {
                        Product_price = dataSnapshot.child("Price").getValue().toString();


                    }
                    if (dataSnapshot.hasChild("Size")) {
                        Product_size = dataSnapshot.child("Size").getValue().toString();


                    }

                }

                if(Product_brand != null) {

                    insertData(db,dataSnapshot.toString(), Product_brand, Product_Name, Product_size, Product_price, quantity);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
return null;
    }

    private void insertData(SQLiteDatabase db, String id, String product_brand, String product_name, String product_size, String product_price, String q) {
        boolean insertData = false;

        String userId = UsersRef.child(currentUserID).getKey();

        if(product_brand != null) {

            insertData = mDatabaseSL.addData(db,userId, id, product_brand, product_name, product_size, product_price, q);


        }

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }

    }

    //to dispay any message
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void delete_item(final DatabaseReference RefItem) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل أنت متاكد من حذف  المنتج؟")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //make sensor available
                        RefItem.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("Sensor")) {
                                        String Sensor= dataSnapshot.child("Sensor").getValue().toString();

                                        RefItem.getParent().getParent().child("Sensors").child(Sensor).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //delete item
                        RefItem.removeValue();
                    }//if click yes end
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        return;
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog deletemass= builder.create();
        deletemass.show();

    }//delete_item end
    

    private void Update_item(final DatabaseReference ref2, final long quantity, final String product_id, final String sensor) {
        ref = ref2;


        toastMessage("On "+ ref);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل وصل طلبك وتريد تجديد المنتج؟ ")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {//___________________________________________________________
                        Calendar calendar = Calendar.getInstance();
                        final int year = calendar.get(Calendar.YEAR);
                        final int month = calendar.get(Calendar.MONTH);
                        final int day = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);



                        DatePickerDialog datePickerDialog = new DatePickerDialog(

                        MainActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth
                                ,setListener,year,day,month);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();

                        setListener= new DatePickerDialog.OnDateSetListener() {



                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                                month = month+1;
                                if(month<10 && dayOfMonth<10){

                                    Exp_Date= year+"/0"+month+"/0"+dayOfMonth;

                                }
                                else if (month<10 ){
                                    Exp_Date= year+"/0"+month+"/"+dayOfMonth;
                                }else if (dayOfMonth<10){
                                    Exp_Date= year+"/"+month+"/0"+dayOfMonth;

                                }else {
                                    Exp_Date= year+"/"+month+"/"+dayOfMonth;

                                }
                                long days=-1;
                                try {
                                    days = RemainingDaymathed(Exp_Date);

                                } catch (ParseException e) {
                                    e.printStackTrace();

                                }
                                if(days>0){
                                    //call Update_in_Database

                                    PutItem(ref, Exp_Date , quantity  , product_id,sensor);

                                }else{
                                    toastMessage("التاريخ الذي أدخلته قديم يرجى إدخال التاريخ بشكل صحيح");
                                }

                            }
                        };


                    }//if click yes end_______________________________________________________________________________________________-
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        return;
                    }//if click no end
                });
        // Create the AlertDialog object and return it
        AlertDialog deletemass= builder.create();
        deletemass.show();


    }//Update_item

    private void PutItem(final DatabaseReference ref2, final String exp_Date, final long quantity,final String product_id, final String sensor) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.put_on_the_sensor, null);
        Button but_OK= (Button)mView.findViewById(R.id.pou_on_the_sensor);
        toastMessage("On 2 "+ ref);
        takeWeight(sensor);
        toastMessage("On 666 "+ Original_weight_rnew);

        alert.setView(mView);
        final AlertDialog alertDialog =alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        but_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_in_Database(ref, exp_Date, quantity,product_id ,sensor );

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void Update_in_Database(DatabaseReference refItem, String exp_Date, long quantity, String product_id, String sensor) {
        //delete_item(ref);
        refItem = ref;
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
        ItemRef = UsersRef.child("items");
        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());
        takeWeight(sensor);
        toastMessage("333"+sensor + Original_weight_rnew  );
      //  Original_weight_rnew=0;

        if(Original_weight_rnew >2){
            toastMessage("لقد in if to change  ");


            Map userMap = new HashMap();
    userMap.put("Add_day", now);
    userMap.put("quantity", quantity);
    userMap.put("Current_quantity", quantity);
    userMap.put("Suggested_item", "-1");
    userMap.put("Suggestion_flag", "0");
    userMap.put("Product_ID", product_id);
    userMap.put("Current_wieght", Original_weight_rnew - 5);
    userMap.put("Original_weight", Original_weight_rnew);
    userMap.put("Sensor", sensor);
    userMap.put("Exp_date", Exp_Date);
    ItemRef.child(String.valueOf(System.currentTimeMillis())).setValue(userMap);


    refItem.removeValue();
            Original_weight_rnew=0;
            }
else
    toastMessage("ضع المنتج الجديد على المستشعر واعد خطوات التجديد ");


        /*

        refItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                        toastMessage("On 8");

                        if (dataSnapshot.hasChild("Sensor")) {
                            String Sensornum = dataSnapshot.child("Sensor").getValue().toString();
                            for (int i = 0; i < 1; i++) {

                               // takeWeight(Sensornum);
                            }
                        }
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/

       // if(Original_weight_rnew>= 2){
        /*toastMessage("On Original_weight_rnew "+ Original_weight_rnew);




        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());

        refItem.child("Exp_date").setValue(Exp_Date);
        refItem.child("Add_day").setValue(now);
        refItem.child("Suggestion_flag").setValue("-1");
        refItem.child("Suggested_item").setValue("-1");
        refItem.child("Current_quantity").setValue(0);
        refItem.child("Original_weight").setValue(400);
        refItem.child("Current_wieght").setValue(200);

*/


      //  }//end  if(Original_weight_rnew>= 2)
       // else {
          //  toastMessage("لقد حدث خطاء قم بوضع المنتج على الميزان وأعد التجديد");

        //}

    }

    private void takeWeight(String Sensornum) {
          toastMessage("لقد حدث ");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference SensorRef = rootRef.child("Sensors");

        SensorRef.child(Sensornum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("Weight")) {
                        Original_weight_rnew = Double.parseDouble(dataSnapshot.child("Weight").getValue().toString());
                       // toastMessage("this is"+ Original_weight_rnew);
                        //ref.child("Original_weight").setValue(Original_weight_rnew);
                        //ref.child("Current_wieght").setValue(Original_weight_rnew-5);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}//big class