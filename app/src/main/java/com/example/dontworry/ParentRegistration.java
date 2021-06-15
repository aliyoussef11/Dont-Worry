package com.example.dontworry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentRegistration extends AppCompatActivity {
    EditText usernameParent, passwordParent, confirmPasswordParent;
    Button one;
    Button next, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_registration);

        back = findViewById(R.id.Previous);
        one = findViewById(R.id.button1);
        next = findViewById(R.id.Next);
        usernameParent = findViewById(R.id.UsernameParent);
        passwordParent = findViewById(R.id.PasswordParent);
        confirmPasswordParent = findViewById(R.id.ConfirmPasswordParent);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ParentRegistration.this, Welcome.class);
                startActivity(i);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameParent.getText().toString();
                final String password = passwordParent.getText().toString();
                final String confirmPassword = confirmPasswordParent.getText().toString();

                if (username.isEmpty()) {
                    usernameParent.setError("Please Enter Your Username");
                    usernameParent.requestFocus();
                } else if (password.isEmpty()) {
                    passwordParent.setError("Please Enter You Last Name");
                    passwordParent.requestFocus();
                }
                else if(confirmPassword.isEmpty()){
                    confirmPasswordParent.setError("Please Enter Confirmation");
                    confirmPasswordParent.requestFocus();
                }
                else if(!(isValidPassword(password))){
                    passwordParent.setError("Must Contain 1 Upper / 1 digit / 8 character");
                    passwordParent.requestFocus();
                }
                else if(!(isValidUsername(username))){
                    usernameParent.setError("Must Contain 6 character / 1 Upper");
                    usernameParent.requestFocus();
                }
                else if(!(password.equals(confirmPassword))){
                    Toast.makeText(ParentRegistration.this, "Password or Confirm Password Not Match", Toast.LENGTH_LONG).show();
                }
                else{
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parents");
                    Query checkChild = reference.orderByChild("username").equalTo(username);
                    checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(ParentRegistration.this, "This Username Is Already Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(ParentRegistration.this, ParentRegistration2.class);
                                i.putExtra("ParentUsername", username);
                                i.putExtra("ParentPassword", password);
                                startActivity(i);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
            });
    }
            }
            });
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidUsername(final String username) {

        Pattern pattern;
        Matcher matcher;

        final String Username_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(Username_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();

    }

    }
