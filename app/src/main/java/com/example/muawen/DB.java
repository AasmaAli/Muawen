package  com.example.muawen;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import  android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteOpenHelper ;

import java.util.ArrayList;


public class DB extends SQLiteOpenHelper {

    public static final String name = "data.db" ;
    public DB( Context context) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db ) {
        db.execSQL("create table mytable(id INTEGER PRIMARY KEY AUTOINCREMENT,Barcode Text ,Brand Text,Name Text,size Text,Price Text) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS mytable ");
        onCreate(db);

    }



    public Cursor getShoppingList()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from mytable ", null );
        return res;


    }
}
