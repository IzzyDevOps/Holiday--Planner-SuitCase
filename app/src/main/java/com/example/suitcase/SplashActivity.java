package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimationView = findViewById(R.id.lottie);
        textView = findViewById(R.id.textlogo);


        textView.animate().translationY(-1400).setDuration(2700).setStartDelay(0);
        lottieAnimationView.animate().translationX(2000).setDuration(2000).setStartDelay(2900);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentUser == null){ //user not logged in redirect to login page

                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    startActivity(intent);

                }else{ //user logged in redirect to AddItem page
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                }
                finish();
            }
        }, 5000);
    }
}
