package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Suggestionitem extends AppCompatActivity {
    private RecyclerView SuggRecyclerView;
    FirebaseRecyclerAdapter<items, SuggView> firebaseUsersAdapter = null;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, User;
    private DatabaseReference ItemRef;
    String currentUserID;
    List<String> listUnllikeItem ;

    //shopping list
    DB mDatabaseSL= new DB(this);
    SQLiteDatabase db ;
    String Sugg_brand, Sugg_Name, Sugg_size, Sugg_price , suggestion;
    String Item_brand=  null;
    String Item_Name=null;
    String Item_size = null;
    String Item_price =null ;
    Product product =new Product();
    boolean Unlike;
    long itemSuggSize;
    Toolbar mToolbar;
    TextView no_sugg ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestionitem);

         mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("منتجات مقترحة");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
        User =UsersRef.child(currentUserID);
        //ItemRef = User.child("items");

        SuggRecyclerView = (RecyclerView) findViewById(R.id.suggestRecyclerView);
        SuggRecyclerView.setHasFixedSize(false);
        SuggRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = mDatabaseSL.getWritableDatabase();

        no_sugg = findViewById(R.id.noSuggest);
        no_sugg.setText("لا يوجد لديك مقترحات الآن");


        viewitemsuggest();

    }

    private void viewitemsuggest() {

        ItemRef =UsersRef.child(currentUserID).child("items");


        FirebaseRecyclerOptions<items> options = new FirebaseRecyclerOptions.Builder<items>()
                .setQuery(ItemRef.orderByChild("Decide_flag").equalTo("0"), items.class)
                .build();
        firebaseUsersAdapter = new FirebaseRecyclerAdapter<items, SuggView>(
                options) {
            @Override
            protected void onBindViewHolder(final SuggView holder, final int position, final items model) {

                no_sugg.setText("");

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                if(model.getSuggested_item().equals("-1")){
                    //notifyDataSetChanged();
                    if(model.getSuggestion_flag().equals("1")){
                        replacing(model.getProduct_ID(), getRef(position),model.getQuantity());

                        if (model.getSuggested_item().equals("-1")){
                            getRef(position).child("Suggested_item").setValue("Negativeone");
                        }


                    }else{
                        //  getRef(position).child("Suggested_item").setValue(upgrade(model.getProduct_ID()));
                        upgrade(model.getProduct_ID(), getRef(position), model);
                        if (model.getSuggested_item().equals("-1")){
                            getRef(position).child("Suggested_item").setValue("positiveone");
                        }

                    }
                }

                if(model.getSuggestion_flag().equals("1")){

                    holder.suggested("إستبدال");
                    holder.setItemBrand("لأنك لم تستهلك " );
                    holder.setItemSize(" قبل إنتهاء فترة الصلاحية " );

                }
                else if(model.getSuggestion_flag().equals("2")){

                    holder.suggested("ترقية");
                    holder.setItemBrand("لأنك أستهلكت " );
                    holder.setItemSize(" في أقل من أسبوع " );


                }

                //Current_wieght:It takes the value from the sensor
                DatabaseReference Product = rootRef.child("Product");
                //Item_Name=null;
                //item info
                Product.child(model.getProduct_ID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.hasChild("Name")) {
                                Item_Name = dataSnapshot.child("Name").getValue().toString();
                                product.setName(Item_Name);
                                String Name = " " + Item_Name + " ";
                                holder.setItemName(Name);


                            }
                            if (dataSnapshot.hasChild("Brand")) {
                                Item_brand = dataSnapshot.child("Brand").getValue().toString();
                                String Brand = " " + Item_brand + " ";

                                // holder.setItemBrand(Brand);

                            }
                            if (dataSnapshot.hasChild("Size")) {
                                Item_size = dataSnapshot.child("Size").getValue().toString();
                                String Size = " الذي حجمه " + Item_size + "مل ";
                                //holder.setItemSize(Size);

                            }
                            if (dataSnapshot.hasChild("Price")) {
                                Item_price = dataSnapshot.child("Price").getValue().toString();
                                String Price = " وسعره " + Item_price + "ريال ";
                                // holder.setItemPrice(Price);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ////item info________________________________________


                if(model.getSuggested_item().equals("Negativeone") ){

                    holder.setSugBrand("فإننا نقترح بتقليل الكميه إلى " + (model.getQuantity()-1) +" بدلاً من "+model.getQuantity());
                    holder.setSugName("");
                    holder.setSugSize("");
                    holder.setSugPrice("");


                }else if (model.getSuggested_item().equals("positiveone") ){

                    holder.setSugPrice("فإننا نقترح بزيادة الكمية إلى "+(model.getQuantity()+1)+" بدلاً من "+model.getQuantity());
                    holder.setSugName("");
                    holder.setSugBrand("");
                    holder.setSugSize("");




                }else {
                    holder.setSugPrice("");
                    holder.setSugBrand("");
                    Product.child(model.getSuggested_item()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild("Name")) {
                                    Sugg_Name= dataSnapshot.child("Name").getValue().toString();
                                    String Name = "فإننا نقترح بإستبداله " + Sugg_Name + "";
                                    holder.setSugName(Name);

                                }
                                if (dataSnapshot.hasChild("Brand")) {
                                    Sugg_brand = dataSnapshot.child("Brand").getValue().toString();
                                    String Brand = " " + Sugg_brand + " ";
                                    holder.setSugBrand(Brand);

                                }
                                if (dataSnapshot.hasChild("Size")) {
                                    Sugg_size = dataSnapshot.child("Size").getValue().toString();;
                                    String Size;
                                    if(model.getSuggestion_flag().equals("1")) {
                                        Size = "بحجم  (" + Sugg_size + "مل) ";
                                    }else{
                                        Size = " بحجم أكبر (" + Sugg_size + "مل) ";
                                    }
                                    holder.setSugSize(Size);

                                }
                                if (dataSnapshot.hasChild("Price")) {
                                    Sugg_price = dataSnapshot.child("Price").getValue().toString();
                                    String Price = " وسعره " + Sugg_price + "ريال ";
                                    // holder.setSugPrice(Price);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }//not Negativeone

                //confirm
                holder.Button_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        getRef(position).child("Decide_flag").setValue("1");
                        if(Item_brand!= null && Item_Name!= null) {
                            if (model.getSuggested_item().equals("positiveone")) {
                                mDatabaseSL.DeleteItem(db, currentUserID, model.getProduct_ID());
                                long Quantity = model.getQuantity() + 1;
                                AddtoShoppingList(model.getProduct_ID(), Quantity+"");
                                // insertData(db, model.getProduct_ID(), Item_brand, Item_Name, Item_size, Item_price, Quantity + "");
                                getRef(position).child("quantity").setValue(Quantity);

                            } else {
                                String Quantity = model.getQuantity() + "";
                                AddtoShoppingList(model.getSuggested_item(), Quantity);

                                // insertData(db, model.getSuggested_item(), Sugg_brand, Sugg_Name, Sugg_size, Sugg_price, Quantity);
                                mDatabaseSL.DeleteItem(db, currentUserID, model.getProduct_ID());

                                getRef(position).child("Product_ID").setValue(model.getSuggested_item());
                            }
                        }else
                            toastMessage("لقد حدث خطاء أعد المحاولة");


                    }

                    @NotNull
                    private View.OnClickListener getOnClickListener() {
                        return this;
                    }
                });
                //confirm___________________________________________________________

                //Reject
                holder.Button_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        getRef(position).child("Decide_flag").setValue("2");
                        if(!model.getSuggested_item().equals("Negativeone") && !model.getSuggested_item().equals("positiveone") ){
                            getRef(position).getParent().getParent().child("Unlike").child(model.getSuggested_item()).setValue("True");

                        }

                    }

                    @NotNull
                    private View.OnClickListener getOnClickListener() {
                        return this;
                    }
                });
                //Reject______________________________________________________________________________

                //  }//if flag

            }//onBindViewHolder


            @Override
            public Suggestionitem.SuggView onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestionitemview, parent, false);
                return new SuggView(view);
            }



        };

        SuggRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SuggRecyclerView.setAdapter(firebaseUsersAdapter);
        firebaseUsersAdapter.startListening();




    }

    public static class SuggView extends RecyclerView.ViewHolder{
        View mView;
        Button Button_reject, Button_confirm;

        public SuggView(View SuggView){
            super(SuggView);
            mView =SuggView;
            Button_reject = (Button) mView.findViewById(R.id.Suggest_reject);
            Button_confirm = (Button) mView.findViewById(R.id.Suggest_confirm);

        }
        @SuppressLint("ResourceAsColor")
        public void suggested(String sugg){
            TextView item_sugg = (TextView)mView.findViewById(R.id.suggested);
            item_sugg.setText(sugg);
            if(sugg.equals("إستبدال")){
                item_sugg.setTextColor(Color.parseColor("#CBED1808"));
            }else{
                item_sugg.setTextColor(Color.parseColor("#FF669900"));

            }


        }

        public void setItemName(String name){
            TextView item_name = (TextView)mView.findViewById(R.id.Item_name_Sugg);
            item_name.setText(name);
        }
        public void setItemBrand(String brand) {
            TextView item_brand = (TextView)mView.findViewById(R.id.Item_brand);
            item_brand.setText(brand);
        }

        public void setItemSize(String size) {
            TextView item_size= (TextView)mView.findViewById(R.id.Item_Size);
            item_size.setText(size);
        }

        public void setItemPrice(String Price) {
            TextView item_Price= (TextView)mView.findViewById(R.id.Item_Price);
            item_Price.setText(Price);
        }

        public void setSugName(String name) {
            TextView item_name = (TextView)mView.findViewById(R.id.suggest_name);
            item_name.setText(name);
        }
        public void setSugBrand(String brand) {
            TextView item_brand = (TextView)mView.findViewById(R.id.suggest_brand);
            item_brand.setText(brand);
        }

        public void setSugSize(String size) {
            TextView item_size= (TextView)mView.findViewById(R.id.suggest_Size);
            item_size.setText(size);
        }

        public void setSugPrice(String Price) {
            TextView item_Price= (TextView)mView.findViewById(R.id.suggest_Price);
            item_Price.setText(Price);
        }

    }// class itemsView


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


    private void replacing(String itemBarcode, final DatabaseReference refItem, long quantity) {
        if(quantity >1){
            refItem.child("Suggested_item").setValue("Negativeone");
            return;

        }


        Cursor res = mDatabaseSL.getItmeInfo(itemBarcode);
        res.moveToFirst();
        final String Brand = res.getString((res.getColumnIndex("Brand")));
        final String Name = res.getString(res.getColumnIndex("Name"));
        final long size = Long.parseLong(res.getString(res.getColumnIndex("size")));

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Product").orderByChild("Name").equalTo(Name);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String itemSugg;
                searchInProdct: {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        itemSugg = dataSnapshot.getKey();
                        // toastMessage(m+" tis is m");
                        if ((!dataSnapshot.child("Brand").getValue().toString().equals(Brand) && Long.parseLong(dataSnapshot.child("Size").getValue().toString()) == size)|| (dataSnapshot.child("Brand").getValue().toString().equals(Brand)&&Long.parseLong(dataSnapshot.child("Size").getValue().toString()) < size)) {
                            Unlike =false;
                            // refItem.child("Suggested_item").setValue(itemSugg);

                            Query query_unlike = User.child("Unlike");
                            final String finalM = itemSugg;
                            query_unlike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    String itme_unlike;

                                    searchInUnlke:
                                    {
                                        for (DataSnapshot dataSnapshot1 : snapshot2.getChildren()) {
                                            itme_unlike = dataSnapshot1.getKey();
                                            if (finalM.equals(itme_unlike)) {

                                                break searchInUnlke;

                                            }

                                        }//for 1
                                        Unlike =true;

                                        refItem.child("Suggested_item").setValue(finalM);
                                    }

                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Suggestionitem.this, "oops", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                        if(Unlike) {

                            break searchInProdct;
                        }
                    }//for 2
                }//seach
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Suggestionitem.this, "oops", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void upgrade(String itemBarcode, final DatabaseReference refItem, final items item) {

        Cursor res = mDatabaseSL.getItmeInfo(itemBarcode);
        res.moveToFirst();
        final String Brand = res.getString((res.getColumnIndex("Brand")));
        final String Name = res.getString(res.getColumnIndex("Name"));
        final long size = Long.parseLong(res.getString(res.getColumnIndex("size")));
        double Price = Double.parseDouble(res.getString(res.getColumnIndex("Price")));
        //product.setPrice(1000000);
        //toastMessage(product.getName() +product.getSize()+product.getPrice()+" this is progct");

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Product").orderByChild("Name").equalTo(Name);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String item;
                long itemSize ;
                itemSuggSize = 0;
                String itemSugg;

                searchInProdct: {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        item = dataSnapshot.getKey();
                        itemSize = Long.parseLong(dataSnapshot.child("Size").getValue().toString());


                        if (dataSnapshot.child("Brand").getValue().toString().equals(Brand) &&  itemSize> size) {
                            Unlike = false;

                            if (itemSize < itemSuggSize|| itemSuggSize ==0) {

                                Query query_unlike = User.child("Unlike");
                                final String finalM = item;
                                final long Size = itemSize;

                                query_unlike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        String itme_unlike;
                                        searchInUnlke:
                                        {
                                            for (DataSnapshot dataSnapshot1 : snapshot2.getChildren()) {
                                                itme_unlike = dataSnapshot1.getKey();
                                                if (finalM.equals(itme_unlike)) {

                                                    break searchInUnlke;

                                                }

                                            }//for 1
                                            Unlike = true;
                                            itemSuggSize=Size;
                                            refItem.child("Suggested_item").setValue(finalM);
                                        }

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Suggestionitem.this, "oops", Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }
                        }
                        if(Unlike) {
                            itemSuggSize=itemSize;
                            itemSugg=item;
                        }
                    }//for 2
                }//seach
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Suggestionitem.this, "oops", Toast.LENGTH_SHORT).show();
            }
        });
        // toastMessage(query+"");


    }




    private void AddtoShoppingList(final String product_barcode, final String Quantity) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference Product = rootRef.child("Product");
        Product.child(product_barcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("Brand")) {
                        Item_brand = dataSnapshot.child("Brand").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("Name")) {
                        Item_Name = dataSnapshot.child("Name").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("Price")) {
                        Item_price = dataSnapshot.child("Price").getValue().toString();


                    }
                    if (dataSnapshot.hasChild("Size")) {
                        Item_size = dataSnapshot.child("Size").getValue().toString();


                    }

                }

                if(Item_brand != null) {

                    insertData(db, product_barcode, Item_brand, Item_Name, Item_size, Item_price, Quantity);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void insertData(SQLiteDatabase db, String id, String product_brand, String product_name, String product_size, String product_price, String q) {
        boolean insertData = false;

        String userId = UsersRef.child(currentUserID).getKey();

        if(product_brand != null) {

            insertData = mDatabaseSL.addData(db,userId, id, product_brand, product_name, product_size, product_price, q);


        }

        if (insertData) {
            toastMessage("لقد أضفنا منتج إلى قائمة التسوق");
        } else {
            toastMessage("حدث خطاء أثناء إضافة المنتج لقائمة التسوق");
        }

    }



}

