package com.tuto.android_camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    int cameraID = 1;

    FrameLayout frameLayout;
    ShowCamera showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surfaceView2);
        //frameLayout = findViewById(R.id.frameLayout);


        //surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.addCallback(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Log.e("Surface", "dalam if requestPermission");
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }else{
            Log.e("Surface", "dalam else permission");

            //open camera
            //camera = Camera.open(cameraID);

            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);

            //showCamera = new ShowCamera(this, camera);
            //frameLayout.addView(showCamera);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e("Surface", "dalam permission granted");

                //camera = Camera.open(cameraID);

                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);

                //showCamera = new ShowCamera(this, camera);
                //frameLayout.addView(showCamera);
            }
            else {
                Toast.makeText(this, "Please Enable Camera Permissions", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void captureImage(View v){
        if(camera != null){
            camera.takePicture(null,null,mPictureCallback);
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            try {
                byte[] imageData = Setting.rotateImageData(CameraActivity.this, bytes, cameraID);

                Intent intent = new Intent(CameraActivity.this, CaptureActivity.class);
                intent.putExtra("ImageData", imageData);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        Log.e("Surface", "dalam surfaceCreated");
        camera = Camera.open(cameraID);
        Camera.Parameters parameters = camera.getParameters();

        //get camera size
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size mSize = null;

        for(Camera.Size size : sizes){
            mSize = size;
        }

        //change orientation of the camera
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation", "potrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }else{
            parameters.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }

        parameters.setPictureSize(mSize.width, mSize.height);
        //set camera parameter
        camera.setParameters(parameters);

        try {
            //set camera preview to surfaceHolder
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e("Surface", "dalam surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.e("Surface", "dalam surfaceDestroyed");
        camera.stopPreview();
        camera.release();
    }
}
