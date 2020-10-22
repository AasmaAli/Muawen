package com.example.muawen;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText barcode ;
    private Button SaveInformationbuttion;
    private TextView item_name , item_size ,item_brand , Date , quantity ;
    private String Product_brand , Product_Name ,Product_size , Exp_Date ;
    int count_quantity =0;
    int ExpDateCheck=0;
    private Spinner sensor;
    public String sensorNum = null;


    DatePickerDialog.OnDateSetListener setListener;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef , ProductRef , ItemRef;
    String currentUserID ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        //database
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);

        //activity
        quantity = findViewById(R.id.add_quantity);
        SaveInformationbuttion = findViewById(R.id.Add_buttonSave);
        Date = findViewById(R.id.Date);

        //sensor
        ArrayAdapter adapterday;
        sensor=findViewById(R.id.Spinner_sensor);
        sensor.setOnItemSelectedListener(this);
        // SpinnerDay
        String[] Available_sensors = getAvailable_sensors();
        if(Available_sensors[0]==null)
         adapterday = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Collections.singletonList("لا يوحد مستشعر متاح"));
        else
        adapterday = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Available_sensors);

        adapterday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensor.setAdapter(adapterday);


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

    private String[] getAvailable_sensors() {
        DatabaseReference Sensors =UsersRef.child("Sensors");
        String[] strArray = new String[3];


        //strArray[0] = "one";
       // strArray[1] = "two";
        //strArray[2] = "three";


        return strArray;
    }


    protected void onStart() {
        super.onStart();

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
        else{
            //add item


            ItemRef = UsersRef.child("items");

            Map userMap = new HashMap();
            userMap.put("Add_day", now);
            userMap.put("quantity",Quantity );
            userMap.put("Current_quantity", Quantity);
            userMap.put("Suggested_item", "-1");
            userMap.put("Suggestion_flag", "0");
            userMap.put("Product_ID", BarCode);
            userMap.put("Current_wieght", 499);
            userMap.put("Original_weight", 500);
            userMap.put("Sensor", 5);
            userMap.put("Exp_date", Exp_Date);




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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.Spinner_order_day) {
            sensorNum = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}