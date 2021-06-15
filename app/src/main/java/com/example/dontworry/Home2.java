package com.example.dontworry;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Home2 extends Fragment{
    Button addLocation;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity, UsernameFromEditProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.home2, container, false);

        addLocation = view.findViewById(R.id.AddLocationButton);

        //Get Current Username Using Intent()
        UsernameFromLogin = getActivity().getIntent().getStringExtra("UsernameParentFromLogin");
        UsernameFromRegistration = getActivity().getIntent().getStringExtra("UsernameParentFromRegistration");
        UsernameFromAddLocationActivity = getActivity().getIntent().getStringExtra("UsernameFromAddLocationActivity");
        UsernameFromEditProfile = getActivity().getIntent().getStringExtra("UsernameFromEditProfileToHome");

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddLocation.class);
                if (TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity) && TextUtils.isEmpty(UsernameFromEditProfile)) {
                    i.putExtra("UserNameFromHome", UsernameFromLogin);
                }
                else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromAddLocationActivity) && TextUtils.isEmpty(UsernameFromEditProfile)){
                    i.putExtra("UserNameFromHome", UsernameFromRegistration);
                }
                else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromEditProfile)){
                    i.putExtra("UserNameFromHome", UsernameFromAddLocationActivity);
                }
                else  if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity)){
                    i.putExtra("UserNameFromHome", UsernameFromEditProfile);
                }
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        return view;
    }



}
