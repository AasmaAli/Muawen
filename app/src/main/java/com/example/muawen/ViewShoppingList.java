package com.example.muawen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class ViewShoppingList extends AppCompatActivity {
    DB db = new DB(this);
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> totalprice = new ArrayList<>();
    int total = 0;

    private ListView price;
    private Button button;
    private ListView list;
    private FirebaseAuth mAuth ;
    private DatabaseReference UsersRef;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        if(currentUserID != null) {
            UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
            final String userId = UsersRef.child(currentUserID).getKey();



        list = findViewById(R.id.list);
                price = findViewById(R.id.price);
                button = findViewById(R.id.button);


                Cursor res = db.getShoppingList(userId);
                res.moveToFirst();

                while(res.isAfterLast()==false)

                {

                        String Brand = res.getString(3);
                        String Name = res.getString(4);
                        String size = res.getString(5);
                        String Price = res.getString(6);
                        String quantity =res.getString(7);

                        arrayList.add(Name + " " + Brand + "            " + size + " غرام         " + quantity + "           " + Price + "  ريال          ");
                        if (Price != null)
                            total = total + (Integer.parseInt(Price));

                    res.moveToNext();
                }

                totalprice.add("   السعر الكلي : "+total +"    ");

                ArrayAdapter arrayAdapter = new ArrayAdapter(this, simple_list_item_1, arrayList);
                list.setAdapter(arrayAdapter);

                ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, simple_list_item_1, totalprice);
                price.setAdapter(arrayAdapter2);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plaseOrder(userId);


                    }
                });

        }//not null

    }//onCreate

    public void plaseOrder(String userId) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference("server/saving-data/fireblog");

        DatabaseReference  OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        Cursor res = db.getShoppingList(userId);
        res.moveToFirst();
        final ArrayList<Product> result = new ArrayList<>();
        int total_price = 0;
        while (res.isAfterLast() == false) {
            //int Id = Integer.parseInt(res.getString(Integer.parseInt("id")));
            String Barcode = res.getString((res.getColumnIndex("Barcode")));
            String Name = res.getString(res.getColumnIndex("Name"));
            String size = res.getString(res.getColumnIndex("size"));
            int Price = Integer.parseInt(res.getString(res.getColumnIndex("Price")));
            result.add(new Product( Barcode, Name, size, Price));
            total_price = total_price + Price;
            res.moveToNext();

        }

        //DatabaseReference ordersRef = OrderRef.child("Orders");
        Order order  = new Order(mAuth.getCurrentUser().getUid(),"تجهيز" ,  total_price,result );

        OrderRef.child(String.valueOf(System.currentTimeMillis())).setValue(order) ;

    }


}//class

