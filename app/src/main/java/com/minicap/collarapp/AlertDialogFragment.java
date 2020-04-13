package com.minicap.collarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlertDialogFragment extends DialogFragment {

    private static final String TAG = "alertActivity";

    EditText intTempHighVal;
    EditText intTempHighTime;
    EditText intTempLowVal;
    EditText intTempLowTime;
    EditText extTempHighVal;
    EditText extTempHighTime;
    EditText extTempLowVal;
    EditText extTempLowTime;
    EditText battAlertVal;
    EditText watchdogAlertVal;

    Button alertEnable;

    SharedPreferenceHelper sph;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        intTempHighVal = view.findViewById(R.id.intTempHighVal);
        intTempHighTime = view.findViewById(R.id.intTempHighTime);
        intTempLowVal = view.findViewById(R.id.intTempLowVal);
        intTempLowTime = view.findViewById(R.id.intTempLowTime);
        extTempHighVal = view.findViewById(R.id.extTempHighVal);
        extTempHighTime = view.findViewById(R.id.extTempHighTime);
        extTempLowVal = view.findViewById(R.id.extTempLowVal);
        extTempLowTime = view.findViewById(R.id.extTempLowTime);

        alertEnable = view.findViewById(R.id.alertEnable);

        sph = new SharedPreferenceHelper(getActivity());

        intTempHighVal.setText(sph.getIntTempHighVal().toString());
        intTempHighTime.setText(sph.getIntTempHighTime().toString());
        intTempLowVal.setText(sph.getIntTempLowVal().toString());
        intTempLowTime.setText(sph.getIntTempLowTime().toString());
        extTempHighVal.setText(sph.getExtTempHighVal().toString());
        extTempHighTime.setText(sph.getExtTempHighTime().toString());
        extTempLowVal.setText(sph.getExtTempLowVal().toString());
        extTempLowTime.setText(sph.getExtTempLowTime().toString());

        alertEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAlerts();
            }
        });

        return view;
    }

    private void enableAlerts() {
        //if any entry is null
        if (intTempHighVal.getText().toString().isEmpty() || intTempHighTime.getText().toString().isEmpty() || intTempLowVal.getText().toString().isEmpty() || intTempLowTime.getText().toString().isEmpty() || extTempHighVal.getText().toString().isEmpty() || extTempHighTime.getText().toString().isEmpty() || extTempLowVal.getText().toString().isEmpty() || extTempLowTime.getText().toString().isEmpty() || battAlertVal.getText().toString().isEmpty() || watchdogAlertVal.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Invalid alert options", Toast.LENGTH_SHORT).show();
            return;
        }
        sph.saveAlertSettings(Double.parseDouble(intTempHighVal.getText().toString()), Double.parseDouble(intTempHighTime.getText().toString()), Double.parseDouble(intTempLowVal.getText().toString()), Double.parseDouble(intTempLowTime.getText().toString()), Double.parseDouble(extTempHighVal.getText().toString()), Double.parseDouble(extTempHighTime.getText().toString()), Double.parseDouble(extTempLowVal.getText().toString()), Double.parseDouble(extTempLowTime.getText().toString()), Double.parseDouble(battAlertVal.getText().toString()), Double.parseDouble(watchdogAlertVal.getText().toString()));
    }
}
