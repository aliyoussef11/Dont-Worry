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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ParentRegistration2 extends AppCompatActivity {
    EditText UsernameChild;
    Button check, two, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_registration2);

        //Get From Intent
        final String usernameParent = getIntent().getStringExtra("ParentUsername");
        final String passwordParent = getIntent().getStringExtra("ParentPassword");

        previous = findViewById(R.id.Previous2);
        UsernameChild = findViewById(R.id.ChildCheck);
        check = findViewById(R.id.ParentRegister);
        two = findViewById(R.id.button2);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ParentRegistration2.this, ParentRegistration.class);
                startActivity(i);
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameOfChild = UsernameChild.getText().toString();

                if (usernameOfChild.isEmpty()) {
                    UsernameChild.setError("Please Enter Your Child Username");
                    UsernameChild.requestFocus();
                }
                else{
                    final ParentHelper parent = new ParentHelper(usernameParent, passwordParent, usernameOfChild);
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                    Query checkChild = reference.orderByChild("username").equalTo(usernameOfChild);
                    checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Parents");
                                reference2.child(usernameParent).setValue(parent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            SweetAlertDialog sweet = new SweetAlertDialog(ParentRegistration2.this, SweetAlertDialog.SUCCESS_TYPE);
                                            sweet.setTitleText("Thank You For Registration");
                                            sweet.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent i = new Intent(ParentRegistration2.this, HomeActivity.class);
                                                    i.putExtra("UsernameParentFromRegistration", usernameParent);
                                                    startActivity(i);
                                                }
                                            }, 2000);
                                        }
                                        else{
                                            SweetAlertDialog sweet = new SweetAlertDialog(ParentRegistration2.this, SweetAlertDialog.ERROR_TYPE);
                                            sweet.setTitleText("Something Went Wrong");
                                            sweet.show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ParentRegistration2.this, "This Username Is Not Found", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
        return;
    }
}