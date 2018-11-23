package com.project.uberauto;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

public class RegisterActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextView textlogin;
    EditText name,email,phone,city,passwd;
    RadioGroup rgroup;
    Button registerbtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        textlogin = findViewById(R.id.textlogin);
        rgroup = findViewById(R.id.rgroup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.semail);
        phone = findViewById(R.id.editphone);
        phone.setText(getIntent().getStringExtra("phone"));
        passwd = findViewById(R.id.password);
        city = findViewById(R.id.city);
        registerbtn = findViewById(R.id.signupbtn);
        registerbtn.setOnClickListener(registerUser);
        avi = findViewById(R.id.avimain);
        avi.setVisibility(View.INVISIBLE);
        textlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(view);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton user = findViewById(R.id.ruser);
            }
        });
        CollectionReference usersCollectionRef = db.collection("Users");
    }
    // Register on Click
    View.OnClickListener registerUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(name.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(email.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(passwd.getText().toString().isEmpty())    {
                Toast.makeText(RegisterActivity.this, "password is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(phone.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "phone is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(city.getText().toString().isEmpty()){
                Toast.makeText(RegisterActivity.this, "City is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            avi.show();
            mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), passwd.getText().toString().trim())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            avi.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                //        Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                //                Toast.LENGTH_LONG).show();
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                switch (errorCode) {

                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                        Toast.makeText(RegisterActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                        Toast.makeText(RegisterActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_CREDENTIAL":
                                        Toast.makeText(RegisterActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(RegisterActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                        email.setError("The email address is badly formatted.");
                                        email.requestFocus();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        Toast.makeText(RegisterActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                                        passwd.setError("password is incorrect ");
                                        passwd.requestFocus();
                                        passwd.setText("");
                                        break;

                                    case "ERROR_USER_MISMATCH":
                                        Toast.makeText(RegisterActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                        Toast.makeText(RegisterActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                        Toast.makeText(RegisterActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                       // Toast.makeText(RegisterActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                                        email.setError("The email address is already in use.");
                                        email.requestFocus();
                                        break;

                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                        Toast.makeText(RegisterActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        Toast.makeText(RegisterActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_TOKEN_EXPIRED":
                                        Toast.makeText(RegisterActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(RegisterActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_USER_TOKEN":
                                        Toast.makeText(RegisterActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                        Toast.makeText(RegisterActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                      //  Toast.makeText(RegisterActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                                        passwd.setError("The password must be atleast 6 characters long");
                                        passwd.requestFocus();
                                        break;
                                }
                            }else { // register success
                                pushdb(email.getText().toString().trim(),passwd.getText().toString().trim());
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                Log.d("this line will"," not execute");
                            }
                        }
                    });

           }};

    @Override
    protected void onResume() {
        super.onResume();
       avi.setVisibility(View.GONE); //as soon as the activity loads avi is hidden
    }

    public void pushdb(String semail,String spassword){
        avi.show();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name",name.getText().toString() );
        data.put("email", semail);
        data.put("phone no", phone.getText().toString());
        data.put("Password",spassword);
        data.put("city", city.getText().toString());
        data.put("Date & time", sdf.format(date));

        db.collection("Users").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                avi.hide();
                Toast.makeText(RegisterActivity.this, "Success User Registered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "failed to insert into db:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
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
