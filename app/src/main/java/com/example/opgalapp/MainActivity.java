package com.example.opgalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
DrawerLayout drawerLayout;
public static boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.home_drawer);
        configureNavDrawer();
        if(flag){
            TextView textView=findViewById(R.id.text_message);
            Button btnsend=findViewById(R.id.btn_sndCamera);
            btnsend.setVisibility(View.VISIBLE);
            textView.setText("Reding Data from Bluetooth device(TVA/Quantifire)");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Context context=getApplicationContext();
        MenuInflater inflater=new MenuInflater(context);
        inflater.inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.switch_ble:
                Toast.makeText(getApplicationContext(),"Switching Bluetooth Connection",Toast.LENGTH_SHORT).show();
                break;
           /* case R.id.switch_wifi:
                Toast.makeText(getApplicationContext(),"Switching Wifi Connection",Toast.LENGTH_SHORT).show();
                break;*/
        }
        return true;
    }

    private void configureNavDrawer() {
        drawerLayout = findViewById(R.id.home_drawer);
        final FrameLayout frameLayout=findViewById(R.id.content_frame);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuId = item.getItemId();
                if (menuId == R.id.bt_config){
                    frameLayout.removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new BluetoothFragment(),"Ble_frag").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else if (menuId == R.id.wifi){
                    frameLayout.removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new WifiFragment()).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else if(menuId==R.id.senddt_ogi){
                    frameLayout.removeAllViews();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new SendDataCameraFragment()).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    finish();
                }
                return true;
            }
        });
    }


    public void sendToCamera(View view) {
        Toast.makeText(getApplication(),"Sending Data To Camera Over Bluetooth",Toast.LENGTH_SHORT).show();
    }
}
