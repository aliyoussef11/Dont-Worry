package com.example.dontworry;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {
    DrawerLayout dl;
    TextView title;
    String username, username2, username3, username4, Final_Username;
    TextView fetchemail;
    private CircleImageView profileImage;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dl =findViewById(R.id.drawerLayout);
        loadFragment(new Home2());
        title = findViewById(R.id.title);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ParentUser");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView nav_view =(NavigationView)findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        fetchemail = headerView.findViewById(R.id.textemail);
        profileImage = headerView.findViewById(R.id.parent_image);

        //Get Current Username
        username = getIntent().getStringExtra("UsernameParentFromLogin");
        username2 = getIntent().getStringExtra("UsernameParentFromRegistration");
        username3 = getIntent().getStringExtra("UsernameFromAddLocationActivity");
        username4 = getIntent().getStringExtra("UsernameFromEditProfileToHome");

        if (TextUtils.isEmpty(username2) && TextUtils.isEmpty(username3) && TextUtils.isEmpty(username4)) {
           fetchemail.setText(username);
           Final_Username = username;
        }
        else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username3) && TextUtils.isEmpty(username4)){
            fetchemail.setText(username2);
            Final_Username = username2;
        }
        else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username2) && TextUtils.isEmpty(username4)){
            fetchemail.setText(username3);
            Final_Username = username3;
        }
        else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username2) && TextUtils.isEmpty(username3)){
            fetchemail.setText(username4);
            Final_Username = username4;
        }

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.home){
                    title.setText("Home Page");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new Home2());
                }
                if(id == R.id.checkChild){
                    title.setText("Check Child Page");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new CheckChild());
                }
                if(id == R.id.profile){
                    Intent i = new Intent(HomeActivity.this, EditProfile.class);
                    if (TextUtils.isEmpty(username2) && TextUtils.isEmpty(username3) && TextUtils.isEmpty(username4)) {
                        i.putExtra("UsernameFromHomeToProfile", username);
                    }
                    else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username3) && TextUtils.isEmpty(username4)){
                        i.putExtra("UsernameFromHomeToProfile", username2);
                    }
                    else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username2) && TextUtils.isEmpty(username4)){
                        i.putExtra("UsernameFromHomeToProfile", username3);
                    }
                    else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(username2) && TextUtils.isEmpty(username3)){
                        i.putExtra("UsernameFromHomeToProfile", username4);
                    }
                    startActivity(i);
                }
                if(id == R.id.changepassword){
                    title.setText("Change Your Password");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new ChangePassword());
                }
                else if(id == R.id.DeactivateYourAccount){
                    title.setText("Deactivation");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new DeactivateParentAccount());
                }
                else if(id == R.id.EditChild){
                    title.setText("Edit Child Username");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new EditChildUsername());
                }
                else if(id == R.id.about){
                    title.setText("About Us Page");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new AboutParent());
                }
                else if(id == R.id.rate){
                    title.setText("Rate Us Page");
                    dl.closeDrawer(GravityCompat.START);
                    loadFragment(new RateUsParent());
                }
                else if(id == R.id.logout){
                    new AlertDialog.Builder(HomeActivity.this)
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
                                    Intent i2 =new Intent(HomeActivity.this, Welcome.class);
                                    startActivity(i2);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dl.closeDrawer(GravityCompat.START);
                                    loadFragment(new Home2());
                                }
                            })
                            .show();
                }
                return true;
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return  super.onOptionsItemSelected(item);
    }

    public void OpenCloseDrawer(View v){
        dl =findViewById(R.id.drawerLayout);
        if(!dl.isDrawerOpen(GravityCompat.START)){
            dl.openDrawer(GravityCompat.START);
        }
        else{
            dl.closeDrawer(GravityCompat.END);
        }
    }

    private void MoveIntent(Activity activity, Class c){
        Intent intent =new Intent(activity, c);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment){
        //Creation fragment manager
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dl.closeDrawer(GravityCompat.START);
    }
}