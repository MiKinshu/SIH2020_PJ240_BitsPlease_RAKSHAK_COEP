package com.mikinshu.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserOrientationActivity extends AppCompatActivity {

    Button btnConfirm;
    String Name = "Shinchan", MedicalCondition = "Bakchodi", EmergencyContact1 = "6265502674", EmergencyContact2 = "8109938187", DOB = "25/03/1999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orientation);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Name", Name);
                intent.putExtra("MedicalCondition", MedicalCondition);
                intent.putExtra("EmergencyContact1", EmergencyContact1);
                intent.putExtra("EmergencyContact2", EmergencyContact2);
                intent.putExtra("DOB", DOB);
                setResult(RESULT_OK, intent);
                UserOrientationActivity.this.finish();
            }
        });
    }
}