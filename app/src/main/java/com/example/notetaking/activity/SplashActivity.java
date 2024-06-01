package com.example.notetaking.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notetaking.R;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.SessionPref;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;
    SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionPref = new SessionPref(SplashActivity.this);
        requestPermissionDialog();
    }

    private void requestPermissionDialog() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
        ) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE}, 6);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        {
            if (requestCode == 6) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CommonFunction.isNetworkConnected(SplashActivity.this)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // checkLoginStatus
                                if (sessionPref.isLogin()) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    SplashActivity.this.finish();
                                } else {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    SplashActivity.this.finish();
                                }
                            }
                        }, SPLASH_TIME_OUT);
                    } else {
                        Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    ActivityCompat.finishAffinity(SplashActivity.this);
                }
            }
        }
    }
}