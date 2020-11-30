 package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.R.layout.simple_list_item_1;

public class ViewShoppingList extends AppCompatActivity {
    public  DB db  = new DB(this);



    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> totalprice = new ArrayList<>();
    Double total = 0.0;

    private ListView price;
    private Button button;
    private ListView list;
    private FirebaseAuth mAuth ;
    private DatabaseReference UsersRef;
    String currentUserID;
    private TextView empty_text;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        if (currentUserID != null) {
            UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
            final String userId = UsersRef.child(currentUserID).getKey();


            list = findViewById(R.id.list);
            price = findViewById(R.id.price);
            button = findViewById(R.id.button);


            Cursor res = db.getShoppingList(userId);
            res.moveToFirst();
            if (res.getCount() > 0) {

                while (res.isAfterLast() == false) {

                    String Brand = res.getString(3);
                    String Name = res.getString(4);
                    String size = res.getString(5);
                    String Price = res.getString(6);
                    String quantity = res.getString(7);

                    arrayList.add(" اسم المنتج: "+Name+" الحجم: "+size+" نوع المنتج: "+Brand+" السعر: "+Price+" العدد: "+quantity);
                    if (Price != null)
                        total = total + (Double.parseDouble(Price));

                    res.moveToNext();
                }

                totalprice.add("   السعر الكلي : " + total + "    ");

                ArrayAdapter arrayAdapter = new ArrayAdapter(this, simple_list_item_1, arrayList);
                list.setAdapter(arrayAdapter);
                //long size =  Long.parseLong(res.getString(res.getColumnIndex("size")));
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, simple_list_item_1, totalprice);
                price.setAdapter(arrayAdapter2);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        placeOrder(userId);


                    }
                });
            } // end count
            else {
                empty_text=findViewById(R.id.empty_shopping_list);
                empty_text.setText(" قائمة التسوق فارغة ");
            }
        }//not null

    }//onCreate



    public  void placeOrder(String UserId) {
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
         String userId = UsersRef.child(currentUserID).getKey();
        DatabaseReference  OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        final DatabaseReference itemRef =UsersRef.child(userId).child("items");
        Cursor res = db.getShoppingList(UserId);//retrieve local shopping list
        res.moveToFirst();
        if (res.getCount() > 0) {
            final ArrayList<OrderProduct> result = new ArrayList<>();
            double total_price = 0.0;
            while (res.isAfterLast() == false) {//get the product info
                String Barcode = res.getString(2);
                String Name = res.getString(4);
                long Size = Long.parseLong(res.getString(5));
                double Price = Double.parseDouble(res.getString(6));
                int quantity = Integer.parseInt(res.getString(7));
                result.add(new OrderProduct(Barcode, Name, quantity,Size,Price));
                total_price = total_price + Price;
                res.moveToNext(); }
            // get current date and time
            SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            String Data = mDateFormatter.format(new Date());
            SimpleDateFormat mُTimeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            String time = mُTimeFormatter.format(new Date());
            //creat new object
            CustomerOrder order = new CustomerOrder(UserId, "ارسال", total_price, Data, time, result);
            OrderRef.child(String.valueOf(System.currentTimeMillis())).setValue(order);
            SQLiteDatabase mydb = db.getWritableDatabase();
            db.DeleteShoppingList(mydb, UserId);//delete local shopping list
            Toast.makeText(ViewShoppingList.this ,"تم إنشاء طلبك ",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ViewShoppingList.this, ViewOrders.class);
            startActivity(i); }
        else // if shopping list is empty
        { Toast.makeText(ViewShoppingList.this ," لم يتم إنشاء الطلب لأن قائمة التسوق فارغة ",Toast.LENGTH_SHORT).show(); }

    }


}//class