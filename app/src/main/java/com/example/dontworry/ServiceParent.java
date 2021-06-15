package com.example.dontworry;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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


public class ServiceParent extends Service{
    final String Condition_Enabled = "Enabled";
    final String Condition_Disabled = "Disabled";
    final Handler handler = new Handler();
    Runnable mCheck;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String name = intent.getStringExtra("Username");

        mCheck = new Runnable() {
            @Override
            public void run() {
                //Checking The Child

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parents");
                Query checkParent = reference.orderByChild("username").equalTo(name);

                checkParent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String ChildUsername = snapshot.child(name).child("childUsername").getValue(String.class);

                            final DatabaseReference referenceCondition = FirebaseDatabase.getInstance().getReference("ChildLocationCondition");
                            Query checkChild = referenceCondition.orderByChild("username").equalTo(ChildUsername);

                            checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String condition = snapshot.child(ChildUsername).child("condition").getValue(String.class);
                                        if(condition.equals(Condition_Enabled)){
                                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CurrentLocationOfChild");
                                            Query checkChildLocation = reference.orderByChild("username").equalTo(ChildUsername);
                                            checkChildLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists())
                                                    {
                                                        //Get Longitude For Child
                                                        final double longitudeChild = snapshot.child(ChildUsername).child("longitude").getValue(Double.class);

                                                        //Get Latitude For Child
                                                        final double latitudeChild = snapshot.child(ChildUsername).child("latitude").getValue(Double.class);


                                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Location");
                                                        Query checkParentInsertedLocation = reference.orderByChild("username").equalTo(name);
                                                        checkParentInsertedLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists())
                                                                {
                                                                    //Get Longitude From Parent
                                                                    double longitudeParent = snapshot.child(name).child("longitude").getValue(Double.class);

                                                                    //Get Latitude From Parent
                                                                    double latitudeParent = snapshot.child(name).child("latitude").getValue(Double.class);

                                                                    if (distance(latitudeChild, longitudeChild, latitudeParent, longitudeParent) < 0.04) {
                                                                        // if distance < 0.04 miles = 64 meters we take locations as equal
                                                                        //do what you want to do...
                                                                        CreateNotification("Your Child Is Still In the Same Location");
                                                                    }
                                                                    else{
                                                                        CreateNotification("Your Child left The Location Entered");
                                                                    }
                                                                }else{
                                                                    //Toast.makeText(ServiceParent.this, "You Didn't Add Any Location Yet", Toast.LENGTH_LONG).show();
                                                                    CreateNotification("You Didn't Add Any Location Yet");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                        else if(condition.equals(Condition_Disabled)){
                                            //Toast.makeText(ServiceParent.this, "Your Child Disable His Location", Toast.LENGTH_LONG).show();
                                            CreateNotification("Your Child Stopped Detecting His Location");
                                        }
                                    }
                                    else{
                                         //Toast.makeText(ServiceParent.this, "Your Child Does Not Exist", Toast.LENGTH_LONG).show();
                                        CreateNotification("Your Child Does Not Start Detecting His Location Yet");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(ServiceParent.this, "Your Child Is Not Registered", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                handler.postDelayed(this, 60000);
            }
        };

        mCheck.run();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        //Intent ii = new Intent(getActivity(), HomeActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("This App Still Running");
        //bigText.setBigContentTitle(name);
        bigText.setSummaryText("Welcome "+ name);

        //mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.check_child);
        //mBuilder.setContentTitle("Your Title");
        //mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) ServiceParent.this.getSystemService(Context.NOTIFICATION_SERVICE);

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
                new NotificationCompat.Builder(ServiceParent.this, "notify_001");
        //Intent ii = new Intent(getActivity(), HomeActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("New Notification About Your Child");
        bigText.setBigContentTitle(s);
        bigText.setSummaryText("New Notification");

        //mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.check_child);
        //mBuilder.setContentTitle("Your Title");
        //mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) ServiceParent.this.getSystemService(Context.NOTIFICATION_SERVICE);

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

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mCheck);
    }
}
