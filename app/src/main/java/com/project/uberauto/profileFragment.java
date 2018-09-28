package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class profileFragment extends Fragment {
    Button logout;
    FirebaseAuth mAuth;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment first
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                      if(mAuth.getCurrentUser()==null){ // user not signed in
                          Toast.makeText(getContext(), "auth state changed logged out", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getContext(),VerifyActivity.class));
                      }
                      else{  // already signed in
                          Toast.makeText(getContext(), "user already signed in", Toast.LENGTH_SHORT).show();
                      }
                    }
                };
                mAuth.addAuthStateListener(listener);
                mAuth.signOut();
            }
        });

        return view;
    }
}
