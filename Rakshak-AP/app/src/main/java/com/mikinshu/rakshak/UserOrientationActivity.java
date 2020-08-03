package com.mikinshu.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserOrientationActivity extends AppCompatActivity {

    Button btnConfirm;
    String Name = "Shinchan", MedicalCondition = "Bakchodi", AdhaarNumber = "6265502674", EmergencyContact2 = "8109938187", DOB = "25/03/1999", AADHAAR = "2500", EmergencyContact1 = "6265502674";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orientation);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setBackgroundColor(Color.parseColor("#247BA0"));
        final EditText etname = findViewById(R.id.editTextNumber2), etaadhaar = findViewById(R.id.editTextNumber4), etMedCon = findViewById(R.id.editTextNumber);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name = etname.getText().toString();
                AADHAAR = etaadhaar.getText().toString();
                MedicalCondition = etMedCon.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("Name", Name);
                intent.putExtra("MedicalCondition", MedicalCondition);
                intent.putExtra("EmergencyContact1", EmergencyContact1);
                intent.putExtra("EmergencyContact2", EmergencyContact2);
                intent.putExtra("DOB", DOB);
                intent.putExtra("AADHAAR", AADHAAR);
                setResult(RESULT_OK, intent);
                UserOrientationActivity.this.finish();
            }
        });
    }
}