package com.example.notetaking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notetaking.R;
import com.example.notetaking.function.SessionPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email_et, pass_et;
    MaterialButton login_mbtn;
    String TAG = "Login TAG";
    SessionPref sessionPref;
    TextView signup_tv, reset_password_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email_et = findViewById(R.id.email_et);
        pass_et = findViewById(R.id.pass_et);
        signup_tv = findViewById(R.id.signup_tv);
        reset_password_tv = findViewById(R.id.reset_password_tv);
        login_mbtn = findViewById(R.id.login_mbtn);
        sessionPref = new SessionPref(this);

        reset_password_tv.setOnClickListener(v -> {
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });
        signup_tv.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
        login_mbtn.setOnClickListener(v -> {
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
                mAuth.signInWithEmailAndPassword(email_et.getText().toString(), pass_et.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "LoginUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    gotoHome(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "LoginUserWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
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