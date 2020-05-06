package com.example.oga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {

    private static final String TAG = "ImageAdapter";
    private Context context;
    private ArrayList<String> imgPath=new ArrayList<>();
    public ImageAdapter(Context context,ArrayList<String> imgPath) {
        this.context = context;
        this.imgPath=imgPath;
    }

    @Override
    public int getCount() {
        return imgPath.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView=new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        File curFile = new File(imgPath.get(position));
        Bitmap bitmap= BitmapFactory.decodeFile(curFile.getAbsolutePath());
        Bitmap rotatedBitmap;
        try {
            ExifInterface exif = new ExifInterface(curFile.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(rotatedBitmap);
        }catch(IOException ex){
            Log.e(TAG, "Failed to get Exif data", ex);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}
