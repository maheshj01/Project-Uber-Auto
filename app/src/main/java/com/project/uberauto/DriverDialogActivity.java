package com.project.uberauto;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

public class DriverDialogActivity extends AppCompatActivity {
    Dialog dcard;

    public void show_card() {
        dcard.setContentView(R.layout.driver_dialog);
//            tvname.setText("Lorem" + " " +"Ipsum");
//            tvphone.setText("8668284377");
/*        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Driver")
                .document(Phoneno)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Button book = mapview.findViewById(R.id.button);
                        TextView tvname = mapview.findViewById(R.id.drivername);
                        TextView tvphone = mapview.findViewById(R.id.driverphone);
                        DocumentSnapshot doc = task.getResult();
                        if(task.isSuccessful()){
                            tvname.setText("Mahesh" + " " +"Jamdade");
                            tvphone.setText("8668284377");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failure:" + e, Toast.LENGTH_LONG).show();
            }
        });*/
        dcard.show();
    }
}
