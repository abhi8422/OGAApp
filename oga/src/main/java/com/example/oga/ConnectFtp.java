package com.example.oga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

public class ConnectFtp extends AppCompatActivity implements View.OnClickListener {
    public static FtpServerConnection ftpclient = null;
    private static final String TAG = "ConnectFtpServer";
    private Button download;

    private EditText ftp_url,ftp_port,ftp_usr,ftp_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ftp);

        download=findViewById(R.id.btn_download);
        ftp_url=findViewById(R.id.ftp_url);
        ftp_port=findViewById(R.id.ftp_port);
        ftp_usr=findViewById(R.id.ftp_usr);
        ftp_pass=findViewById(R.id.ftp_pass);
        download.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},101);
        }
        ftpclient = new FtpServerConnection();
        findViewById(R.id.btn_connect_ftp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url= String.valueOf(ftp_url.getText());
                String usr= String.valueOf(ftp_usr.getText());
                String pass= String.valueOf(ftp_pass.getText());
                int port=Integer.parseInt(String.valueOf(ftp_port.getText()));
                new FtpTask(url,port,usr,pass).execute();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && requestCode==101){
            Toast.makeText(this,"Permission is granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class FtpTask extends AsyncTask<Void, Void, FTPClient> {
        boolean status = false;

        String url,usr,pass;
        int port;

        public FtpTask( String url, int port, String usr, String pass) {
            this.url = url;
            this.port = port;
            this.usr = usr;
            this.pass = pass;
        }

        protected FTPClient doInBackground(Void... args) {

            status = ftpclient.ftpConnect(url,usr,pass, port);
            if (status == true) {
                Log.d(TAG, "Connection Success");
               // Toast.makeText(ConnectFtp.this,"Connection is Sucessful",Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Connection failed");
                //Toast.makeText(ConnectFtp.this,"Connection is Failed",Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(FTPClient ftpClient) {
            super.onPostExecute(ftpClient);
            if (status == true) {
                Toast.makeText(getApplicationContext(), "Connection Success",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Connection failed",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
