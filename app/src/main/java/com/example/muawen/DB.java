package  com.example.muawen;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import  android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteOpenHelper ;
import android.util.Log;

import java.util.ArrayList;


public class DB extends SQLiteOpenHelper {


    public static final String name = "data.db" ;
    public DB( Context context) {
        super(context, name, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db ) {
        db.execSQL("create table mytable(id INTEGER PRIMARY KEY AUTOINCREMENT,Barcode Text ,Brand Text,Name Text,size Text,Price Text,Quantity Text) ");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mytable ");
        onCreate(db);

    }



    public Cursor getShoppingList()
    {


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from mytable ", null );
        return res;
    }

    public boolean HasthisBrand(SQLiteDatabase db, String barcode){
        Cursor Barcodes = db.rawQuery("select * from mytable where Barcode = '"+barcode+"';", null );
        if(Barcodes.getCount()==0)
          return false;
        else
           return true;
    }



    public boolean addData(SQLiteDatabase db,String barcode , String brand , String Name, String size, String Price, String quantity) {


        ContentValues contentValues = new ContentValues();
        contentValues.put("Barcode", barcode);
        contentValues.put("Brand", brand);
        contentValues.put("Name", Name);
        contentValues.put("size", size);
        contentValues.put("Price", Price);
        contentValues.put("Quantity", quantity);


        long result = db.insert("mytable", null, contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}