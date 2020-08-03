package com.mikinshu.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchSafety extends AppCompatActivity {

    Animation animRotation;
    Animation animFadein;
    Button rotateButton;
    TextView txtView;
    String prediction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_safety);
        //prediction = getIntent().getStringExtra("Prediction");
        getSupportActionBar().hide();
        rotateButton=findViewById(R.id.BTNrotate);
        txtView= findViewById(R.id.perText);
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        animRotation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        try {
            final String currentTime = Calendar.getInstance().getTime().toString().split(" ")[3];
            Log.d("heloo", "world");
//                        RequestBody body = new FormBody.Builder()
//                                .add("military_time", currentTime.substring(0, currentTime.lastIndexOf(':')))
//                                .add("lat", mLat)
//                                .add("long", mLon)
//                                .add("age", "25")
//                                .add("gender", "female")
//                                .build();
            String url = "https://rakshak-ps.herokuapp.com/" + "predict/"+ currentTime.substring(0, currentTime.lastIndexOf(':'))+"/"+18.516726+"/"+73.856255+"/"+"1/25";
            Log.d("url", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            //Log.d("Request", request.toString());
//                        try (Response response = client.newCall(request).execute()) {
//                            Log.d("resp",response.body().string());
//                        }
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SearchSafety.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
//
                                        //prediction = response.body().string();

                                      txtView.setText(String.valueOf(100-Double.parseDouble(response.body().string())));
                                      txtView.setVisibility(View.VISIBLE);
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
        } catch (Exception e) {
            Log.d("TAG", "Location is Null " + e);
        }
        final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Toast.makeText(SearchSafety.this,"Started Rotation",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Toast.makeText(SearchSafety.this,"End Rotation",Toast.LENGTH_SHORT).show();

                animFadein.restrictDuration(5000);
                txtView.startAnimation(animFadein);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Toast.makeText(SearchSafety.this,"Repeat Rotation",Toast.LENGTH_SHORT).show();
            }
        };
        animRotation.setAnimationListener(animationListener);
        animRotation.restrictDuration(3000);
        rotateButton.startAnimation(animRotation);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animRotation.start();
                rotateButton.startAnimation(animRotation);
            }
        });
    }
}