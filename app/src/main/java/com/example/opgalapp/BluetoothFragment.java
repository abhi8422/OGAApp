package com.example.opgalapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

import static com.example.opgalapp.MainActivity.flag;


/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothFragment extends Fragment {
private BluetoothAdapter bluetoothAdapter;
private Intent bluetoothEnableIntent;
    public BluetoothFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ListView listView=view.findViewById(R.id.ble_list);
        Button refresh = view.findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment ft=getActivity().getSupportFragmentManager().findFragmentByTag("Ble_frag");
                FragmentTransaction FT=getActivity().getSupportFragmentManager().beginTransaction();
                FT.detach(ft);
                FT.attach(ft);
                FT.commit();
            }
        });
        Button btn_nxt=view.findViewById(R.id.btn_nxt);
        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Connection is successful",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),MainActivity.class));
                flag=true;
            }
        });
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        bluetoothEnableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if(!bluetoothAdapter.isEnabled()){
            startActivityForResult(bluetoothEnableIntent,9);
        }
        Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        BluetoothDevice[] btArray=new BluetoothDevice[bt.size()];
        int index=0;
        if(bt.size()>0){
            for(BluetoothDevice device:bt){
                btArray[index]=device;
                strings[index]=device.getName();
                index++;
            }
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,strings);
        listView.setAdapter(arrayAdapter);
        return view;
    }


}
