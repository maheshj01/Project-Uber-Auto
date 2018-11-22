package com.project.uberauto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_dialog, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tvname = v.findViewById(R.id.drivername);
        tvphone = v.findViewById(R.id.driverphone);

        String title=this.getArguments().getString("title");

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