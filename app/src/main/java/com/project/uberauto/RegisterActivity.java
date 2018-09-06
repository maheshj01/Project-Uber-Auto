package com.project.uberauto;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextView textlogin;
    EditText name,email,phone,city,passwd;
    Button registerbtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textlogin = findViewById(R.id.textlogin);
        name = findViewById(R.id.name);
        email = findViewById(R.id.semail);
        phone = findViewById(R.id.editphone);
        passwd = findViewById(R.id.password);
        city = findViewById(R.id.city);
        registerbtn = findViewById(R.id.signupbtn);
        registerbtn.setOnClickListener(registerUser);
        avi = findViewById(R.id.avimain);
        mAuth = FirebaseAuth.getInstance();
        textlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(view);
            }
        });
        CollectionReference usersCollectionRef = db.collection("Users");
    }
    // Register on Click
    View.OnClickListener registerUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty()
                    || passwd.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
            } else {
               /* mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwd.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });*/
               updateUI(email.getText().toString(),passwd.getText().toString());
            }
        }};

    public void updateUI(String semail,String spassword){
        avi.show();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name",name.getText().toString() );
        data.put("email", semail);
        data.put("phone no", phone.getText().toString());
        data.put("Password",spassword);
        data.put("city", city.getText().toString());
        avi.show();
        db.collection("New User").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                avi.hide();
                Toast.makeText(RegisterActivity.this, "Success User Registered", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(login);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    }
