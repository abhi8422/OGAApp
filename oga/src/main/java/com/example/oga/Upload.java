package com.example.oga;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static com.example.oga.LeakQuantifire.max;
import static com.example.oga.MainActivity.barcodenumber;
import static com.example.oga.MainActivity.currentPhotoPath;

public class Upload extends AppCompatActivity {
TextView barcode,reading;
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        barcode=findViewById(R.id.upl_barcode);
        imageView=findViewById(R.id.upl_imgview);
        reading=findViewById(R.id.upl_reading);
        barcode.setText("Component Barcode:"+barcodenumber);
        if(currentPhotoPath!=null){
            File image = new File(currentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            Bitmap rotatedBitmap;
            try {
                ExifInterface exif = new ExifInterface(image.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(rotatedBitmap);
            }catch(IOException ex){
                Toast.makeText(this,"Failed to get Exif data",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
        reading.setText("Component Reading:"+max.toString());

    }
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
    public void Upload(View view){
Toast.makeText(this,"Uploading data to server",Toast.LENGTH_LONG).show();
    }
}
