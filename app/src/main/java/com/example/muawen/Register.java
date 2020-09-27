package com.example.muawen;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private EditText Useremail, Userpassword, UserConfirmationPassword;
    private Button register_button;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private TextView HaveAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Useremail = (EditText) findViewById(R.id.register_email);
        Userpassword= (EditText) findViewById(R.id.register_password);
        UserConfirmationPassword = (EditText) findViewById(R.id.Confirmation_password);
        loadingBar = new ProgressDialog(this);
        register_button = (Button) findViewById(R.id.register_button);
        HaveAccount =findViewById(R.id.Have_account);
        mAuth = FirebaseAuth.getInstance();


        HaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Register.this, login.class);
                startActivity(i);            }
        });
        register_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }
    private void CreateNewAccount() {

        String email = Useremail.getText().toString();
        String password = Userpassword.getText().toString();
        String Confirm_password =  UserConfirmationPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "فضلًا ادخل البريد الالكتروني ...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "فضلًا ادخل كلمة المرور ...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Confirm_password)){
            Toast.makeText(this, "فضلًا اعد ادخال كلمة المرور ...",Toast.LENGTH_SHORT).show();
        }

        else if(!password.equals(Confirm_password)){
            Toast.makeText(this, "كلمة المرور غير متطابقة !",Toast.LENGTH_SHORT).show();
        }

        else{

            loadingBar.setTitle("انشاء الحساب");
            loadingBar.setMessage("جاري انشاء الحساب , فضلاً أنتظر  ...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){


                        SendUserToSetupActivity();
                        Toast.makeText(Register.this, "تم التسجيل بنجاح",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(Register.this, "لم يتم التسجيل !" + message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            });
        }

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(Register.this, Setup.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}