package com.tuto.android_camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder surfaceHolder;

    public ShowCamera(Context context, Camera camera){
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
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

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
