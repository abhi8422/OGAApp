package com.example.oga;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static com.example.oga.LeakQuantifire.LeakQunatifirehandler;

public class Bluetooth extends AppCompatActivity {
    Button btn_save;
    TextView status,message;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    BluetoothDevice device;
    private static final String TAG = "Bluetooth";
    public static final int STATE_LISTING=1;
    public static final int STATE_CONNECTING=2;
    public static final int STATE_CONNECTED=3;
    public static final int STATE_CONNECTION_FAILED=4;
    SendReceive sendReceive;
    public static boolean leakActivity=false;
    int REQUEST_ENABLED_BLUETOOTH=1;
    Intent bluetoothEnableIntent;
    ListView listView;
    byte[] buffer_send_receive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        btn_save=findViewById(R.id.btn_save);
        listView=findViewById(R.id.list_item);
        message=findViewById(R.id.message);
        status=findViewById(R.id.txt_status);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        bluetoothEnableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        //check bluetooth is on or off
        if(!bluetoothAdapter.isEnabled()){
            startActivityForResult(bluetoothEnableIntent,REQUEST_ENABLED_BLUETOOTH);
        }
        if(( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ) &&
                ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED )){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }
        Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        btArray=new BluetoothDevice[bt.size()];
        int index=0;
        if(bt.size()>0){
            for(BluetoothDevice device:bt){
                btArray[index]=device;
                strings[index]=device.getName();
                index++;
            }
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
        listView.setAdapter(arrayAdapter);
        implementListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this,"Permission Accepted",Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this,"User Refused To Accept The Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void implementListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    device=btArray[position];
                    ClientClass clientClass=new ClientClass(device);
                    clientClass.start();
                    status.setText("Connecting");
                } catch (IOException e) {
                    e.printStackTrace();
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                }
            }
        });


    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case STATE_LISTING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("CONNECTING");
                    break;
                case  STATE_CONNECTED:
                    status.setText("Connected Device :"+device.getName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(Bluetooth.this);
                    builder.setTitle("Device "+device.getName()+" Is Connected");
                    builder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alertdialog = builder.create();
                    alertdialog.show();

                    alertdialog.getWindow().setGravity(Gravity.BOTTOM);

                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("CONNECTION_FAILED");
                    break;
                case 7:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempmsg=new String(readBuff,0,msg.arg1);
                    //message.setText(tempmsg);
                    //status.setText("MSG :"+tempmsg);
                    break;
                case 8:
                    break;

            }
            return true;
        }
    });

    public void generateNoteOnSD(Context context, String sFileName, ArrayList<String> bufferdata) {
        Log.i(TAG,"Run Received Message::: "+bufferdata);
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStorageDirectory(), "TVA_Buffer_Data.txt");
            outputStream = new FileOutputStream(file);
            outputStream.write(buffer_send_receive);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
        }

    }



    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;
        public ClientClass(BluetoothDevice device1) throws IOException {
            device=device1;
            final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                socket=device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        }
        public void run(){
            try {
                /*if (!socket.isConnected()){
                    socket.connect();
                }*/
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive=new SendReceive(socket);
                String str_logstart="log start\r\n";
                try {
                    sendReceive.write(str_logstart.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String str_detector_fid="detector fid\r\n";
                try {
                    sendReceive.write(str_detector_fid.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendReceive.start();

            } catch (IOException e) {
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
                e.printStackTrace();
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
            }
        }
    }

    public class SendReceive extends Thread{

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        private SendReceive(BluetoothSocket socket) throws IOException {
            this.socket = socket;
            InputStream temIn=null;
            OutputStream temOut=null;
            temIn=socket.getInputStream();
            temOut=socket.getOutputStream();
            inputStream=temIn;
            outputStream=temOut;
        }

        public void run(){
            buffer_send_receive=new byte[1024];
            int bytes;
            while (true){
                try {

                    bytes=inputStream.read(buffer_send_receive);
                    if(bytes<=0){
                        handler.obtainMessage(8);
                    }
                    //String readMessage = new String(buffer_send_receive, 0, bytes);
                    //buffer_received_data.add(readMessage);
                    handler.obtainMessage(7,bytes,-1,buffer_send_receive).sendToTarget();
                    if(leakActivity){
                        LeakQunatifirehandler.obtainMessage(7,bytes,-1,buffer_send_receive).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                }
            }
        }

        public void write(byte[] bytes) throws IOException {
            outputStream.write(bytes);

        }
    }
}
