package com.mikinshu.rakshak;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;
import java.util.Objects;

import static com.mikinshu.rakshak.NoNetworkActivity.mLat;
import static com.mikinshu.rakshak.NoNetworkActivity.mLon;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String TAG = "MyLOGS";
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Toast.makeText(FirebaseMessagingService.this, remoteMessage.getData().toString(), Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Log.w("Exception", e.toString());
        }
        Log.d(TAG, "onMessageReceived: Recieved"+remoteMessage.getData());
        String type = remoteMessage.getData().get("status");

        if(type.equals("0")) {

            String emergencyType, title, message;
            emergencyType = remoteMessage.getData().get("type");
            assert emergencyType != null;
            if (emergencyType.equalsIgnoreCase("general")) {
                title = "General Emergency";
                message = getString(R.string.general_message);
            } else if (emergencyType.equalsIgnoreCase("fire")) {
                title = "Fire Alert";
                message = getString(R.string.fire_message);
            } else if (emergencyType.equalsIgnoreCase("medical")) {
                title = "Medical Help";
                message = getString(R.string.medical_message);
            } else {
                title = "Disaster Alert";
                message = getString(R.string.disaster_message);
            }
            String lat = "-1", lon = "-1";
            try {
                String[] location = Objects.requireNonNull(remoteMessage.getData().get("loc")).split(" ", 2);
                Log.d("location", location[0] + " " + location[1]);
                lat = location[0];
                lon = location[1];
            } catch (Exception e) {
                Log.w("Exception caught in calculating distance from emergency ", e.toString());
            }
            showNotification(FirebaseMessagingService.this, title, message, lat, lon, 0);
        }
        else if(type.equals("2"))
        {
            String name = "Abhishek1";
            name = remoteMessage.getData().get("name");
            String body = "Emergency has been assigned to : " + name;
            showNotification(FirebaseMessagingService.this, "Assigned!", body, "0", "0", 2);
        }
        super.onMessageReceived(remoteMessage);
    }

    public void showNotification(Context context, String title, String body,String lat,String lon, int type) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = (int)System.currentTimeMillis();;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if(type == 0) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
            Log.d("URI", "" + gmmIntentUri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body);
            mBuilder.setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(mapIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body);
            mBuilder.setAutoCancel(true);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
}
