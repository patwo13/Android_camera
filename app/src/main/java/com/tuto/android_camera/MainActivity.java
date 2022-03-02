package com.tuto.android_camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    ImageView imageView;
    boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.CAMERA},1);
        }
        else{
            permission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                permission = true;
            }else{
                permission = false;
                Toast.makeText(this, "Please allow permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClickSurfaceView(View view){
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        if(permission){
            startActivityForResult(intent, 1);
        }
        else{
            Toast.makeText(this, "Please allow permission to proceed", Toast.LENGTH_LONG).show();
        }
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
