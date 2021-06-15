package com.example.dontworry;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class ChangePassword extends Fragment {
    Button Change;
    EditText OldPassword, NewPassword, ConfirmNewPassword;
    String old_password, new_password, confirm_new_password;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity;
    String Final_Username;
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_change_password, container, false);

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

        //Referencing
        OldPassword = view.findViewById(R.id.OldPassword);
        NewPassword = view.findViewById(R.id.NewPassword);
        ConfirmNewPassword = view.findViewById(R.id.ConfirmNewPassword);
        Change = view.findViewById(R.id.Change);

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Take values from editText
                old_password = OldPassword.getText().toString().trim();
                new_password = NewPassword.getText().toString().trim();
                confirm_new_password = ConfirmNewPassword.getText().toString().trim();

                if (old_password.isEmpty()) {
                    OldPassword.setError("Please Enter Your Old Password");
                    OldPassword.requestFocus();
                } else if (new_password.isEmpty()) {
                    NewPassword.setError("Please Enter Your New Password");
                    NewPassword.requestFocus();
                }else if (confirm_new_password.isEmpty()) {
                    ConfirmNewPassword.setError("Please Enter Your Confirmation Password");
                    ConfirmNewPassword.requestFocus();
                }else if(!(new_password.equals(confirm_new_password))){
                    Toast.makeText(getActivity(), "Password or Confirm Password Not Match", Toast.LENGTH_LONG).show();
                }
                else if(!(isValidPassword(new_password))){
                    NewPassword.setError("Must Contain 1 Upper / 1 digit / 8 characters");
                    NewPassword.requestFocus();
                }
                else{
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parents");
                    Query checkParent = reference.orderByChild("username").equalTo(Final_Username);

                    checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String passwordParentFromDB = snapshot.child(Final_Username).child("password").getValue(String.class);
                                if (passwordParentFromDB.equals(old_password)) {
                                    if(old_password.equals(new_password)){
                                        Toast.makeText(getActivity(), "They Are Equals", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        reference.child(Final_Username).child("password").setValue(new_password);
                                        Toast.makeText(getActivity(), "Changed Successfully", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Wrong Old Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        return view;
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

}
