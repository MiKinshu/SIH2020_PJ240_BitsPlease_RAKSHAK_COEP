package com.mikinshu.rakshak;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.interfaces.ChirpEventListener;
import io.chirp.chirpsdk.models.ChirpError;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mikinshu.rakshak.NoNetworkActivity.chirp;

public class Listener extends Service {
    String TAG = "MyLogs";
    Set<String> Received = new HashSet<>();
    public static Map<String,String> PhDirectory = new HashMap<>();

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    public Listener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        PhDirectory.put("M","Medical");
        PhDirectory.put("F","Fire");
        PhDirectory.put("D","Disaster");
        PhDirectory.put("G","General Emergency");

        ChirpError error = chirp.start(true, true);
        if (error.getCode() > 0) {
            Log.e("ChirpError: ", error.getMessage());
        } else {
            Log.v("ChirpSDK: ", "Started ChirpSDK");
        }

        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }

        ChirpEventListener chirpEventListener = new ChirpEventListener() {
            @Override
            public void onReceived(byte[] data, int channel) {
                if (data != null) {
                    String identifier = new String(data);
                    Toast.makeText(Listener.this, "Received Message", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onReceived: identifier is "+ identifier);
                    Log.v(TAG, "Received " + identifier);
                    if(!isNetworkAvailable()) {
                        //sending w/o signal
                        Log.d(TAG, "onReceived: identifier is "+ identifier);
                        Toast.makeText(Listener.this, "Received Message", Toast.LENGTH_SHORT).show();
                        if(Received.contains(identifier)) {
                            Toast.makeText(Listener.this, "Repeated Receive, not forwarding", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Listener.this, "Network Unavailable, forwarding Audio. " + identifier, Toast.LENGTH_SHORT).show();
                            byte[] payload = identifier.getBytes(Charset.forName("UTF-8"));
                            ChirpError error = chirp.send(payload);
                            if (error.getCode() > 0) {
                                Log.e("ChirpError: ", error.getMessage());
                            } else {
                                Log.v("ChirpSDK: ", "Sent " + identifier);
                            }
                        }
                        Received.add(identifier);
                    }
                    else{
                        Toast.makeText(Listener.this, "Network Available, sending to Server.", Toast.LENGTH_SHORT).show();
                        String type = PhDirectory.get(identifier.charAt(0)+"");
                        String mlat = 25.423 + identifier.substring(1,3);
                        String mLong = 81.774 + identifier.substring(4,6);
                        makeCall(type, mlat, mLong); //This has to be changed once the server is running.
                    }
                } else {
                    Log.e("ChirpError: ", "Decode failed");
                }
            }
            public void onSending(byte[] payload, int channel) {}
            public void onSent(byte[] payload, int channel) {}
            public void onReceiving(int channel) {}
            public void onStateChanged(int oldState, int newState) {}
            @Override
            public void onSystemVolumeChanged(float old, float current) {}
        };
        chirp.setListener(chirpEventListener);
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, NoNetworkActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        initChannels(getApplicationContext());
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Listening in background")
                .setContentIntent(pendingIntent)
                .build());
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID,
                "Background channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void makeCall(String type, String lat, String lon) {
        String url = getResources().getString(R.string.server) + "requests";
        RequestBody body = new FormBody.Builder()
                .add("uid", "dum_UID") //This has to be checked.
                .add("loc", lat + " " + lon)
                .add("type", type)
                .add("msg","")
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }
}
