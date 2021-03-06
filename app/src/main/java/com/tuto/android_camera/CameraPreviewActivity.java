package com.tuto.android_camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class CameraPreviewActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    int cameraId = 1;

    ImageView btnCapture;
    ImageView btnFlip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        btnCapture = findViewById(R.id.capture);
        btnFlip = findViewById(R.id.flip);

        btnCapture.setOnClickListener(this);
        btnFlip.setOnClickListener(this);

        surfaceView = findViewById(R.id.cameraPreview);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);


    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if(!openCamera(cameraId)){
            Toast.makeText(this, "Error to open camera", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        releaseCamera();
    }


    private boolean openCamera(int id){
        boolean result = false;
        cameraId = id;

        if(camera != null) {
            //stop camera in case there is running camera
            releaseCamera();
        }

        try{
            camera = Camera.open(cameraId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(camera != null)
        {
            try{
                //set camera setting
                cameraSetting();
                camera.setErrorCallback(new Camera.ErrorCallback() {
                    @Override
                    public void onError(int i, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            }catch (Exception e){
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }

        return result;
    }

    private void cameraSetting(){

        Camera.Parameters parameters = camera.getParameters();

        //get camera size
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size msize = null;

        for(Camera.Size size : sizes){
            msize = size;
        }

        //change orientation of the camera
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation", "potrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }else{
            parameters.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }

        parameters.setPictureSize(msize.width, msize.height);

        //set camera parameter
        camera.setParameters(parameters);
    }

    private void releaseCamera(){
        try{
            if(camera != null){
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error", e.toString());
            camera = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.capture:
                captureImage();
                break;
            case R.id.flip:
                flipCamera();
                break;

            default:
                break;
        }
    }

    public void captureImage(){
        if(camera != null){
            camera.takePicture(null, null, mPictureCallback);
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try{
                byte[]  imageData = Setting.rotateImageData(CameraPreviewActivity.this, bytes, cameraId);
                if(imageData.length > 0){
                    //Log.e("onPictureTaken", "Success");

                    Intent intent = new Intent();
                    intent.putExtra("ImageData", imageData);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public void flipCamera(){

    }
}
