package com.example.notetaking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notetaking.R;
import com.example.notetaking.function.SessionPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email_et, pass_et;
    MaterialButton sign_up_mbtn;
    String TAG = "Sign up TAG";
    SessionPref sessionPref;
    TextView login_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        email_et = findViewById(R.id.email_et);
        pass_et = findViewById(R.id.pass_et);
        login_tv = findViewById(R.id.login_tv);
        sign_up_mbtn = findViewById(R.id.sign_up_mbtn);
        sessionPref = new SessionPref(this);

        login_tv.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        sign_up_mbtn.setOnClickListener(v -> {
            boolean isError = false;
            if (email_et.getText().toString().isEmpty()) {
                email_et.setError("Required");
                isError = true;
            }
            if (pass_et.getText().toString().isEmpty()) {
                pass_et.setError("Required");
                isError = true;
            }
            if (isError) {
                Toast.makeText(this, "Please check error", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email_et.getText().toString(), pass_et.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    gotoHome(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Already user is login", Toast.LENGTH_SHORT).show();
            gotoHome(null);
        }
    }

    private void gotoHome(FirebaseUser user) {
        if (user != null) {
            sessionPref.setIsLogin(true);
            sessionPref.setUserEmail(user.getEmail());
            sessionPref.setUserPassword(pass_et.getText().toString());
            sessionPref.setUserProviderId(user.getProviderId());
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}