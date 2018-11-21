package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class profileFragment extends Fragment {
    Button logout;
    FirebaseAuth mAuth;
    View view;
    ImageView status;
    TextView profilename;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        MaterialSpinner spinner = (MaterialSpinner)view.findViewById(R.id.spinner);
        spinner.setSelectedIndex(1);
        profilename = view.findViewById(R.id.name);
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
        status = view.findViewById(R.id.statusicon);
        spinner.setItems("Available","Running","Offline");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                // update driver status to firebase
                if(item=="Available"){
                    status.setImageDrawable(getResources().getDrawable(R.drawable.available));
                }
                else if(item =="Running" ){
                    status.setImageDrawable(getResources().getDrawable(R.drawable.running));
                }
                else if(item == "Offline"){
                    status.setImageDrawable(getResources().getDrawable(R.drawable.offline));
                }
            }
        });

        return view;
    }
}
