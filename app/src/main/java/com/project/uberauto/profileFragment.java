package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wang.avi.AVLoadingIndicatorView;

public class profileFragment extends Fragment {
    Button logout;
    FirebaseAuth mAuth;
    View view;
    ImageView status;
    TextView profilename;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SessionManager session;
    AVLoadingIndicatorView avi;
    String CurrentUser,phoneno;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment first
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = view.findViewById(R.id.logout);
        MaterialSpinner spinner = (MaterialSpinner)view.findViewById(R.id.spinner);
        spinner.setSelectedIndex(1);
        status = view.findViewById(R.id.statusicon);
        profilename = view.findViewById(R.id.name);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences("DATA",Context.MODE_PRIVATE);
        String sname=sharedPref.getString("shared_name","null");
        CurrentUser=sharedPref.getString("shared_status","null");
        phoneno = sharedPref.getString("shared_phone",null);
        profilename.setText(sname);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                if(mAuth.getCurrentUser()==null){
                    Toast.makeText(getContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(),VerifyActivity.class));
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.clear();
                    edit.commit();
                }
                else{
                    Toast.makeText(getContext(), "Logout failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(CurrentUser.equalsIgnoreCase("Driver")) {
            spinner.setItems("Available", "Running", "Offline");
            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    // update driver status to firebase
                    if (item == "Available") {
                        status.setImageDrawable(getResources().getDrawable(R.drawable.available));
                        updateStatus(item);
                    } else if (item == "Running") {
                        status.setImageDrawable(getResources().getDrawable(R.drawable.running));
                        updateStatus(item);
                    } else if (item == "Offline") {
                        status.setImageDrawable(getResources().getDrawable(R.drawable.offline));
                        updateStatus(item);
                    }
                }
            });
        }
        else{
            status.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    public void updateStatus(String item){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CurrentUser)
                .document(phoneno)
                .update("Status",item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Driver Status Updated", Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, "Failure: " + e, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
