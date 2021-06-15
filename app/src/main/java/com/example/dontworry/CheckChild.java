package com.example.dontworry;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import static java.lang.System.currentTimeMillis;

public class CheckChild extends Fragment{
    Button checkChild, stopCheking;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity, UsernameFromEditProfile;
    String Final_Username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_child, container, false);

        //Referencing
        checkChild = view.findViewById(R.id.checkChild);
        stopCheking = view.findViewById(R.id.StopCheking);

        //Get Current Username Using Intent()
        UsernameFromLogin = getActivity().getIntent().getStringExtra("UsernameParentFromLogin");
        UsernameFromRegistration = getActivity().getIntent().getStringExtra("UsernameParentFromRegistration");
        UsernameFromAddLocationActivity = getActivity().getIntent().getStringExtra("UsernameFromAddLocationActivity");
        UsernameFromEditProfile = getActivity().getIntent().getStringExtra("UsernameFromEditProfileToHome");

        if (TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity) && TextUtils.isEmpty(UsernameFromEditProfile)) {
            Final_Username = UsernameFromLogin;
        }
        else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromAddLocationActivity) && TextUtils.isEmpty(UsernameFromEditProfile)){
            Final_Username = UsernameFromRegistration;
        }
        else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromEditProfile)){
            Final_Username = UsernameFromAddLocationActivity;
        }
        else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity)){
            Final_Username = UsernameFromEditProfile;
        }

        checkChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getActivity(), ServiceParent.class);
                serviceIntent.putExtra("Username", Final_Username);
                getActivity().startService(serviceIntent);
            }
        });

        stopCheking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getActivity(), ServiceParent.class);
                getActivity().stopService(serviceIntent);
            }
        });

        return view;
    }

}
