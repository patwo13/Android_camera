package com.tuto.android_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class CaptureActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imageView = findViewById(R.id.images);
        Intent intent = getIntent();
        //byte[] imageData = intent.getByteArrayExtra("ImageData");
        byte[] imageData = (byte[]) intent.getExtras().get("ImageData");
        Log.e("ImageData", "Size - "+ imageData.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        //imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false));
        imageView.setImageBitmap(bitmap);
    }
}
