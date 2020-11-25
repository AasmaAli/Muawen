package com.example.muawen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class seggAdapter extends FirebaseRecyclerAdapter<items , seggAdapter.seggHolder> {


        public seggAdapter(@NonNull FirebaseRecyclerOptions<items> options) {
            super(options);
        }




    @Override
        protected void onBindViewHolder(@NonNull seggHolder holder, int position, @NonNull items model) {
            holder.productText.setText(String.valueOf(model.getProduct_ID()));
            holder.suggestionText.setText(String.valueOf(model.getSuggested_item()));
            String flag = String.valueOf(model.getSuggestion_flag()) ;
            if (flag.equals("1"))
            {
                flag = " استبدال " ;
            }
            else if  (flag.equals("2"))
            {
                flag = " ترقية " ;
            }
            holder.suggestionFlag.setText(flag);

        }

        @NonNull
        @Override
        public seggHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_layout, parent, false);


            return new seggHolder(view);
        }

        class seggHolder extends RecyclerView.ViewHolder {

            TextView productText;
            TextView suggestionText;
            TextView suggestionFlag;

            public seggHolder(@NonNull View itemView) {
                super(itemView);
                productText = itemView.findViewById(R.id.text_product);
                suggestionText = itemView.findViewById(R.id.text_suggestion);
                suggestionFlag = itemView.findViewById(R.id.text_flag);
            }

        }//end holder

    } //end adapter

