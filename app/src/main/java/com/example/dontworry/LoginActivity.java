package com.example.dontworry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignIn;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailId = findViewById(R.id.LoginEmail);
        password = findViewById(R.id.LoginPassword);
        btnSignIn = findViewById(R.id.SignIn);
        remember = findViewById(R.id.remember);

        final SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        final String checkBox = preferences.getString("remember", "");
        final String RecoveryUsername = preferences.getString("username", "");
        final String RecoveryPassword = preferences.getString("password", "");
        if (checkBox.equals("true")){
            final SweetAlertDialog sweet = new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.PROGRESS_TYPE);
            sweet.setTitleText("Checking ...");
            sweet.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sweet.dismiss();
                }
            }, 3000);

            final DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Parents");
            Query checkParent = reference3.orderByChild("username").equalTo(RecoveryUsername);

            checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String passwordParentFromDB = snapshot.child(RecoveryUsername).child("password").getValue(String.class);
                        if(passwordParentFromDB.equals(RecoveryPassword)){
                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            i.putExtra("UsernameParentFromLogin", RecoveryUsername);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        final DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Children");
                        Query checkChild = reference4.orderByChild("username").equalTo(RecoveryUsername);

                        checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String passwordFromDB = snapshot.child(RecoveryUsername).child("password").getValue(String.class);
                                    if(passwordFromDB.equals(RecoveryPassword)){
                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(LoginActivity.this, ChildHome.class);
                                        i.putExtra("ChildUsername", RecoveryUsername);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else if(checkBox.equals("false")){
            Toast.makeText(LoginActivity.this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();
                if(email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your Password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fields are Empty!", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Parents");
                    Query checkParent = reference2.orderByChild("username").equalTo(email);

                    checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String passwordParentFromDB = snapshot.child(email).child("password").getValue(String.class);
                                if(passwordParentFromDB.equals(pwd)){
                                    final SweetAlertDialog sweet = new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.SUCCESS_TYPE);
                                    sweet.setTitleText("Login Successfully");
                                    sweet.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sweet.dismiss();
                                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                            i.putExtra("UsernameParentFromLogin", email);
                                            startActivity(i);
                                        }
                                    }, 2000);
                                }
                                else{
                                    remember.setChecked(false);
                                    new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Login Error")
                                            .show();
                                }
                            }
                            else{
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                                Query checkChild = reference.orderByChild("username").equalTo(email);

                                checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String passwordFromDB = snapshot.child(email).child("password").getValue(String.class);
                                            if(passwordFromDB.equals(pwd)){
                                                final SweetAlertDialog sweet = new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.SUCCESS_TYPE);
                                                        sweet.setTitleText("Login Successfully");
                                                        sweet.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(LoginActivity.this, ChildHome.class);
                                                        i.putExtra("ChildUsername", email);
                                                        startActivity(i);
                                                    }
                                                }, 2000);
                                            }
                                            else{
                                                remember.setChecked(false);
                                                new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Login Error")
                                                        .show();
                                            }
                                        }
                                        else{
                                            remember.setChecked(false);
                                            new SweetAlertDialog(LoginActivity.this,  SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Login Error")
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preference = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("remember", "true");
                    editor.putString("username", emailId.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.apply();
                } else if(!compoundButton.isChecked()){
                    SharedPreferences preference = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }

            }
        });
    }

    public void Back(View view) {
        Intent intent= new Intent(LoginActivity.this, Welcome.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}