package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    String mcode;
    TextView sendotp,skip,msg;
    EditText phone,otp;
    Button verify;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RadioGroup rgroup;
    public static String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth = FirebaseAuth.getInstance();
        phone = findViewById(R.id.phone);
        skip=findViewById(R.id.skip);
        sendotp = findViewById(R.id.textView2);
        avi=findViewById(R.id.aviverify);
        otp = findViewById(R.id.otp);
        otp.setVisibility(View.INVISIBLE);
        verify = findViewById(R.id.vbutton);  //btn
        verify.setVisibility(View.INVISIBLE);
        verify.setOnClickListener(verifyotp);
        avi.setVisibility(View.INVISIBLE);
        msg = findViewById(R.id.textView5);
        msg.setVisibility(View.INVISIBLE);
        currentUser="User";
        rgroup = findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = findViewById(checkedId);
                currentUser = checked.getText().toString();
                Toast.makeText(VerifyActivity.this, checked.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null || sharedPref.getString("sharedphone","defvalue")!=null){
           startActivity(new Intent(VerifyActivity.this,PostLogin.class));
           finish();
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyActivity.this,NameActivity.class));
                finish();
            }
        });
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(phone.getText().toString().length()==10) {
                msg.setVisibility(View.VISIBLE);
                avi.show();
                sendOtp(phone.getText().toString());
            }
            else{
                Toast.makeText(VerifyActivity.this, "enter 10 digit phone no", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
    // verify otp
    View.OnClickListener verifyotp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String code = otp.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
               otp.setError("Enter valid code");
                otp.requestFocus();
                return;
            }
            avi.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mcode, code);
            //signing the user
            signInWithPhoneAuthCredential(credential);
        }
    };

    public void sendOtp(String phoneno) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneno.trim(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);
           }

   private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override  //on auto detect sms:success
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            avi.setVisibility(View.GONE);
            String code = credential.getSmsCode();
            Log.d("", "onVerificationCompleted:" + credential);
            if(code!=null) {
                Toast.makeText(VerifyActivity.this, "Sms Detected", Toast.LENGTH_SHORT).show();
                msg.setText("OTP Verified :)");
                signInWithPhoneAuthCredential(credential);
                otp.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(VerifyActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                otp.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);

            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                msg.setText("OTP Request out of Service :( ");
                sendotp.setClickable(false);
                sendotp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                Toast.makeText(VerifyActivity.this, "Sms Limit exceeded please try again later ", Toast.LENGTH_SHORT).show();
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            otp.setVisibility(View.VISIBLE);
            verify.setVisibility(View.VISIBLE);
            Log.d("", "onCodeSent:" + verificationId);
            Toast.makeText(VerifyActivity.this, "code sent please wait", Toast.LENGTH_SHORT).show();
            msg.setText("Otp Sent!,waiting to Auto Detect Sms");
            // Save verification ID and resending token so we can use them later
            mcode = verificationId;
            mResendToken = token;
            Log.d("#otp = ",mcode);
            // ...
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            avi.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            Toast.makeText(VerifyActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();
                           /* SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("phonecache",MODE_PRIVATE).edit();
                            editor.putString("phone",phone.getText().toString());
                            editor.commit();*/
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("sharedphone",phone.getText().toString());
                            editor.commit();
                            //if new user? ask name: else login;
                            db.collection(currentUser)
                                    .document(phone.getText().toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot doc = task.getResult();
                                            if(doc.exists()){
                                                Intent view = new Intent(VerifyActivity.this,PostLogin.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("number",phone.getText().toString());
                                                view.putExtras(bundle);
                                                startActivity(view);
                                                finish();
                                            }
                                            else{
                                                Intent view = new Intent(VerifyActivity.this,NameActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("number",phone.getText().toString());
                                                view.putExtras(bundle);
                                                startActivity(view);
                                                finish();
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VerifyActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("error:",e.getMessage());
                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            avi.setVisibility(View.GONE);
                            Log.w("", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyActivity.this, "Invalid code :(", Toast.LENGTH_SHORT).show();
                                verify.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
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
