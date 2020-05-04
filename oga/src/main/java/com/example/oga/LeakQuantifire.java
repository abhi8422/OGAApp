package com.example.oga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import static com.example.oga.Bluetooth.leakActivity;
import static com.example.oga.MainActivity.barcodenumber;

public class LeakQuantifire extends AppCompatActivity {
static TextView barcode,Reading,MaxReading;
static int i=0;
static Double max = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_quantifire);
        barcode=findViewById(R.id.leak_barcode);
        barcode.setText(barcodenumber);
        Reading=findViewById(R.id.leak_reading);
        MaxReading=findViewById(R.id.max_leakrate);
        leakActivity=true;
    }

    public static Handler LeakQunatifirehandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 7:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempmsg=new String(readBuff,0,msg.arg1);
                    tempmsg=tempmsg.replace("\r\n","#");
                    String[] FilterString=tempmsg.split("#");
                    String fString;
                    try{
                        for(String s:FilterString){
                            fString=s.replace("OK", " ").trim();
                            if(fString.length()!=0){
                                    if(i==0){
                                        max=Double.valueOf(fString);
                                        i++;
                                    }
                                    if (max > Double.valueOf(fString)) {
                                        MaxReading.setText(String.valueOf(max));
                                    }else {
                                        max=Double.valueOf(fString);
                                        MaxReading.setText(String.valueOf(max));
                                    }
                                    Reading.setText(fString);
                            }
                        }
                    }catch (NumberFormatException ex){
                        ex.printStackTrace();
                        break;
                    }
            }
            return true;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leakActivity=false;
    }

    public void Save(View view) {
        startActivity(new Intent(this,Upload.class));
    }
}
