package com.example.notetaking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notetaking.R;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.SessionPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.A;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email_et;
    MaterialButton reset_pass_mbtn;
    String TAG = "Reset password TAG";
    SessionPref sessionPref;
    TextView login_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        mAuth = FirebaseAuth.getInstance();
        email_et = findViewById(R.id.email_et);
        login_tv = findViewById(R.id.login_tv);
        reset_pass_mbtn = findViewById(R.id.reset_pass_mbtn);
        sessionPref = new SessionPref(this);

        login_tv.setOnClickListener(v -> {
            onBackPressed();
        });
        reset_pass_mbtn.setOnClickListener(v -> {
            if (email_et.getText().toString().isEmpty()) {
                email_et.setError("Required");
                Toast.makeText(this, "Please check error", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(email_et.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    String msg = "Email sent. Check email in inbox and spam";
                                    CommonFunction._LoadAlert(ResetPasswordActivity.this, msg);
                                    Toast.makeText(ResetPasswordActivity.this, msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}