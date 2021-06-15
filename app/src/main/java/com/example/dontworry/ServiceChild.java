package com.example.dontworry;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServiceChild extends Service{
    final Handler handler = new Handler();
    Runnable mUpdateCurrentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String name = intent.getStringExtra("UsernameChild");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mUpdateCurrentLocation = new Runnable() {
            @Override
            public void run() {
                //Checking The Child Current Location
                //Check if We have the ability to Access Fine Location

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (isLocationEnabled(getApplicationContext())) {
                            //Get Location Here
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        final Double latitude = location.getLatitude();
                                        final Double longitude = location.getLongitude();

                                        LocationCondition condition = new LocationCondition(name, "Enabled");
                                        final DatabaseReference referenceCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                                        referenceCondition.child(name).setValue(condition)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            LocationHelper helper = new LocationHelper(name, longitude, latitude);
                                                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CurrentLocationOfChild");
                                                            reference.child(name).setValue(helper)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                //Toast.makeText(ServiceChild.this, latitude+"/"+longitude , Toast.LENGTH_SHORT).show();
                                                                                CreateNotification("Your Location Has Been Updated");
                                                                            } else {
                                                                                //Toast.makeText(ServiceChild.this, "Not Saved", Toast.LENGTH_SHORT).show();
                                                                                CreateNotification("Unable To Update Your Location");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ServiceChild.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                        else{
                            CreateNotification("Please Enable Your GPS");
                        }
                    }
                else {
                        CreateNotification("Please Enable Your Location Permission From App Info");
                    }
                }

                handler.postDelayed(this, 60000);
            }
        };

        mUpdateCurrentLocation.run();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("This App Still Running");
        //bigText.setBigContentTitle(name);
        bigText.setSummaryText("Welcome "+ name);

        mBuilder.setSmallIcon(R.drawable.check_child);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) ServiceChild.this.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        startForeground(1, mBuilder.build());

        return START_STICKY;
    }

    private void CreateNotification(String s) {

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ServiceChild.this, "notify_001");

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("New Notification About You");
        bigText.setBigContentTitle(s);
        bigText.setSummaryText("New Notification");

        mBuilder.setSmallIcon(R.drawable.check_child);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) ServiceChild.this.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mUpdateCurrentLocation);
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

}
