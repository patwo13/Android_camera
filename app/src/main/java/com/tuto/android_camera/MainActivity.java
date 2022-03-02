package com.tuto.android_camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity{

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

    }

    public void onClickSurfaceView(View view){
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onClickIntent(View view){
        startActivity(new Intent(this, SurfaceViewActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                byte[] imageData = (byte[]) data.getExtras().get("ImageData");
                Log.e("Intent", "Size - "+imageData.length);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
