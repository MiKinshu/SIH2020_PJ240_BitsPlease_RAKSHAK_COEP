package com.mikinshu.rakshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.interfaces.ChirpEventListener;
import io.chirp.chirpsdk.models.ChirpError;

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

    String TAG = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: In OnCreate");
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
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        } else SetupApplication();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //User Signed-In
                Log.d(TAG, "onActivityResult: User logged in");
                mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) { //user signing for the first time.
                    //open user orientation activity.
                    Log.d(TAG, "onActivityResult: user logged in for the first time");
                    Intent intent = new Intent(MainActivity.this, com.mikinshu.rakshak.UserOrientationActivity.class);
                    startActivityForResult(intent, RC_USER_PREF_ACT);
                } else {
                    Log.d(TAG, "onActivityResult: Existing user");
                    MarkFirstTimeFalse();
                    SetupApplication();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cannot work, until you sign in.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_USER_PREF_ACT) {
            if (resultCode == RESULT_OK) {
                String Name, MedicalCondition, EmergencyContact1, EmergencyContact2, DOB;
                Name = data.getStringExtra("Name");
                MedicalCondition = data.getStringExtra("MedicalCondition");
                EmergencyContact1 = data.getStringExtra("EmergencyContact1");
                EmergencyContact2 = data.getStringExtra("EmergencyContact2");
                DOB = data.getStringExtra("DOB");
                Log.d(TAG, "onActivityResult: Name : " + Name);
                Log.d(TAG, "onActivityResult: MedicalCondition : " + MedicalCondition);
                Log.d(TAG, "onActivityResult: EmergencyContact1 : " + EmergencyContact1);
                Log.d(TAG, "onActivityResult: EmergencyContact2 : " + EmergencyContact2);
                Log.d(TAG, "onActivityResult: DOB : " + DOB);
                PutUserDataToFirebase(Name, MedicalCondition, EmergencyContact1, EmergencyContact2, DOB);
                AskPermissions();
                MarkFirstTimeFalse();
                SetupApplication();
            } else {
                Toast.makeText(this, "Cannot work, until you provide info.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    void PutUserDataToFirebase(String Name, String MedicalCondition, String EmergencyContact1, String EmergencyContact2, String DOB) {
        Log.d(TAG, "PutUserDataToFirebase: Called");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mUsersDatabaseReference = firebaseDatabase.getReference().child("Users").child(mUid);
            Log.d(TAG, "PutUserDataToFirebase: User ID : " + mUid);
            mUsersDatabaseReference.child("Name").setValue(Name);
            mUsersDatabaseReference.child("MedicalCondition").setValue(MedicalCondition);
            mUsersDatabaseReference.child("EmergencyContact1").setValue(EmergencyContact1);
            mUsersDatabaseReference.child("EmergencyContact2").setValue(EmergencyContact2);
            mUsersDatabaseReference.child("DOB").setValue(DOB);
            mUsersDatabaseReference.child("NetworkID").setValue("No ID set yet");
        } else Log.d(TAG, "PutUserDataToFirebase: FireBase Error - Cannot find logged in user.");
    }

    void MarkFirstTimeFalse() {
        SharedPreferences.Editor faveditor = getSharedPreferences("com.mikinshu.rakshak.ref", MODE_PRIVATE).edit();
        faveditor.putBoolean("FT", false);
        faveditor.apply();
    }

    void SetupApplication() {
        //Application logic.
        Log.d(TAG, "SetupApplication: Called");
        AskPermissions();
        //startService(new Intent(getApplicationContext(), Listener.class));

        if(isNetworkAvailable()){
            //set up the network features.
        }
        else {
            Intent intent= new Intent(MainActivity.this,com.mikinshu.rakshak.NoNetworkActivity.class);
            startActivity(intent);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}