package com.mikinshu.rakshak;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.nio.charset.Charset;

import io.chirp.chirpsdk.models.ChirpError;

import static com.mikinshu.rakshak.NoNetworkActivity.chirp;
import static com.mikinshu.rakshak.NoNetworkActivity.mLat;
import static com.mikinshu.rakshak.NoNetworkActivity.mLon;


public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button go;
    String TAG = "MyLOGS";
    public Spinner spinnerdialogue;
    String value = "General Emergency";
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;


    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogue);
        go = (Button) findViewById(R.id.BTNgo);
        spinnerdialogue = findViewById(R.id.spinner2);
        String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire", "Disaster"};
        ArrayAdapter<String> spinnerEmergencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, spinnerEmergencylist);
        spinnerEmergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdialogue.setAdapter(spinnerEmergencyAdapter);
        go.setOnClickListener(this);
        spinnerdialogue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(c, parent.getItemAtPosition(position).toString() + " selected.", Toast.LENGTH_SHORT).show();
                value = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: nothing selected");
                Toast.makeText(c, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.BTNgo) {
            makeNoNetCall(value);
        }
        dismiss();
    }

    void makeNoNetCall(String Emergency) {
//        String identifier = Emergency.charAt(0) + mLat.substring(6,9) + mLon.substring(6,9); //This is creating NULL Pointer Exception.
        String identifier = "Test1";
        Log.d(TAG, "makeNoNetCall: Indentifier is " + identifier);
        byte[] payload = identifier.getBytes(Charset.forName("UTF-8"));
        ChirpError error = chirp.send(payload);
        if (error.getCode() > 0) {
            Log.e("ChirpError: ", error.getMessage());
        } else {
            Log.v("ChirpSDK: ", "Sent " + identifier);
        }
    }
}