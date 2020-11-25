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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class ViewShoppingList extends AppCompatActivity {
    DB db = new DB(this);
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> totalprice = new ArrayList<>();
    int total = 0;

    private ListView price;
    private Button button;
    private TextView textView;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);



                list = findViewById(R.id.list);
                price = findViewById(R.id.price);
                button = findViewById(R.id.button);


                Cursor res = db.getShoppingList();
                res.moveToFirst();

                while(res.isAfterLast()==false)

                {
                    String Brand = res.getString(2);
                    String Name = res.getString(3);
                    String size = res.getString(4);
                    String Price = res.getString(5);
                    String quantity =res.getString(6);

                    arrayList.add(Name+" "+Brand+"            "+ size+" غرام         "+quantity+"           "+Price +"  ريال          ");
                  if(Price != null)
                    total = total + (Integer.parseInt(Price));
                    res.moveToNext();

                }

                totalprice.add("   السعر الكلي : "+total +"    ");

                ArrayAdapter arrayAdapter = new ArrayAdapter(this, simple_list_item_1, arrayList);
                list.setAdapter(arrayAdapter);
                long size =  Long.parseLong(res.getString(res.getColumnIndex("size")));
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, simple_list_item_1, totalprice);
                price.setAdapter(arrayAdapter2);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent (ViewShoppingList.this, Suggestionitem.class);
                        startActivity(intent);

                    }
                });

            }


        }

