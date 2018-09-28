package com.project.uberauto;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    String mcode;
    TextView sendotp,skip;
    EditText phone,otp;
    Button verify;
    PhoneAuthProvider.ForceResendingToken mResendToken;
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
        verify = findViewById(R.id.vbutton);  //btn
        verify.setOnClickListener(verifyotp);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyActivity.this,PostLogin.class));
            }
        });
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(phone.getText().toString().length()==10) {
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
            String code = credential.getSmsCode();
            Log.d("", "onVerificationCompleted:" + credential);
            if(code!=null) {
                Toast.makeText(VerifyActivity.this, "Sms Detected", Toast.LENGTH_SHORT).show();
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

            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
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

            Log.d("", "onCodeSent:" + verificationId);
            Toast.makeText(VerifyActivity.this, "code sent please wait", Toast.LENGTH_SHORT).show();
            // Save verification ID and resending token so we can use them later
            mcode = verificationId;
            mResendToken = token;
            Log.d("#otp = ",mcode);

            // ...
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        avi.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            avi.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            Toast.makeText(VerifyActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();
                            //if new user ask name else login
                            Intent view = new Intent(VerifyActivity.this,PostLogin.class);
                            //view.putExtra("phone",phone.getText().toString());
                            startActivity(view);
                        } else {
                            // Sign in failed, display a message and update the UI
                            avi.setVisibility(View.GONE);
                            Log.w("", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyActivity.this, "Invalid code :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
