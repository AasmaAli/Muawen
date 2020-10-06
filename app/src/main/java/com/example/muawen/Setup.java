package com.example.muawen;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class Setup extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    private EditText UserName , mobile , Address ;
    private RadioGroup Auto_orderGroup;
    private Spinner  SpinnerDay;
    private Button SaveInformationbuttion;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String currentUserID , currentUserEmail;
    private ProgressDialog loadingBar;
    boolean Auto_order = Boolean.parseBoolean(null);
    public String Theday = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        currentUserEmail= mAuth.getCurrentUser().getEmail();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
        loadingBar = new ProgressDialog(this);



        UserName =findViewById(R.id.user_name);
        mobile =findViewById(R.id.user_mobile);
        Address =findViewById(R.id.user_address);
        Auto_orderGroup = (RadioGroup)findViewById(R.id.Auto_order_RadioGroup);
        SpinnerDay=findViewById(R.id.Spinner_order_day);
        SpinnerDay.setOnItemSelectedListener(this);
        SaveInformationbuttion = findViewById(R.id.buttonSave);

        //Auto_orderGroup
        Auto_orderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Auto_order_yes:
                        Auto_order= true;
                        break;
                    case R.id.Auto_order_no:
                        Auto_order = false;
                        break;

                    default:
                        Auto_order = false;
                        break;



                }//end swithc
            }
        });


        // SpinnerDay
        String[] Day= getResources().getStringArray(R.array.Days);
        ArrayAdapter adapterday = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Day);
        adapterday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDay.setAdapter(adapterday);

        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });

    }

    private void SaveAccountSetupInformation() {
        String username = UserName.getText().toString();
        String usermobile = mobile.getText().toString();
        String userAddress= Address.getText().toString();


        if(username.isEmpty())
        {
            Toast.makeText(this, "رجاءً أدخل أسم المستخدم", Toast.LENGTH_SHORT).show();
        }
        if(usermobile.isEmpty())
        {
            Toast.makeText(this, "رجاءً أدخل رقم الجوال", Toast.LENGTH_SHORT).show();
        }
        if(userAddress.isEmpty())
        {
            Toast.makeText(this, "رجاءً أدخل العنوان", Toast.LENGTH_SHORT).show();
        }
        if(!username.isEmpty() && !usermobile.isEmpty() && !userAddress.isEmpty()) {


            loadingBar.setTitle("حفظ المعلومات");
            loadingBar.setMessage("جاري حفظ ملعومات, فضلاً أنتظر ..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            Map userMap = new HashMap();
            userMap.put("Username", username);
            userMap.put("Email", currentUserEmail);
            userMap.put("Phone_number", usermobile);
            userMap.put("Address", userAddress);
            userMap.put("Auto_order", Auto_order);
            userMap.put("Order_time", Theday);


            UsersRef.setValue(userMap);
            Intent mainIntent = new Intent(Setup.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
            loadingBar.dismiss();
        }

    }//Save Account Setup Information

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          if(parent.getId() == R.id.Spinner_order_day){
              Theday =parent.getItemAtPosition(position).toString();
              Toast.makeText(Setup.this, "أسوم أخترت اليوم ا", Toast.LENGTH_SHORT).show();

          }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}