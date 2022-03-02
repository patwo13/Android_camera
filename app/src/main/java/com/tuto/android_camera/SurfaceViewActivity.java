package com.tuto.android_camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.List;

public class SurfaceViewActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private android.hardware.Camera camera;
    private Button btn_capture;
    private Button btn_flash;
    private Button btn_flip;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);

        //create camera surfaceview
        cameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
        btn_flip = findViewById(R.id.flipCamera);
        btn_flash = findViewById(R.id.flash);
        btn_capture = findViewById(R.id.captureImage);
        surfaceView = findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        btn_capture.setOnClickListener(this);
        btn_flash.setOnClickListener(this);
        btn_flip.setOnClickListener(this);

        //keep screen on if activity still running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //check number of camera in the device
        if(android.hardware.Camera.getNumberOfCameras() > 1){
            btn_flip.setVisibility(View.VISIBLE);
        }

        //check whether device has flash
        if(!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)){
            btn_flash.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if(!openCamera(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)){
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(this, "Camera Info", "Error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.ic_launcher_background);
        if(title != null){
            dialog.setTitle(title);
        }
        else{
            dialog.setTitle("Information");
        }

        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;
    }


    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        //stop whichever camera is running and make camera object null
        releaseCamera();
        try{
            camera = Camera.open(cameraId);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(camera != null){
            try{
                setUpCamera(camera);
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

    private void setUpCamera(Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();

        int degree = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            Log.e("Rotation", "Rotation 1 - "+rotation+ "   Orientation - "+ info.orientation + "   Degree - "+ degree);
            //frontfacing
            rotation = (info.orientation + degree) % 360;
            Log.e("Rotation", "Rotation 1 - "+rotation+ "   Orientation - "+ info.orientation + "   Degree - "+ degree);
            rotation = (360 - rotation) % 360;
            Log.e("Rotation", "Rotation 1 - "+rotation+ "   Orientation - "+ info.orientation + "   Degree - "+ degree);

        }else{
            Log.e("Rotation", "Rotation 1 - "+rotation+ "   Orientation - "+ info.orientation + "   Degree - "+ degree);
            //back-facing
            rotation = (info.orientation - degree + 360) % 360;
            Log.e("Rotation", "Rotation 2 - "+rotation+ "   Orientation - "+ info.orientation + "   Degree - "+ degree);
        }
        camera.setDisplayOrientation(rotation);
        Camera.Parameters params = camera.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if(focusModes != null){
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);


    }

    private void showFlashButton(Camera.Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        btn_flash.setVisibility(showFlash ? View.VISIBLE
                : View.VISIBLE);
    }

    private void releaseCamera() {
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
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                takeImage();
                break;

            default:
                break;
        }
    }

    private void takeImage() {
        camera.takePicture(null, null, new Camera.PictureCallback() {

            private File imageFile;

            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                try{
                    //convert byte array into bitmap
                    Bitmap loadedBitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);

                    //rotate IMage
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(loadedBitmap, 0,
                            0, loadedBitmap.getWidth(), loadedBitmap.getHeight(), rotateMatrix, false);
                    String state = Environment.getExternalStorageState();

                    File folder = null;
                    if(state.contains(Environment.MEDIA_MOUNTED)){
                        folder = new File(Environment.getExternalStorageDirectory() + "/Demo");
                    }
                    else{
                        folder = new File(Environment.getExternalStorageDirectory() + "/Demo");
                    }

                    boolean success = true;
                    if(!folder.exists()){
                        success = folder.mkdirs();
                    }
                    if(success){
                        java.util.Date date = new java.util.Date();
                        imageFile = new File(folder.getAbsolutePath()
                                + File.separator
                                + new Timestamp(date.getTime()).toString()
                                + "Image.jpg");
                        imageFile.createNewFile();
                    }else{
                        Toast.makeText(getBaseContext(), "Image not saved", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    //save image into gallery
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    fileOutputStream.close();

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

                    SurfaceViewActivity.this.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void flipCamera() {
        int id = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK);
        if(!openCamera(id)){
            alertCameraDialog();
        }
    }

    private void flashOnButton() {
        if(camera != null){
            try{
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(!flashmode ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                flashmode = !flashmode;
            }catch (Exception e){

            }
        }
    }
}
