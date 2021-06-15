package com.example.dontworry;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class AboutParent extends Fragment {
    LinearLayout linearLayoutMail, linearLayoutPhone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parent_about, container, false);
        linearLayoutMail = view.findViewById(R.id.contactMail);
        linearLayoutPhone = view.findViewById(R.id.contactPhone);

        linearLayoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:70082977"));
                startActivity(intent);
            }
        });

        linearLayoutMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:asy085@usal.edu.lb.com"));
                startActivity(intent);
            }
        });



        return view;
    }
}
