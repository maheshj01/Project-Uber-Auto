package com.project.uberauto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyCustomDialogFragment extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public TextView tvname;
    public TextView tvphone;
    public Button book_now;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_dialog, container, false);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        tvname = v.findViewById(R.id.drivername);
        tvphone = v.findViewById(R.id.driverphone);
        book_now = v.findViewById(R.id.booknow);
        final String title=this.getArguments().getString("title");
        final Boolean destination_marked = this.getArguments().getBoolean("destination_marked");
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
                    Toast.makeText(getContext(), "Suceess All set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SharedPreferences sharedPref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
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
                                tvname.setText(doc.getString("First_Name") + " " + doc.getString("Last_Name"));
                                tvphone.setText(doc.getString("Phone_Number"));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        return v;
    }
}