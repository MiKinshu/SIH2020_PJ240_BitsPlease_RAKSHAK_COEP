package com.mikinshu.rakshak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.models.ChirpError;

public class NoNetworkActivity extends AppCompatActivity {

    Button BTNemergencyMSG, BTNwalkietalkie;
    public static ChirpSDK chirp;
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private static final int REQUEST_COARSE_LOCATION = 3;

    public static String CHIRP_APP_KEY = "a425Bff1fE83BaFD9e5F6d5A3";
    public static String CHIRP_APP_SECRET = "A0DFeB41c736196Fa82fBFCcAa36E334610Ea71bd128e7e9D5";
    public static String CHIRP_APP_CONFIG = "GpEu5S5FtjVzSmhBNpDrmLU9Ojw4c2xcsrhoPuerCsqSNfoicEuPCx0iX1lNFMYN2ekd4+HAT1wXVvvgqaTrm7FXO3EKt6aDCEOYcZtc8oPSMNk83Q/UMFfdgimH5AYSbx9yFiuvoKAuhXA31VsiEfdYSLD82zXTgEjgTYyzje1BMRIEqXKV3fG6pT14vftbJ1gc3qJR1RW4+/g1bVqKo6zG7gHkC+qwzSrqQ1/63lA2wMQ8Cvu3mmMzvgFVWlsBUg++sxGaztNCX0F7Ig96oi7PGeVGZGj5nnicfJsL3RHH2siNwoILh9E6SkejXNGq5uq35juxz1ySslDGTOr2y0yvKxjfgC5JI2+01TLlXGPTY8q7cDpASP9rbSwHWoEu7HIxHgu/g1ZZTfo21HxAkjHcxg0Zj+25HkTCalQ/jbrB33yYEUUI+05l+dP0OU29SMeZ1G2xTmrzy2nerEzTOW9CECAu/X0Vy6Wk+qYScuW64uboqeQnSfer5qmDK44jNYuwAg9ZklpzkTKaIRD/2bpsBElAwwS5UvTI5u2uQ/obYopGHC6VB88Ird1Q41FGGnIfMYwmvRJfPpBa4TGvU8S/9NoNZF891m0FYvy0FoN2kxus+Xi6z7O2lvcEGON+aiiKenCC+xdAimpNEGVJyQH36AG6KsIz3iAJT+Q/lpBwbWerYf5s7SlNZhlkxeWQfb+X7p/SSNndfrxsuX5g3RhMKXbXvG3bQueQHzguaWi5ykqMWzioEQA/xGeRhPu1gYaMDXYmonLfX/WWEuEpEqhmSlNh/ePnqM85CMkXBPz0AbleD5Xe0nz+f/hO2iX09br1ymNpJ2PnhEwpAPffoGNNYrxceUxCYYEMImRMaBjskKxoGl5jxknvY9G5jn6kW16/91NApoeul75yRFdzqF5fYT68uxNTzlF9stX6ukvsDoWPr8EFWl0OY82pgUfRiGdQwyy/0f9k8SKD41Ptcyac0VVeiZcoLeuRtm3EJPk9bgMk7FpQmkTz/dOSXVCb4upARtDmGY1+v5nbsxNEocmC9aVlHWXyYKqzKjZzf15XPLMUGP5/HPLA+8bNs7G9MT+vWi5EhR/QMKaDykLPdQCKrDJvXdPxeKVriDIqRGim24EmheFz4lb2Q2apYkc4JCD4gnXKuvrTdhtBtHZx3A==";

    private FusedLocationProviderClient fusedLocationClient;
    public static String mLat, mLon;
    String TAG = "MyLOGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);
        getSupportActionBar().hide();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(NoNetworkActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: got location");
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mLat = location.getLatitude() + "";
                    mLon = location.getLongitude() + "";
                    Log.d(TAG, "onSuccess: Lat is " + mLat + "Long is " + mLon);
                }
                else Log.d(TAG, "onSuccess: The Location received is NULL");
            }
        });

        chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }

        BTNemergencyMSG=findViewById(R.id.BTNemergencyMSG);
        BTNwalkietalkie=findViewById(R.id.BTNwalkietalkie);
        BTNemergencyMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass cdd = new CustomDialogClass(NoNetworkActivity.this);
                cdd.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            ChirpError error = chirp.start(true, true);
            if (error.getCode() > 0) {
                Log.e("ChirpError: ", error.getMessage());
            } else {
                Log.v("ChirpSDK: ", "Started ChirpSDK");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ChirpError error = chirp.start();
                    if (error.getCode() > 0) {
                        Log.e("ChirpError: ", error.getMessage());
                    } else {
                        Log.v("ChirpSDK: ", "Started ChirpSDK");
                    }
                }
            }
        }
    }

    public void handleWalkieTalkie(View view){
        Intent intent = new Intent(NoNetworkActivity.this, com.mikinshu.rakshak.WalkiTalkie.class);
        startActivity(intent);
    }
}