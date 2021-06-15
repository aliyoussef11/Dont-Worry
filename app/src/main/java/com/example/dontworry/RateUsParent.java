package com.example.dontworry;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RateUsParent extends Fragment{
    RatingBar rate_us;
    float MyRating = 0;
    Button Save;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity, Final_Username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_us_parent, container, false);

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

        rate_us = view.findViewById(R.id.Rate_Us_Parent);
        Save = view.findViewById(R.id.Save_Rating_Parent);

        rate_us.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rating = (int) v;
                String message = null;

                MyRating = ratingBar.getRating();

                switch(rating){
                    case 1:
                        message = "Sorry To Hear That! :(";
                        break;

                    case 2:
                        message = "You Always Accept suggestions!";
                        break;
                    case 3:
                        message ="Good Enough!";
                        break;
                    case 4:
                        message = "Great! Thank You";
                        break;
                    case 5:
                        message = "Awesome!";
                        break;
                }

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ChildHome.this, String.valueOf(MyRating) , Toast.LENGTH_LONG).show();
                String user = "Parent";
                final RatingHelper ratingHelper = new RatingHelper(MyRating , Final_Username, user);
                final DatabaseReference referenceRate = FirebaseDatabase.getInstance().getReference("Rating");
                referenceRate.child(Final_Username).setValue(ratingHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Thank You For Rating", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Sorry, Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}
