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

public class MainActivity extends AppCompatActivity{
    Button signin,register;
    EditText text,email,password;
    TextView forgot;
    ImageView facebook,google,twitter;
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        forgot= findViewById(R.id.forgot);
        register = findViewById(R.id.register);
        signin = findViewById(R.id.signin);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        google = findViewById(R.id.gplus);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        google.setOnClickListener(logintoGoogle);
        avi = findViewById(R.id.avimain);
        avi.hide();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerview = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerview);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "email or password empty", Toast.LENGTH_SHORT).show();
                    avi.setVisibility(View.GONE);
                    return;
                }
            avi.show();
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        avi.setVisibility(View.GONE);
                        Log.d("", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Authentication Success ! Welcome.",
                                Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(MainActivity.this,PostLogin.class);
                        startActivity(login);
                  //      updateUI(user);
                    } else {
                        avi.hide();
                        // If sign in fails, display a message to the user.
                        Log.d("", "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
               //         updateUI(null);
                    }

                    // ...
                }
            });

            }});

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mverify = new Intent(MainActivity.this,VerifyActivity.class);
                startActivity(mverify);
            }
        });
    }
    @Override
    public void onStart() {
        mAuth = FirebaseAuth.getInstance();
        super.onStart();
        /*if(mAuth.getCurrentUser()!=null){
            Toast.makeText(this, "User Already Signed in skip main", Toast.LENGTH_SHORT).show();
            Intent view = new Intent(MainActivity.this,PostLogin.class);
            startActivity(view);
        }*/
       /*  Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);*/
    }

    View.OnClickListener logintoGoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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