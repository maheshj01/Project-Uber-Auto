package com.project.uberauto;

import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button signin,register;
    EditText text,email,password;
    TextView skip,forgot;
    ImageView facebook,google,twitter;
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avi=findViewById(R.id.avi);
        setContentView(R.layout.activity_main);
        forgot= findViewById(R.id.forgot);
        register = findViewById(R.id.register);
        signin = findViewById(R.id.signin);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        google = findViewById(R.id.gplus);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        google.setOnClickListener(logintoGoogle);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerview = new Intent(MainActivity.this,RegisterActivity.class);
                registerview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                registerview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(registerview);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().length() != 10){
                    Toast.makeText(MainActivity.this, "Please enter 10 digit phone no", Toast.LENGTH_SHORT).show();
                }
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                    Toast.makeText(MainActivity.this, "email or password empty", Toast.LENGTH_SHORT).show();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mverify = new Intent(MainActivity.this,VerifyActivity.class);
                startActivity(mverify);
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    View.OnClickListener logintoGoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent view = new Intent(MainActivity.this,PostLogin.class);
            startActivity(view);
        }
    };

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}