package com.example.muawen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity  implements AdapterView.OnItemSelectedListener  {

    private DatabaseReference reference ;
    private DatabaseReference users ;
    private String userId ;
    private EditText email , phone , name , address ;
    String email_firebase ,phone_firebase ,name_firebase , address_firebase ,day_firebase ;
    private Spinner SpinnerDay;
    public String Theday = null;
    TextView order_day ;
    boolean Auto_order , changeOrderDay , isChangeOrderDay,oldAutoOrder;
    private RadioGroup Auto_orderGroup;
private ArrayAdapter<String> adapterday ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        users = FirebaseDatabase.getInstance().getReference().child("User");
        email = findViewById(R.id.Email_data2);
        phone =  findViewById(R.id.phone_data2);
        name =  findViewById(R.id.name_data2);
        address =  findViewById(R.id.address_data2);


        Auto_orderGroup = (RadioGroup)findViewById(R.id.Auto_order_RadioGroup);
        order_day = findViewById(R.id.order_time_data);
        SpinnerDay=findViewById(R.id.Spinner_order_day);
        SpinnerDay.setOnItemSelectedListener(this);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                 email_firebase = snapshot.child("Email").getValue().toString();
                 phone_firebase = snapshot.child("Phone_number").getValue().toString();
                 name_firebase = snapshot.child("Username").getValue().toString();
                 address_firebase = snapshot.child("Address").getValue().toString();
                 day_firebase = snapshot.child("Order_time").getValue().toString();
                 Auto_order = Boolean.parseBoolean(snapshot.child("Auto_order").getValue().toString());
                 oldAutoOrder = Auto_order ;
                email.setText(email_firebase);
                phone.setText(phone_firebase);
                name.setText(name_firebase);
                address.setText(address_firebase);
                order_day.setText(day_firebase);


                if (Auto_order) {
                    Auto_orderGroup.check(R.id.Auto_order_yes);
                    SpinnerDay.setEnabled(true);

                }
                else
                {
                    order_day.setText("---");
                    SpinnerDay.setEnabled(false);
                    Auto_orderGroup.check(R.id.Auto_order_no);
                }


                }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this ,"there is somtiong wrong",Toast.LENGTH_SHORT).show();
            }
        });



        Auto_orderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Auto_order_yes:
                        Auto_order= true;
                        order_day.setText(day_firebase);
                        SpinnerDay.setEnabled(true);

                        break;
                    case R.id.Auto_order_no:
                        Auto_order = false;
                        SpinnerDay.setEnabled(false);

                        break;




                }//end swithc


            }
        });

        String[] Day= getResources().getStringArray(R.array.Days);
        adapterday = new ArrayAdapter(EditProfile.this,android.R.layout.simple_spinner_item,Day);
        adapterday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDay.setAdapter(adapterday);

        int spinnerPosition = adapterday.getPosition(day_firebase);
        SpinnerDay.setSelection(spinnerPosition);

    }
    public void  update(View view) {
        isChangeOrderDay = (oldAutoOrder != Auto_order) ;
        changeOrderDay = (Theday!=day_firebase);
        if (isNameChange() || isPhoneChange() || isAddressChange() || isEmailChange() || changeOrderDay ||isChangeOrderDay   ) {


            if ( changeOrderDay ) {
                day_firebase = Theday;
                reference.child("Order_time").setValue(Theday);

            }

                if ( isChangeOrderDay)
                {
                    oldAutoOrder = Auto_order ;
                    reference.child("Auto_order").setValue(Auto_order);
                }

            Toast.makeText(EditProfile.this, "تم تحديث البيانات ", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(EditProfile.this ,"لا يوجد تحديث ",Toast.LENGTH_SHORT).show();

        }

    }

    public boolean isNameChange()
    {
        if (!name_firebase.equals(name.getText().toString()))
        {
         reference.child("Username").setValue(name.getText().toString());
         name_firebase = name.getText().toString();
         return true ;
        }
        else {
            return false;
        }
    }

    public boolean isEmailChange()
    {
        if (!email_firebase.equals(email.getText().toString()))
        {
            reference.child("Email").setValue(email.getText().toString());
            email_firebase = email.getText().toString();
            return true ;
        }
        else {
            return false;
        }
    }

    public boolean isPhoneChange()
    {
        if (!phone_firebase.equals(phone.getText().toString()))
        {
            reference.child("Phone_number").setValue(phone.getText().toString());
            phone_firebase = phone.getText().toString();
            return true ;
        }
        else {
            return false;
        }
    }

    public boolean isAddressChange()
    {
        if (!address_firebase.equals(address.getText().toString()))
        {
            reference.child("Address").setValue(address.getText().toString());
            address_firebase = address.getText().toString();
            return true ;
        }
        else {
            return false;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.Spinner_order_day){
            Theday =parent.getItemAtPosition(position).toString();



        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}