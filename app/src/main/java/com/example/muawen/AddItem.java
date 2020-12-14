package com.example.muawen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddItem extends AppCompatActivity  {
    public static  EditText barcode ;
    public static String ScanQRCode;
    public double Original_weight ;
    public boolean CheckSensor;
    private ImageView scan_barcode_item;
    private ImageView scan_QR_item;
    private Button SaveInformationbuttion;
    private TextView item_name , item_size ,item_brand , Date , quantity ;
    private String Product_brand , Product_Name ,Product_size , Exp_Date ;
    int count_quantity =0;
    int ExpDateCheck=0;
    public String sensorNum = null;
    private Toolbar mToolbar;



    DatePickerDialog.OnDateSetListener setListener;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef , ProductRef , ItemRef;
    String currentUserID ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("إضافة منتج");

        //database
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);

        //activity
        quantity = findViewById(R.id.add_quantity);
        SaveInformationbuttion = findViewById(R.id.Add_buttonSave);
        Date = findViewById(R.id.Date);

        //barcode

      //Barcode reading
        scan_barcode_item= findViewById(R.id.Buttons_add_barcode);//Define barcode the button
        scan_barcode_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This will move it to open the camera and capture the code
                startActivity(new Intent(getApplicationContext(),ScanCodeActivity.class));
                //This method call to display the rest of the product information
                Productinfo(barcode.getText().toString());
            }
        });

        //QR reading
        scan_QR_item= findViewById(R.id.QR);//Define QR code the button
        scan_QR_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This will move it to open the camera and capture the code
                startActivity(new Intent(getApplicationContext(),ScanQR.class));
                //This method calls to display a message telling the user to put the product on the scale
                PutItem();
            }
        });

        //______________________-

        //Date
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddItem.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth
                        ,setListener,year,day,month);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
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
                ExpDateCheck=1;
                Date.setText("تاريخ الأنتهاء:  "+ Exp_Date);
            }
        };






        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    SaveNewItem();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }//onCreate

    public void PutItem() {
         final AlertDialog.Builder alert = new AlertDialog.Builder(AddItem.this);
         View mView = getLayoutInflater().inflate(R.layout.put_on_the_sensor, null);
         Button but_OK= (Button)mView.findViewById(R.id.pou_on_the_sensor);
        alert.setView(mView);
        final AlertDialog alertDialog =alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        but_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeWeight();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }//PutItem

    private boolean CheckSensor() {
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
if(ScanQRCode==null){
    return false;
}
        //make sensor available
        UsersRef.child("Sensors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(ScanQRCode)) {
                        CheckSensor= false;

                    }else{
                        CheckSensor = true;
                    }
                }
                else {
                    CheckSensor = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return CheckSensor;
    }//CheckSenso

   // Read the original weight of the sensor
    private void takeWeight() {
        Original_weight =0;
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference SensorRef = rootRef.child("Sensors");
            try {
                SensorRef.child(ScanQRCode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(" Weight")) {
                                Original_weight = Double.parseDouble(dataSnapshot.child(" Weight").getValue().toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
        }
    }


    protected void onStart() {
        super.onStart();
        CheckSensor();

        barcode = findViewById(R.id.add_barcode);
        item_name = (TextView) findViewById(R.id.item_name);
        item_size = (TextView) findViewById(R.id.item_size);
        item_brand = (TextView) findViewById(R.id.item_brand);

        barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(barcode != null);
                {
                    Productinfo(barcode.getText().toString());
                }



            }
        });



    }//onStart

    private void Productinfo(String barcode) {
        ProductRef =FirebaseDatabase.getInstance().getReference().child("Product");
        DatabaseReference product = ProductRef.child(barcode);

        product.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("Brand")) {
                         Product_brand = dataSnapshot.child("Brand").getValue().toString();
                        item_brand.setText("الشركة: "+ Product_brand);


                    }
                    if (dataSnapshot.hasChild("Name")) {
                         Product_Name = dataSnapshot.child("Name").getValue().toString();
                        item_name.setText("أسم المنتج: "+ Product_Name);


                    }
                    if (dataSnapshot.hasChild("Size")) {
                         Product_size = dataSnapshot.child("Size").getValue().toString();
                        item_size.setText("الحجم: "+ Product_size);

                    }

                }else {
                    item_name.setText(" ");
                    item_brand.setText("الشريط غير صحيح");
                    item_size.setText(" ");
                    Product_brand =null;
                    Product_Name =null;
                    Product_size = null;


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }//Productinfo

    private void SaveNewItem() throws ParseException {
        String BarCode = barcode.getText().toString();
        long Quantity = count_quantity;
        long days=0;
        takeWeight();



        //Add day
        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String now = mDateFormatter.format(new Date());
        if(ExpDateCheck!= 0) {
            //Check date
            Calendar Calendartoday = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Calendartoday.setTime(mDateFormatter.parse(now));

            Calendar CalendarExe_date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            CalendarExe_date.setTime(mDateFormatter.parse(Exp_Date));


            long diff = CalendarExe_date.getTimeInMillis() - Calendartoday.getTimeInMillis(); //result in millis

             days = diff / (24 * 60 * 60 * 1000);
        }


        //Verify the information entered
        if (BarCode.isEmpty() || Product_Name.isEmpty()){
            Toast.makeText(this, "رجاءً أكتب الشريط بشكل صحيح", Toast.LENGTH_SHORT).show();
        }
        else if (Quantity==0){
            Toast.makeText(this, "رجاءً أدخل الكمية", Toast.LENGTH_SHORT).show();
        }
        else if (ExpDateCheck==0 ){
            Toast.makeText(this,  "رجاءً أدخل تاريخ الأنتهاء", Toast.LENGTH_SHORT).show();
        }else if(days<=0){
            Toast.makeText(this,  "التاريخ الذي أدخلته قديم يرجى إدخال التاريخ بشكل صحيح", Toast.LENGTH_SHORT).show();
        }
        else if(ScanQRCode ==null ){
            Toast.makeText(this,  "رجاءً قم بمسح الشريط الذي على الميزان ", Toast.LENGTH_SHORT).show();
        }else if(!CheckSensor()){
            Toast.makeText(this,  "هذا الميزان مستخدم لمنتج أخر", Toast.LENGTH_SHORT).show();
        }else if (Original_weight <= 2){
            Toast.makeText(this,  "لم يتم وضع المنتج على الميزان الرجاء المحاولة مره أخرى", Toast.LENGTH_SHORT).show();

        }
        else{

            //Add item
            ItemRef = UsersRef.child("items");
            Map userMap = new HashMap();
            //Entered the system, it is the day added the item
            userMap.put("Add_day", now);
            //Default values are added automatically for each item
            userMap.put("Suggested_item", "-1");
            userMap.put("Suggestion_flag", "0");
            userMap.put("Decide_flag", "-1");
            //The original weight is taken from the scale when placing the item
            userMap.put("Current_wieght", Original_weight-5);
            userMap.put("Original_weight", Original_weight);
            //Entered from the user
            userMap.put("Product_ID", BarCode);
            userMap.put("Sensor", ScanQRCode);
            userMap.put("Exp_date", Exp_Date);
            userMap.put("quantity",Quantity );
            userMap.put("Current_quantity", Quantity);

            //Add all data to the database
            ItemRef.getParent().child("Sensors").child(ScanQRCode).setValue(true);
            ItemRef.child(String.valueOf(System.currentTimeMillis())).setValue(userMap);
            Intent mainIntent = new Intent(AddItem.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }







    }//SaveNewItem

    public void decrement(View view) {
        if(count_quantity<=0) {
            count_quantity++;
            quantity.setText("" + count_quantity);
        }else{
            if(count_quantity-1 != 0)
            count_quantity--;
            quantity.setText("" + count_quantity);
        }

    }

    public void increment(View view) {
        count_quantity++;
        quantity.setText(""+count_quantity);
    }


}