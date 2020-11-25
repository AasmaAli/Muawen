package com.example.muawen;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder> {
    Context context;
    ArrayList<Order> order;
    DatabaseReference reference;
    public MyAdapter(Context c, ArrayList<Order> O) {
        context = c;
        order = O;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.my_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        holder.btn.setText(order.get(position).getID());
        holder.textView.setText(order.get(position).getName());
        holder.text.setText(order.get(position).getD());
        holder.textTime.setText(order.get(position).getT());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OrderNum=order.get(position).getName();
                String Status=order.get(position).getS();
                Intent intent = new Intent(context, Customer_ststus.class);
                intent.putExtra("the order number ",OrderNum);

                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return order.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
TextView textView,text,textTime;
        Button btn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.MyOrder);
textView = itemView.findViewById(R.id.text2);
text=itemView.findViewById(R.id.textdate);
textTime=itemView.findViewById(R.id.texttime);

        }


    }
}