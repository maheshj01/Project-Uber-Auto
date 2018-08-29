package com.project.uberauto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextView textlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textlogin = findViewById(R.id.textlogin);
        textlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(view);
            }
        });
    }
}
