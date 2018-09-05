package com.project.uberauto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextView textlogin;
    EditText name,email,phone,city,passwd;
    Button registerbtn;
    ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("name",name.getText().toString() );
                data.put("email", email.getText().toString());
                data.put("phone no", phone.getText().toString());
                data.put("city", city.getText().toString());

                db.collection("New User").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }};

    }
