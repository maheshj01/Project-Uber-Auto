package com.project.uberauto;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


        tvname = v.findViewById(R.id.drivername);
        tvphone = v.findViewById(R.id.driverphone);

        String name=this.getArguments().getString("name");


        tvname.setText(name);
        tvphone.setText("8668284377");

        return v;
    }
}