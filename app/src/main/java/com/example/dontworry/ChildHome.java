package com.example.dontworry;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChildHome extends AppCompatActivity {
    //Allowed Character For Random Text
    private static final String ALLOWED_CHARACTERS ="AZNKGTOPQHDS";
    RatingBar rate_us;
    float MyRating = 0;

    private StorageReference storageProfilePicsRef;
    GridLayout gridLayout;
    TextView Child_Name, RandomText;
    Button Detect, StopDetect;
    EditText old_password_child, new_password_child, confirm_password;
    EditText Confirmation, UsernameChild;
    String oldPassword, newPassword, confirmNewPassword;
    Button Change, Deactivate, Save_Rating;
    private CircleImageView profileImage;
    private DatabaseReference databaseReference;
    String Final_Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_home);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ChildUser");
        profileImage = findViewById(R.id.profile_image_child);

        final String username_child = getIntent().getStringExtra("ChildUsername");
        final String username_child_from_registration = getIntent().getStringExtra("Childusername");
        final String username_child_from_editProfile = getIntent().getStringExtra("UsernameFromEditProfileToHome");

        Child_Name = findViewById(R.id.child_name);
        if(TextUtils.isEmpty(username_child) && TextUtils.isEmpty(username_child_from_editProfile)){
            Child_Name.setText(username_child_from_registration);
            Final_Username = username_child_from_registration;
        }
        else if(TextUtils.isEmpty(username_child_from_registration) && TextUtils.isEmpty(username_child_from_editProfile)){
            Child_Name.setText(username_child);
            Final_Username = username_child;
        }
        else if(TextUtils.isEmpty(username_child_from_registration) && TextUtils.isEmpty(username_child)){
            Child_Name.setText(username_child_from_editProfile);
            Final_Username = username_child_from_editProfile;
        }

        gridLayout=(GridLayout)findViewById(R.id.mainGrid);
        setSingleEvent(gridLayout, username_child, username_child_from_registration, username_child_from_editProfile);

        getUserInfo();
    }

    private void getUserInfo() {
        databaseReference.child(Final_Username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0)
                {
                    if(snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSingleEvent(GridLayout gridLayout, final String username1, final String username2, final String username3) {

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            CardView cardView = (CardView) gridLayout.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalI == 5) {
                        new AlertDialog.Builder(ChildHome.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences preference = getSharedPreferences("checkbox", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preference.edit();
                                        editor.putString("remember", "false");
                                        editor.apply();

                                        finish();
                                        Intent i2 = new Intent(ChildHome.this, Welcome.class);
                                        startActivity(i2);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    } else if (finalI == 0) {
                        final Dialog dialog_detect_location = new Dialog(ChildHome.this);
                        dialog_detect_location.setContentView(R.layout.locationchild_dialog);
                        Detect = dialog_detect_location.findViewById(R.id.DetectChildLocation);
                        StopDetect = dialog_detect_location.findViewById(R.id.StopDetectChildLocation);
                        dialog_detect_location.show();

                        Detect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        if(isLocationEnabled(getApplicationContext())){
                                            Intent intentService = new Intent(ChildHome.this, ServiceChild.class);
                                            intentService.putExtra("UsernameChild", Final_Username);
                                            startService(intentService);
                                            dialog_detect_location.dismiss();
                                        }
                                        else{
                                            SendPermission(getApplicationContext());
                                        }
                                    } else {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                    }
                                }

                            }
                        });

                        StopDetect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intentService = new Intent(ChildHome.this, ServiceChild.class);
                                stopService(intentService);
                                if (TextUtils.isEmpty(username1) && (TextUtils.isEmpty(username3))) {
                                    LocationCondition condition = new LocationCondition(username2, "Disabled");
                                    final DatabaseReference referenceCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                                    referenceCondition.child(username2).setValue(condition)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChildHome.this, "Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Not Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    }
                                                }
                                            });
                                } else if (TextUtils.isEmpty(username2) && (TextUtils.isEmpty(username3))) {
                                    LocationCondition condition = new LocationCondition(username1, "Disabled");
                                    final DatabaseReference referenceCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                                    referenceCondition.child(username1).setValue(condition)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChildHome.this, "Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Not Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    }
                                                }
                                            });
                                } else if (TextUtils.isEmpty(username1) && (TextUtils.isEmpty(username2))) {
                                    LocationCondition condition = new LocationCondition(username3, "Disabled");
                                    final DatabaseReference referenceCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                                    referenceCondition.child(username3).setValue(condition)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChildHome.this, "Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Not Stopped", Toast.LENGTH_SHORT).show();
                                                        dialog_detect_location.dismiss();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                    } else if (finalI == 2) {
                        final Dialog dialog_change_password = new Dialog(ChildHome.this);
                        dialog_change_password.setContentView(R.layout.change_password_dialogchild);
                        dialog_change_password.show();

                        old_password_child = dialog_change_password.findViewById(R.id.OldPasswordChild);
                        new_password_child = dialog_change_password.findViewById(R.id.NewPasswordChild);
                        confirm_password = dialog_change_password.findViewById(R.id.ConfirmNewPasswordChild);
                        Change = dialog_change_password.findViewById(R.id.ChangePasswordChild);

                        Change.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Take values from editText
                                oldPassword = old_password_child.getText().toString().trim();
                                newPassword = new_password_child.getText().toString().trim();
                                confirmNewPassword = confirm_password.getText().toString().trim();

                                if (oldPassword.isEmpty()) {
                                    old_password_child.setError("Please Enter Your Old Password");
                                    old_password_child.requestFocus();
                                } else if (newPassword.isEmpty()) {
                                    new_password_child.setError("Please Enter Your New Password");
                                    new_password_child.requestFocus();
                                } else if (confirmNewPassword.isEmpty()) {
                                    confirm_password.setError("Please Enter Your Confirmation Password");
                                    confirm_password.requestFocus();
                                } else if (!(newPassword.equals(confirmNewPassword))) {
                                    Toast.makeText(ChildHome.this, "Password or Confirm Password Not Match", Toast.LENGTH_LONG).show();
                                } else if (!(isValidPassword(newPassword))) {
                                    new_password_child.setError("Must Contain 1 Upper / 1 digit / 8 characters");
                                    new_password_child.requestFocus();
                                } else {
                                    if (TextUtils.isEmpty(username1) && (TextUtils.isEmpty(username3))) {
                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                                        Query checkParent = reference.orderByChild("username").equalTo(username2);

                                        checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String passwordParentFromDB = snapshot.child(username2).child("password").getValue(String.class);
                                                    if (passwordParentFromDB.equals(oldPassword)) {
                                                        if (oldPassword.equals(newPassword)) {
                                                            Toast.makeText(ChildHome.this, "They Are Equals", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            reference.child(username2).child("password").setValue(newPassword);
                                                            Toast.makeText(ChildHome.this, "Changed Successfully", Toast.LENGTH_LONG).show();
                                                            dialog_change_password.dismiss();
                                                        }
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Wrong Old Password", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else if (TextUtils.isEmpty(username2) && (TextUtils.isEmpty(username3))) {
                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                                        Query checkParent = reference.orderByChild("username").equalTo(username1);

                                        checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String passwordParentFromDB = snapshot.child(username1).child("password").getValue(String.class);
                                                    if (passwordParentFromDB.equals(oldPassword)) {
                                                        if (oldPassword.equals(newPassword)) {
                                                            Toast.makeText(ChildHome.this, "They Are Equals", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            reference.child(username1).child("password").setValue(newPassword);
                                                            Toast.makeText(ChildHome.this, "Changed Successfully", Toast.LENGTH_LONG).show();
                                                            dialog_change_password.dismiss();
                                                        }
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Wrong Old Password", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else if (TextUtils.isEmpty(username1) && (TextUtils.isEmpty(username2))) {
                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Children");
                                        Query checkParent = reference.orderByChild("username").equalTo(username3);

                                        checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String passwordParentFromDB = snapshot.child(username3).child("password").getValue(String.class);
                                                    if (passwordParentFromDB.equals(oldPassword)) {
                                                        if (oldPassword.equals(newPassword)) {
                                                            Toast.makeText(ChildHome.this, "They Are Equals", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            reference.child(username3).child("password").setValue(newPassword);
                                                            Toast.makeText(ChildHome.this, "Changed Successfully", Toast.LENGTH_LONG).show();
                                                            dialog_change_password.dismiss();
                                                        }
                                                    } else {
                                                        Toast.makeText(ChildHome.this, "Wrong Old Password", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                            }
                        });
                    } else if (finalI == 1) {
                        Intent i = new Intent(ChildHome.this, EditChildProfile.class);
                        if (TextUtils.isEmpty(username1) && TextUtils.isEmpty(username3)) {
                            i.putExtra("UsernameFromHome", username2);
                        } else if (TextUtils.isEmpty(username2) && TextUtils.isEmpty(username3)) {
                            i.putExtra("UsernameFromHome", username1);
                        } else if (TextUtils.isEmpty(username2) && TextUtils.isEmpty(username1)) {
                            i.putExtra("UsernameFromHome", username3);
                        }
                        startActivity(i);
                    } else if (finalI == 3) {
                        //Generate a random Text
                        final Random random = new Random();
                        final StringBuilder sb = new StringBuilder(6);
                        for (int i = 0; i < 6; ++i) {
                            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
                        }

                        final Dialog dialog_deactivate_account = new Dialog(ChildHome.this);
                        dialog_deactivate_account.setContentView(R.layout.deactivate_childaccount);
                        Confirmation = dialog_deactivate_account.findViewById(R.id.ConfirmDeactivationChild);
                        UsernameChild = dialog_deactivate_account.findViewById(R.id.ConfirmUsernameDeactivationChild);
                        RandomText = dialog_deactivate_account.findViewById(R.id.randomTextChild);
                        Deactivate = dialog_deactivate_account.findViewById(R.id.deactivateChildAccount);

                        RandomText.setText(sb);

                        dialog_deactivate_account.show();

                        Deactivate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Take Values From Edit Text
                                String confirmation = Confirmation.getText().toString().trim();
                                String username = UsernameChild.getText().toString().trim();

                                if (confirmation.isEmpty()) {
                                    Confirmation.setError("Please Fill This Text");
                                    Confirmation.requestFocus();
                                } else if (username.isEmpty()) {
                                    UsernameChild.setError("Please Fill Your Username");
                                    UsernameChild.requestFocus();
                                } else if (!(confirmation.equals(sb.toString()))) {
                                    Toast.makeText(ChildHome.this, "Confirmation Error, Try Again", Toast.LENGTH_LONG).show();
                                } else if (!(username.equals(Final_Username))) {
                                    Toast.makeText(ChildHome.this, "Username Error, Try Again", Toast.LENGTH_LONG).show();
                                } else {
                                    //Toast.makeText(getActivity(), "Matching", Toast.LENGTH_LONG).show();
                                    final DatabaseReference referenceChild = FirebaseDatabase.getInstance().getReference("Children");
                                    final DatabaseReference referenceLocationChild = FirebaseDatabase.getInstance().getReference("CurrentLocationOfChild");
                                    final DatabaseReference ChildUser = FirebaseDatabase.getInstance().getReference("ChildUser");
                                    final DatabaseReference ChildCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                                    storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic Child");

                                    referenceChild.child(Final_Username).removeValue();
                                    referenceLocationChild.child(Final_Username).removeValue();
                                    ChildUser.child(Final_Username).removeValue();
                                    storageProfilePicsRef.child(Final_Username + ".jpg").delete();
                                    ChildCondition.child(Final_Username).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                                                final SweetAlertDialog sweet = new SweetAlertDialog(ChildHome.this, SweetAlertDialog.SUCCESS_TYPE);
                                                sweet.setTitleText("Your Account Has Been Deactivated");
                                                sweet.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sweet.dismiss();
                                                        Intent i = new Intent(ChildHome.this, Welcome.class);
                                                        startActivity(i);
                                                    }
                                                }, 2000);
                                            } else {
                                                Toast.makeText(ChildHome.this, "Error When Deleting", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }

                        });
                    }

                    else if(finalI == 4){
                        final Dialog dialog_rate_us= new Dialog(ChildHome.this);
                        dialog_rate_us.setContentView(R.layout.rate_child_page);

                        //Referecing
                        rate_us = dialog_rate_us.findViewById(R.id.Rate_Us);
                        Save_Rating = dialog_rate_us.findViewById(R.id.Save_Rating);
                        dialog_rate_us.show();

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

                                Toast.makeText(ChildHome.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                        Save_Rating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(ChildHome.this, String.valueOf(MyRating) , Toast.LENGTH_LONG).show();
                                String user = "Child";
                                final RatingHelper ratingHelper = new RatingHelper(MyRating , Final_Username, user);
                                final DatabaseReference referenceRate = FirebaseDatabase.getInstance().getReference("Rating");
                                referenceRate.child(Final_Username).setValue(ratingHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ChildHome.this, "Thank You For Rating", Toast.LENGTH_SHORT).show();
                                            dialog_rate_us.dismiss();
                                        }
                                        else{
                                            Toast.makeText(ChildHome.this, "Sorry, Something Went Wrong", Toast.LENGTH_SHORT).show();
                                            dialog_rate_us.dismiss();
                                        }
                                    }
                                });
                            }
                        });

                    }

                }
            });
        }
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public void SendPermission(Context context){
        new AlertDialog.Builder(ChildHome.this)
                .setTitle("GPS Not Found")
                .setMessage("Do You Want Enable GPS?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return ;
                    }
                })
                .show();
    }

}