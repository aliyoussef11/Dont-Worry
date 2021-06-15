package com.example.dontworry;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditChildUsername extends Fragment {
    TextView username;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity;
    String Final_Username;
    Button EditUsername, Update;
    EditText NewUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editchild_username, container, false);

        username = view.findViewById(R.id.UsernameChild_In_Parent_Page);
        EditUsername = view.findViewById(R.id.UpdateUsername);

        //Fetch The Username Using Intents
        UsernameFromLogin = getActivity().getIntent().getStringExtra("UsernameParentFromLogin");
        UsernameFromRegistration = getActivity().getIntent().getStringExtra("UsernameParentFromRegistration");
        UsernameFromAddLocationActivity = getActivity().getIntent().getStringExtra("UsernameFromAddLocationActivity");

        //Take The Correct Username
        if (TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity)) {
            Final_Username = UsernameFromLogin;
        } else if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromAddLocationActivity)) {
            Final_Username = UsernameFromRegistration;
        } else if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration)) {
            Final_Username = UsernameFromAddLocationActivity;
        }

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parents");
        Query checkParent = reference.orderByChild("username").equalTo(Final_Username);

        checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final String ChildUsername = snapshot.child(Final_Username).child("childUsername").getValue(String.class);
                    username.setText(ChildUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog_edit_username = new Dialog(getActivity());
                dialog_edit_username.setContentView(R.layout.dialog_editusername_child);
                Update = dialog_edit_username.findViewById(R.id.Set_New_Child_Username);
                NewUsername = dialog_edit_username.findViewById(R.id.New_Name_Child);
                dialog_edit_username.show();

                Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String newUsername = NewUsername.getText().toString().trim();
                        final String CheckUsername = username.getText().toString().trim();

                        if (newUsername.isEmpty()) {
                            NewUsername.setError("Please Enter The New Username");
                            NewUsername.requestFocus();
                        }
                        else if(CheckUsername.equals(newUsername)) {
                         Toast.makeText(getActivity(), "This is Already Your Child !", Toast.LENGTH_LONG).show();
                        }
                        else {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                            Query checkChild = reference.orderByChild("username").equalTo(newUsername);
                            checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final DatabaseReference referenceParent = FirebaseDatabase.getInstance().getReference("Parents");
                                        referenceParent.child(Final_Username).child("childUsername").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Your Child Username Has Been Updated", Toast.LENGTH_LONG).show();
                                                    username.setText(newUsername);
                                                    dialog_edit_username.dismiss();
                                                } else {
                                                    Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                                                    dialog_edit_username.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "This Username IS Not Found", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }

}