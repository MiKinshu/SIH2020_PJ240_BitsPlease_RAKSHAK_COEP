package com.mikinshu.rakshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

import static com.mikinshu.rakshak.NoNetworkActivity.CHIRP_APP_CONFIG;
import static com.mikinshu.rakshak.NoNetworkActivity.CHIRP_APP_KEY;
import static com.mikinshu.rakshak.NoNetworkActivity.CHIRP_APP_SECRET;
import static com.mikinshu.rakshak.NoNetworkActivity.chirp;

public class MainActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //User
    public static String mUid, mToken, mUsername;
    Boolean FirstTime = true;

    //runtime manipulation
    private static final int RC_SIGN_IN = 1;
    private static final int RC_USER_PREF_ACT = 24;
    private static final int PERMISSION_ALL = 1;

    //Network-Available Features
    Spinner spinner;
    OkHttpClient client;
    Button BTNaskhelp, BTNInNetCommunity, BTNInNetPredictor;
    String Emergency = "General Emergency";
    static String mLat, mLon;
    private FusedLocationProviderClient fusedLocationClient;
    String organisation;

    //Development Manipulation
    String TAG = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: In OnCreate");
        getSupportActionBar().hide();
        getUserPreference();
    }

    private void getUserPreference() {
        SharedPreferences faveditor = getSharedPreferences("com.mikinshu.rakshak.ref", MODE_PRIVATE);
        FirstTime = faveditor.getBoolean("FT", true);
        if (FirstTime) {
            Log.d(TAG, "getUserPreference: Going to ask user to sign in");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.ic_namelogoblue)//Experiment with this.
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        } else {
            SetupApplication();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //User Signed-In
                Log.d(TAG, "onActivityResult: User logged in");
                mFirebaseAuth = FirebaseAuth.getInstance();
//                FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
//                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) { //user signing for the first time.
                    //open user orientation activity.
                    Log.d(TAG, "onActivityResult: user logged in for the first time");
                    Intent intent = new Intent(MainActivity.this, com.mikinshu.rakshak.UserOrientationActivity.class);
                    startActivityForResult(intent, RC_USER_PREF_ACT);
//                } else {
//                    Log.d(TAG, "onActivityResult: Existing user");
//                    MarkFirstTimeFalse();
//                    SetupApplication();
//                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cannot work, until you sign in.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_USER_PREF_ACT) {
            if (resultCode == RESULT_OK) {
                String Name, MedicalCondition, EmergencyContact1, EmergencyContact2, DOB, AADHAAR;
                Name = data.getStringExtra("Name");
                MedicalCondition = data.getStringExtra("MedicalCondition");
                EmergencyContact1 = data.getStringExtra("EmergencyContact1");
                EmergencyContact2 = data.getStringExtra("EmergencyContact2");
                AADHAAR = data.getStringExtra("AADHAAR");
                DOB = data.getStringExtra("DOB");
                Log.d(TAG, "onActivityResult: Name : " + Name);
                Log.d(TAG, "onActivityResult: MedicalCondition : " + MedicalCondition);
                Log.d(TAG, "onActivityResult: EmergencyContact1 : " + EmergencyContact1);
                Log.d(TAG, "onActivityResult: EmergencyContact2 : " + EmergencyContact2);
                Log.d(TAG, "onActivityResult: DOB : " + DOB);
                PutUserDataToFirebase(Name, MedicalCondition, EmergencyContact1, EmergencyContact2, DOB, AADHAAR);
                MarkFirstTimeFalse();
                SetupApplication();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
                Toast.makeText(this, "Cannot work, until you provide info.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Send user data to Firebase.
    void PutUserDataToFirebase(String Name, String MedicalCondition, String EmergencyContact1, String EmergencyContact2, String DOB, String AADHAAR) {
        Log.d(TAG, "PutUserDataToFirebase: Called");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
            saveUID(mUid);
            AddTokenToFirebase();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mUsersDatabaseReference = firebaseDatabase.getReference().child("Users").child(mUid);
            Log.d(TAG, "PutUserDataToFirebase: User ID : " + mUid);
            mUsersDatabaseReference.child("Name").setValue(Name);
            mUsersDatabaseReference.child("MedicalCondition").setValue(MedicalCondition);
            mUsersDatabaseReference.child("EmergencyContact1").setValue(EmergencyContact1);
            mUsersDatabaseReference.child("EmergencyContact2").setValue(EmergencyContact2);
            mUsersDatabaseReference.child("DOB").setValue(DOB);
            mUsersDatabaseReference.child("NetworkID").setValue("IIITA");
            mUsersDatabaseReference.child("AADHAAR").setValue(AADHAAR);
        } else Log.d(TAG, "PutUserDataToFirebase: FireBase Error - Cannot find logged in user.");
    }

    void AddTokenToFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        mToken = task.getResult().getToken();
                        Log.d(TAG, "onComplete: MToken : " + mToken);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference mUsersDatabaseReference = firebaseDatabase.getReference().child("Users").child(mUid);
                        mUsersDatabaseReference.child("Token").setValue(mToken);
                    }
                });
    }

    //Making bool FirstTime false.
    void MarkFirstTimeFalse() {
        SharedPreferences.Editor faveditor = getSharedPreferences("com.mikinshu.rakshak.ref", MODE_PRIVATE).edit();
        faveditor.putBoolean("FT", false);
        faveditor.apply();
    }

    //Main application logic.
    void SetupApplication() {
        //Application logic.
        Log.d(TAG, "SetupApplication: Called");
        AskPermissions();
        SetUpChirp();
        getUID();
        startService(new Intent(getApplicationContext(), Listener.class));
        if(isNetworkAvailable()){
            //set up the network features.
            client = new OkHttpClient();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            client = builder.build();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getloc();
            spinner = findViewById(R.id.spinner);
            BTNaskhelp = findViewById(R.id.BTNaskhelp);
            BTNInNetCommunity = findViewById(R.id.BTNInNetCommunity);
            BTNInNetPredictor = findViewById(R.id.BTNInNetPredictor);
            BTNInNetPredictor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SearchSafety.class);
                    startActivity(intent);



//                    Intent intent = new Intent(MainActivity.this, SearchSafety.class);
//                    startActivity(intent);
                }
            });

            BTNInNetCommunity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Enter your organisation network ID key");

                    final EditText input = new EditText(MainActivity.this);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestBody body = new FormBody.Builder()
                                    .add("uid", mUid)
                                    .add("networkId", input.getText().toString())
                                    .build();
                            Request request = new Request.Builder()
                                    .url(getResources().getString(R.string.server) + "usenetwork")
                                    .post(body)
                                    .build();
                            Toast.makeText(getApplicationContext(), "Trying to Login", Toast.LENGTH_LONG).show();

                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull final Response response){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            organisation = input.getText().toString();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

            String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire", "Disaster"};
            ArrayAdapter<String> spinnerEmergencyAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, spinnerEmergencylist);
            spinnerEmergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerEmergencyAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString() + " selected.", Toast.LENGTH_SHORT).show();
                    Emergency = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Log.d(TAG, "onNothingSelected: nothing selected");
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            });
            BTNaskhelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeCall(Emergency);
                }
            });
        }
        else{
            Intent intent = new Intent(MainActivity.this, NoNetworkActivity.class);
            startActivity(intent);
        }
    }
    void openSafety(String res){
    }
    //Getting location of user.
    @SuppressLint("MissingPermission")
    protected void getloc() {
        super.onStart();
        fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: got location");
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mLat = location.getLatitude() + "";
                    mLon = location.getLongitude() + "";
                }
            }
        });
    }

    //Configuring chirp usage throughout the app.
    void SetUpChirp() {
        chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }
    }

    //These two methods are there to ask user permission.
    void AskPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Check if the phone has access to network.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //getting UID from the disk.
    void getUID(){
        SharedPreferences faveditor = getSharedPreferences("com.mikinshu.rakshak.ref", MODE_PRIVATE);
        mUid = faveditor.getString("UID", "error_in_getting_UID");
    }

    void saveUID(String UID){
        SharedPreferences.Editor faveditor = getSharedPreferences("com.mikinshu.rakshak.ref", MODE_PRIVATE).edit();
        faveditor.putString("UID", UID);
        faveditor.apply();
    }

    //Makes network call and call to the respective service from the phone.
    @SuppressLint("MissingPermission")
    void makeCall(String type) {
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String url = getResources().getString(R.string.server) + "requests";
        RequestBody body = new FormBody.Builder()
                .add("uid", mUid)
                .add("loc", mLat + " " + mLon)
                .add("type", type)
                .add("msg", "")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
        String phoneNumber;
        if (type.equalsIgnoreCase("General Emergency")) {
            phoneNumber = getString(R.string.general_call);
        } else if (type.equalsIgnoreCase("Fire")) {
            phoneNumber = getString(R.string.fire_call);
        } else if (type.equalsIgnoreCase("Medical")) {
            phoneNumber = getString(R.string.medical_call);
        } else {
            phoneNumber = getString(R.string.disaster_call);
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }
}