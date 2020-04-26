package com.example.oga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.oga.ConnectFtp.ftpclient;

public class VideoDownload extends AppCompatActivity {
    private ListView listView,dwnllistview;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_download);
        listView=findViewById(R.id.list_view);
        dwnllistview=findViewById(R.id.list_view_dwnl);
        if(ftpclient!=null){
            if(ftpclient.getFilesNames().size()!=0){
                arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,ftpclient.getFilesNames());
                listView.setAdapter(arrayAdapter);
            }else {
                Toast.makeText(this,"FTP Server Problem",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void DownlaodFiles(View view) {
        Toast.makeText(this,"Downloading files from server",Toast.LENGTH_LONG).show();
        dwnllistview.setAdapter(arrayAdapter);
    }

    public void SaveVideo(View view) {
        startActivity(new Intent(this,LeakQuantifire.class));
    }
}
