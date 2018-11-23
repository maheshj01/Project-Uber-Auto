package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyCustomDialogFragment extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public TextView tvname;
    public TextView tvphone;
    public Button book_now;
    String driver_name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_dialog, container, false);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        tvname = v.findViewById(R.id.drivername);
        tvphone = v.findViewById(R.id.driverphone);
        book_now = v.findViewById(R.id.booknow);
        final String title=this.getArguments().getString("title");
        final String source_lat = getArguments().getString("source_lat");
        final String source_lan = getArguments().getString("source_lan");
        final String dest_lat = getArguments().getString("destination_lat");
        final String dest_lan = getArguments().getString("destination_lan");
        final Boolean destination_marked = this.getArguments().getBoolean("destination_marked");
        final SharedPreferences sharedPref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        tvphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+91" + title));
                startActivity(callIntent);
            }
        });
        book_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!destination_marked){
                    Toast.makeText(getContext(), "Choose destination on Map", Toast.LENGTH_LONG).show();
                }
                else{
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("User_name",sharedPref.getString("shared_name","null"));
                    data.put("Driver_name",driver_name);
                    data.put("Source_Lat",source_lat);
                    data.put("Source_Lan",source_lan);
                    data.put("TimeStamp", FieldValue.serverTimestamp());
                    data.put("Destination_Lat",dest_lat);
                    data.put("Destination_Lan",dest_lan);
                    Toast.makeText(getContext(), "Success All set", Toast.LENGTH_SHORT).show();
                    db.collection("Requests")
                            .document()
                            .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( "onSuccess: ","Request Submitted");
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            HomeFragment home = new HomeFragment();
                            ft.replace(R.id.frame_container,home);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failed: ",e.getMessage());
                        }
                    });
                }
            }
        });

        String sphone = sharedPref.getString("shared_phone","null");
        if(sphone!=null) {
            db.collection("Driver")
                    .document(title)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if(task.isSuccessful()) {
                                driver_name=doc.getString("First_Name") + " " + doc.getString("Last_Name");
                                tvname.setText(driver_name);
                                tvphone.setText(doc.getString("Phone_Number"));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Exception :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return v;
    }
}