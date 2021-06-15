package com.example.dontworry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLocation extends AppCompatActivity implements OnMapReadyCallback{
    GoogleMap gMap;
    Button save;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        save = findViewById(R.id.Save);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                //Set marker position
                markerOptions.position(latLng);
                //Set latitude and Longitude On marker
                markerOptions.title(latLng.longitude+ " : "+latLng.latitude);

                //Take Longitude and Latitude Values To a specific Username
                final String usernameparent = getIntent().getStringExtra("UserNameFromHome");
                final LocationHelper helper = new LocationHelper(
                        usernameparent, latLng.longitude, latLng.latitude
                );

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username2 = getIntent().getStringExtra("UserNameFromHome");
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Location");
                        reference.child(username2).setValue(helper)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(AddLocation.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AddLocation.this, HomeActivity.class);
                                            i.putExtra("UsernameFromAddLocationActivity", username2);
                                            startActivity(i);
                                        }
                                        else{
                                            Toast.makeText(AddLocation.this, "Not Saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                //Clear the previous click position
                gMap.clear();
                //Zoom the marker
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                //Add marker on Map
                gMap.addMarker(markerOptions);

            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void BackToHome(View view){
        String username = getIntent().getStringExtra("UserNameFromHome");
        Intent i = new Intent(AddLocation.this, HomeActivity.class);
        i.putExtra("UsernameFromAddLocationActivity", username);
        startActivity(i);
    }

}
