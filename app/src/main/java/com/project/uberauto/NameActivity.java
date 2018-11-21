package com.project.uberauto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class NameActivity extends AppCompatActivity {
    private String url= "https://onkarbangale44.000webhostapp.com/Reg.php";
    private Button next;
    EditText first,last;  // first and last name
    private String phone; //phone number
    private RadioGroup rgroup;
    public static String currentUser;  // user / driver
    int x;
    AVLoadingIndicatorView avi;
    Boolean status;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        next = findViewById(R.id.next);
        first = findViewById(R.id.first);
        last = findViewById(R.id.last);
        avi = findViewById(R.id.avimain);
        avi.setVisibility(View.INVISIBLE);
/*        Bundle bundle =getIntent().getExtras();
        phone = bundle.getString("number");*/
        phone = getApplicationContext().getSharedPreferences("phonecache",MODE_PRIVATE).getString("phone","9423757172");
        rgroup = findViewById(R.id.rgroup);
        status = true;
        currentUser = "User";
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = findViewById(checkedId);
                currentUser = checked.getText().toString();
                Toast.makeText(NameActivity.this, checked.getText().toString() + "current user: "+ phone, Toast.LENGTH_SHORT).show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!first.getText().toString().isEmpty()&&!last.getText().toString().isEmpty()) {
                    avi.show();
                    Toast.makeText(NameActivity.this, "lOGIN Success for "+ phone, Toast.LENGTH_SHORT).show();
                    //registerUser(); // if register success
                        updateUser(first.getText().toString(),last.getText().toString());
                }
                else{
                    Toast.makeText(NameActivity.this, "first or last name left empty!", Toast.LENGTH_SHORT).show();
            }
        }
    });

}

    public void updateUser(String firstname,String  lastname){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("First_Name",firstname);
        data.put("Last_Name",lastname);
        data.put("TimeStamp", FieldValue.serverTimestamp());
        data.put("Phone_Number",phone);
        data.put("Status",currentUser);

        // overwrites the document
        db.collection(currentUser)
                .document(phone)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        avi.setVisibility(View.GONE);
                        Toast.makeText(NameActivity.this, "Success User Registered", Toast.LENGTH_SHORT).show();
                        Intent start = new Intent(NameActivity.this, PostLogin.class);
                        startActivity(start);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avi.setVisibility(View.GONE);
                Toast.makeText(NameActivity.this, "Failed to Register:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Boolean registerUser() {
        if (currentUser.equals("user")) {
            x = 1;
            Toast.makeText(this, "User Selected", Toast.LENGTH_SHORT).show();
        } else {
            x = 2;
            Toast.makeText(this, "Driver Selected", Toast.LENGTH_SHORT).show();
        }
        StringRequest request = new StringRequest(Request.Method.GET, "https://onkarbangale44.000webhostapp.com/Reg.php" + "?currentUser=" + x + "&fname=" + first.getText() + "&lname=" + last.getText() + "&number=" + phone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        avi.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                String obj = jsonObject.getString("error_msg");
                                Toast.makeText(getApplicationContext(), "Success" + obj , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                if(error == null) {
                    Toast.makeText(getApplicationContext(), "null response", Toast.LENGTH_LONG).show();
                }
                try{
                    body=new String(error.networkResponse.data,"UTF-8");   // no internet error
                 }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
        Log.d("status=",status.toString());
        Toast.makeText(this, "status=" + status.toString(), Toast.LENGTH_SHORT).show();
        return status;
    }

}
