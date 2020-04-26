package com.example.opgalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendDataCameraFragment extends Fragment {

    public SendDataCameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_send_data_camera, container, false);
        Button dwn = view.findViewById(R.id.btn_download);
        dwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Downloading Video Files From OGICamera Please Wait",Toast.LENGTH_LONG).show();
            }
        });
        Button upl = view.findViewById(R.id.btn_sendtoserver);
        upl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Sending Video Files From Server Please Wait",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
