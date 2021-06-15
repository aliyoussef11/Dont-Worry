package com.example.dontworry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChildRegistration extends AppCompatActivity {
    EditText emailId, password, confirmpassword, username;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_registration);

        username = findViewById(R.id.UserNameChild);
        emailId = findViewById(R.id.EmailChild);
        password = findViewById(R.id.PasswordChild);
        confirmpassword= findViewById(R.id.ConfirmPasswordChild);
        btnSignUp = findViewById(R.id.SignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = username.getText().toString();
                final String email = emailId.getText().toString();
                final String confpwd = confirmpassword.getText().toString();
                final String pwd = password.getText().toString();

                if (name.isEmpty()) {
                    username.setError("Please Enter A Username");
                    username.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else if (confpwd.isEmpty()) {
                    confirmpassword.setError("Please Enter Confirm Password");
                    confirmpassword.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty() && confpwd.isEmpty() && name.isEmpty()) {
                    Toast.makeText(ChildRegistration.this, "Fields are Empty!", Toast.LENGTH_LONG).show();
                } else if (!(pwd.equals(confpwd))) {
                    Toast.makeText(ChildRegistration.this, "Password Or Confirm Password Not Match!", Toast.LENGTH_LONG).show();
                } else if (!(isValidPassword(pwd))) {
                    password.setError("Must Contain 1 Upper / 1 digit / 8 character");
                    password.requestFocus();
                } else if (!(isValidEmail(email))) {
                    emailId.setError("Must Contain 8 character / @");
                    emailId.requestFocus();
                } else if (!(isValidUsername(name))) {
                    username.setError("Must Contain 1 Upper / 6 character");
                    username.requestFocus();
                }
                else{
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                Query checkChild = reference.orderByChild("username").equalTo(name);
                checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(ChildRegistration.this, "This Username Is Already Exist", Toast.LENGTH_SHORT).show();
                        } else {
                            ChildHelper child = new ChildHelper(name, email, pwd);
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                            reference.child(name).setValue(child).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(ChildRegistration.this, "Thank You For Registration", Toast.LENGTH_SHORT).show();
                                                SweetAlertDialog sweet = new SweetAlertDialog(ChildRegistration.this, SweetAlertDialog.SUCCESS_TYPE);
                                                sweet.setTitleText("Thank You For Registration");
                                                sweet.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(ChildRegistration.this, ChildHome.class);
                                                        i.putExtra("Childusername", name);
                                                        startActivity(i);
                                                    }
                                                }, 2000);
                                            } else {
                                                Toast.makeText(ChildRegistration.this, "Failed Registration", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }}
        });
    }

    public void Back(View view) {
        Intent intent = new Intent(ChildRegistration.this, Welcome.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^(?=.*[a-z])(?=.*[@])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }

    public boolean isValidUsername(final String username) {

        Pattern pattern;
        Matcher matcher;

        final String USERNAME_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();

    }

    }
