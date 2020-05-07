package com.example.oga;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public boolean captured = false;
    ImageView Camera_click;
    Bitmap rotatedBitmap;
    EditText barcode;
    TextView GPS_Coordinates;
    public  static float LAT,LNG;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imageview;
    public static String currentPhotoPath;
    public static String barcodenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcode=findViewById(R.id.edt_barcode);
        GPS_Coordinates=findViewById(R.id.gps_coordinates);
        imageview=findViewById(R.id.imageview);
        Camera_click=findViewById(R.id.camera_click);
        Dexter.withActivity(this).withPermissions(READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    /* Location API Calling*/
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                    /* Location API Calling*/

                    /* Location Changes */
                    getLastLocation();
                    /* Location Changes */
                }
                if (report.isAnyPermissionPermanentlyDenied()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Permissions");
                    builder.setMessage("This app needs these permissions. You can grant them in app settings.");
                    builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            openSettings();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            dialog.cancel();
                            finish();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();

        Camera_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // startActivityForResult(takePictureIntent,111);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File imageFile = null;
                        try {
                            imageFile = File.createTempFile(
                                    imageFileName,  /* prefix */
                                    ".jpg",         /* suffix */
                                    storageDir      /* directory */
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Save a file: path for use with ACTION_VIEW intents
                        currentPhotoPath = imageFile.getAbsolutePath();
                        System.out.println(barcode.getText().toString());
                        if (imageFile != null ) {
                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    "com.example.oga",
                                    imageFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 12);
                        }
                    }
            }
        });

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED)){
            RequestPermission();
        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (captured){
                    final Dialog builder = new Dialog(MainActivity.this);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    builder.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                uri=Uri.fromFile(new File(OP.getAbsolutePath()));
                    BitmapFactory.Options opts;
                    builder.setContentView(R.layout.dialog_layout);
                    ImageButton close=builder.findViewById(R.id.btnClose);

                    ImageView img = builder.findViewById(R.id.Img);
                    img.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
                    img.getLayoutParams().width=ViewGroup.LayoutParams.WRAP_CONTENT;
                    img.setAdjustViewBounds(false);

//                img.setImageURI(uri);
                    img.setImageBitmap(rotatedBitmap);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });

                    builder.show();
                }
                else {
                    Toast.makeText(MainActivity.this, "No Image Captured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void RequestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.BLUETOOTH},101);
    }

    public void barcodeShow(View view) {
        IntentIntegrator integrator = new IntentIntegrator (MainActivity.this);
        integrator.setCaptureActivity(ScannerActivity.class);
        integrator.initiateScan();
    }



    public void Save(View view) {
       barcodenumber=barcode.getText().toString();
        startActivity(new Intent(this,VideoDownload.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            getLastLocation();
            barcodenumber=result.getContents();
            barcode.setText(result.getContents());
        }
        if(requestCode==12){
            File curFile = new File(currentPhotoPath);
            Bitmap bitmap= BitmapFactory.decodeFile(curFile.getAbsolutePath());
                try {
                    ExifInterface exif = new ExifInterface(curFile.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);
                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    imageview.setVisibility(View.VISIBLE);
                    captured=true;
                    imageview.setImageBitmap(rotatedBitmap);
                }catch(IOException ex){
                    Log.e(TAG, "Failed to get Exif data", ex);
            }
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cnftp:
                startActivity(new Intent(this,ConnectFtp.class));
                break;
            case R.id.cnble:
                startActivity(new Intent(this,Bluetooth.class));

        }
        return true;
    }


    /* Changes for the GPS Co-ordinates */
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()){
            if (isLocationEnabled()){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null){
                            requestNewLocation();
                        }else {
                            LAT = Float.parseFloat((location.getLatitude()+""));
                            LNG = Float.parseFloat((location.getLongitude()+""));
                            System.out.println("GetLastLocation"+LAT+" "+LNG);
                            GPS_Coordinates.setText(LAT+" LAT "+LNG+" LNG");
                        }
                    }
                });
            }
            else {
                displayLocationSettingsRequest(getApplicationContext());
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }
        else {
            requestPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000 / 2);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        settingsBuilder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> settingsResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,settingsBuilder.build());
        settingsResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            private String TAG;

            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(MainActivity.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e){
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            Location lastLocation = locationResult.getLastLocation();
            LAT = Float.parseFloat((lastLocation.getLatitude()+""));
            LNG = Float.parseFloat((lastLocation.getLongitude()+""));
            System.out.println("CallBack"+LAT+" "+LNG);
            GPS_Coordinates.setText(LAT+" LAT "+LNG+" LNG");

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,101);
    }

}
