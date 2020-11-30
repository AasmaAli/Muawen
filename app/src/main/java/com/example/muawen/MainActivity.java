package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.LinearLayout;
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
import java.util.ArrayList;
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
    double Original_weight_rnew ;
boolean delete_item;

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

            viewitem();

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

        }//if end}

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
                        finish();
                    }//end if

                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }



            });

            db = mDatabaseSL.getWritableDatabase();
           // mDatabaseSL.onUpgrade(db,db.getVersion() , db.getVersion() +1);

           // viewitem();

        }//else end

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if the customer apply the auto Order
                boolean auto_Order = (boolean) snapshot.child("Auto_order").getValue();//get value from firebase
                if (auto_Order) {//if true call  autoOrder();
                    autoOrder();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }//On Start


    private void viewitem() {


        ItemRef =UsersRef.child(currentUserID).child("items");

         final FirebaseRecyclerOptions<items> options = new FirebaseRecyclerOptions.Builder<items>()
                .setQuery(ItemRef, items.class)
                .build();
      firebaseUsersAdapter = new FirebaseRecyclerAdapter<items, itemsView>(
                options) {
          /*
          @Override
          public  int getItemCount(){
              int sizeItemm = options.getSnapshots().size();
              return (null != options ? sizeItemm : 0);
          }
*/

          @Override
            protected void onBindViewHolder(final itemsView holder, final int position, final items model) {

                holder.deleteitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("هل أنت متاكد من حذف  المنتج؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //make sensor available
                                        getRef(position).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (dataSnapshot.hasChild("Sensor")) {
                                                        String Sensor= dataSnapshot.child("Sensor").getValue().toString();

                                                        getRef(position).getParent().getParent().child("Sensors").child(Sensor).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        //delete item
                                        getRef(position).removeValue();
                                        //notifyItemRemoved(position);
                                       //firebaseUsersAdapter.notifyItemChanged(position);
                                        //firebaseUsersAdapter.notifyItemRemoved(position);
                                        //irebaseUsersAdapterf.notifyItemRemoved(position-1);
                                       //Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                                        //startActivity(refresh);
                                        //MainActivity.super.onBackPressed();
                                        //MainActivity.this.finish();
                                        //Intent newInt = new Intent("android.intent.action.MainActivity");
                                        //firebaseUsersAdapter<items>.notifyDataSetChanged();
                                     //  ((FirebaseRecyclerAdapter<items, itemsView>) firebaseUsersAdapter).notifyDataSetChanged();
                                        //notifyDataSetChanged();
                                      //  Setetnotifydatachanged();
                                        //notifyItemRemoved(position);
                                        //startActivity(new Intent(MainActivity.this, MainActivity.class));

                                      //  notifyItemRemoved(position);
                                       // position =null;
                                       // this.notify();
                                        //notifyDataSetChanged();


                                    }//if click yes end
                                })
                                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog deletemass= builder.create();
                        deletemass.show();


                    }

                    @NotNull
                    private View.OnClickListener getOnClickListener() {
                        return this;
                    }
                });//deleat




                holder.Updateitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Update_item(getRef(position) , model.getQuantity() , model.getProduct_ID() , model.getSensor());

                    }

                    //=@NotNull
                    //private View.OnClickListener getOnClickListener() {
                    //  return this;
                    //}
                });


                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                //Current_wieght:It takes the value from the sensor
                //Update current weight as per sensor reading
                DatabaseReference SensorRef = rootRef.child("Sensors");
                SensorRef.child(model.getSensor()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("Weight")) {
                                double SensorWeight= Double.parseDouble(dataSnapshot.child("Weight").getValue().toString());
                                if(dataSnapshot.getKey().equals(model.getSensor())) {
                                    if (SensorWeight >= model.getOriginal_weight()) {//If the sensor value is greater than the original weight
                                        if (!(model.getOriginal_weight() - model.getCurrent_wieght() < 6)) {
                                            if (model.getCurrent_quantity() > 1) {
                                                try {
                                                    getRef(position).child("Current_wieght").setValue(model.getOriginal_weight() - 5);
                                                    getRef(position).child("Current_quantity").setValue(model.getCurrent_quantity() - 1);
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    } else if (!(SensorWeight <= 2)) {//Ensure that the sensor has a value
                                        try {
                                            getRef(position).child("Current_wieght").setValue(SensorWeight);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                }
                holder.setRemainingDay(RemainingDay);
                //display the remaining wieght
                String Current_wieght = "الوزن المتبقي : "+ model.getCurrent_wieght()+ " غرام";
                holder.setcurrentweight(Current_wieght);
                //display the remaining quantity
                String Current_quantity = "الكمية المتبقية: "+ model.getCurrent_quantity();
                holder.setquantity(Current_quantity);
                holder.setimageitem(model);


                //call Add to shoping list
                int Suggestion_flag_Check= Integer.parseInt(model.getSuggestion_flag());
                if( Suggestion_flag_Check == 0 ) {
                    if (days < 0) {//expired item
                        try {
                            AddtoShoppingList(model, 1, getRef(position), model.getAdd_day());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (model.getCurrent_wieght() <= model.getOriginal_weight() /
                            4 && model.getCurrent_quantity() == 1) { //consumed item
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
        @SuppressLint("ResourceAsColor")
        public void setRemainingDay(String RemainingDay){
            TextView item_RemainingDay = (TextView)mView.findViewById(R.id.Item_RemainingDay);
            item_RemainingDay.setText(RemainingDay);
            if(RemainingDay.equals("لقد أنتهى تاريخ المنتج")){
                item_RemainingDay.setTextColor(Color.parseColor("#CBED1808"));
            }else{
                item_RemainingDay.setTextColor(Color.parseColor("#FF020005"));

            }

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
            //Comparison between the current weight and the original weight of the appropriate icon display.
            ImageView item_icon = (ImageView) mView.findViewById(R.id.imageitem);
            if(item.getCurrent_wieght()  > item.getOriginal_weight()/2)
                item_icon.setImageResource(R.drawable.fullicon);
            else if (item.getCurrent_wieght() <= item.getOriginal_weight()/ 2 && item.getCurrent_wieght() > item.getOriginal_weight()/ 4)
                item_icon.setImageResource(R.drawable.halficon);
            else if(item.getCurrent_wieght() <= item.getOriginal_weight()/ 4 && item.getCurrent_wieght()> (item.getOriginal_weight()/ 4)/2 ) {
                item_icon.setImageResource(R.drawable.quartericon);
            }
            else if(item.getCurrent_wieght()<= (item.getOriginal_weight()/ 4)/2 ) {
                item_icon.setImageResource(R.drawable.alerticon);
            }
        }


    }// class itemsView

    public long RemainingDaymathed(String time) throws ParseException {
        //this method to calculation the remaining day then returned it
        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());
        Calendar Calendartoday = Calendar.getInstance(TimeZone.getTimeZone("UTC"));//Taking the current time
        Calendartoday.setTime(mDateFormatter.parse(now));
        Calendar CalendarExe_date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));//Convert expiration date to millis
        CalendarExe_date.setTime(mDateFormatter.parse(time));
        long diff = CalendarExe_date.getTimeInMillis() - Calendartoday.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);//Convert millis to days
        return days;
    }


    private  String AddtoShoppingList(items item, int flag, DatabaseReference refItem, String add_day) throws ParseException {



        if(flag ==1){//suggest a replace item if expired.
            refItem.child("Decide_flag").setValue("0");
            refItem.child("Suggestion_flag").setValue("1");
        } else if(flag ==2){
            long days=0;
            try {
                days = RemainingDaymathed(add_day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(days>=-4){//suggest an upgrade iF the item is consumed in four days
                refItem.child("Decide_flag").setValue("0");
                refItem.child("Suggestion_flag").setValue("2");
            }else{//Otherwise, there is no suggestion only replenishment
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

                    insertData(db,Product_ID, Product_brand, Product_Name, Product_size, Product_price, quantity);

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

            //Add the basic item to the shopping list
            insertData = mDatabaseSL.addData(db,userId, id, product_brand, product_name, product_size, product_price, q);


        }

        if (insertData) {
            toastMessage("لقد أضفنا منتج إلى قائمة التسوق");
        } else {
            toastMessage("Something went wrong");
        }

    }

    //to dispay any message
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public boolean delete_item(final DatabaseReference RefItem) {
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
                        delete_item = true;

                    }//if click yes end
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete_item=false;
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog deletemass= builder.create();
        deletemass.show();
        return delete_item;


    }//delete_item end


    private void Update_item(final DatabaseReference ref2, final long quantity, final String product_id, final String sensor) {
        ref = ref2;


        toastMessage("On "+ ref);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هذا الخيار فقط إن وصلك الطلب من المتجر وتريد التجديد.. هل تريد فعل هذا الآن؟")
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
        //toastMessage("On 2 "+ ref);
        takeWeight(sensor);
        //toastMessage("On 666 "+ Original_weight_rnew);

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
        // toastMessage("333"+sensor + Original_weight_rnew  );
        //  Original_weight_rnew=0;

        if(Original_weight_rnew >2){

            Map userMap = new HashMap();
            userMap.put("Add_day", now);
            userMap.put("quantity", quantity);
            userMap.put("Current_quantity", quantity);
            userMap.put("Suggested_item", "-1");
            userMap.put("Suggestion_flag", "0");
            userMap.put("Decide_flag", "-1");
            userMap.put("Product_ID", product_id);
            userMap.put("Current_wieght", Original_weight_rnew - 5);
            userMap.put("Original_weight", Original_weight_rnew);
            userMap.put("Sensor", sensor);
            userMap.put("Exp_date", Exp_Date);
            toastMessage("222"+ userMap );
            ItemRef.child(String.valueOf(System.currentTimeMillis())).setValue(userMap);
            userMap.clear();
            toastMessage("333"+ userMap );


            refItem.removeValue();
            Original_weight_rnew=0;
           // this.notify();
        }
        else
            toastMessage("لم يتم وضع المنتج على الميزان الرجاء المحاولة مره أخرى");

    }

    private void takeWeight(String Sensornum) {
        // toastMessage("لقد حدث ");
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

    private void autoOrder ()
    { final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID).child("Order_time");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                { String currentDay = snapshot.getValue().toString();
                    switch (currentDay) {// change the current day to SimpleDateFormat function format so we can compare it
                        case "السبت":
                            currentDay = "Sat" ;break;
                        case "الأحد":
                            currentDay = "Sun" ;break;
                        case "الأثنين":
                            currentDay = "Mon" ;break;
                        case "الثلاثاء":
                            currentDay = "Tue" ;break;
                        case "الإربعاء":
                            currentDay = "Wed" ;break;
                        case "الخميس":
                            currentDay = "Thu" ;break;
                        case "الجمعة":
                            currentDay = "Fri" ;break; }
                    //get the the current day
                    SimpleDateFormat mُTimeFormatter2 = new SimpleDateFormat("EEE", Locale.ENGLISH);
                    String Day = mُTimeFormatter2.format(new Date());
                    if (Day.equals(currentDay))//if true then  place order process will be continue
                    {
                        DatabaseReference  OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");


                        Cursor res = mDatabaseSL.getShoppingList(currentUserID);
                        res.moveToFirst();
                        if (res.getCount() > 0) {
                            final ArrayList<OrderProduct> result = new ArrayList<>();
                            double total_price = 0.0;
                            while (res.isAfterLast() == false) {
                                String Barcode = res.getString(2);
                                String Name = res.getString(4);
                                long Size = Long.parseLong(res.getString(5));
                                double Price = Double.parseDouble(res.getString(6));
                                int quantity = Integer.parseInt(res.getString(7));
                                result.add(new OrderProduct(Barcode, Name, quantity,Size,Price));
                                total_price = total_price + Price;
                                res.moveToNext();

                            }
                            SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
                            String Data = mDateFormatter.format(new Date());

                            SimpleDateFormat mُTimeFormatter1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                            String time = mُTimeFormatter1.format(new Date());

                            //DatabaseReference ordersRef = OrderRef.child("Orders");
                            CustomerOrder order = new CustomerOrder(currentUserID, "ارسال", total_price, Data, time, result);

                            OrderRef.child(String.valueOf(System.currentTimeMillis())).setValue(order);

                            SQLiteDatabase mydb = mDatabaseSL.getWritableDatabase();
                            mDatabaseSL.DeleteShoppingList(mydb, currentUserID);

                            Toast.makeText(MainActivity.this ,"تم إنشاء طلبك وسوف يتم التوصيل خلال ٢٤ ساعة ",Toast.LENGTH_SHORT).show();



                        }// end res cheak
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }//end autoOrder

}//big class