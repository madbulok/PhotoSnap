package com.jedai.photosnapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class CameraPreview extends Activity {

    SurfaceView surfaceView;
    Camera camera;

    public static TextView textView;
    public static TextView time_show;

    volatile File photoFile;
    File dir;

    private Handler takePhoto = new Handler();
    private int seconds;
    private int timeToPhoto = 61;

    /*DropBox information*/
    private static final String APP_KEY = "2802eumntkumxpy";
    private static final String APP_SECRET = "qbl5bkd6m7g3mpm";
    private DropboxAPI<AndroidAuthSession> dropboxApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        textView = (TextView) findViewById(R.id.text);
        time_show = (TextView) findViewById(R.id.timer);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
        dropboxApi.getSession().startOAuth2Authentication(CameraPreview.this);

        camera = Camera.open();

        dir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!dir.exists()){
            Toast file = Toast.makeText(this, "Папка не создана!", Toast.LENGTH_LONG);
            file.show();
        }

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                    startHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

    }

    void startHandler() {
        takePhoto.post(new Runnable() {
            @Override
            public void run() {
                seconds++;
                int timer = timeToPhoto - seconds;
                time_show.setText(String.valueOf(timer)); //отсчет времени до снимка
                if (seconds == timeToPhoto) {
                    startPhotoSnap();
                    seconds = 0;
                }

                takePhoto.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dropboxApi.getSession().authenticationSuccessful()) {
            try {
                dropboxApi.getSession().finishAuthentication();
                String accessToken = dropboxApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    public void startPhotoSnap(){
        if (dropboxApi.getSession().authenticationSuccessful()) {
            try {
                dropboxApi.getSession().finishAuthentication();
                String accessToken = dropboxApi.getSession().getOAuth2AccessToken();
                takePicture();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    public void takePicture () {
        try {
            camera.startPreview();
        }catch (NullPointerException e ){
            e.printStackTrace();
            Toast camera = Toast.makeText(getApplicationContext(), "Камера не найдена!", Toast.LENGTH_LONG);
            camera.show();
        }

        camera.takePicture(null, null, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                try {
                    Date time = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    String name = String.valueOf(dateFormat.format(time)) + ".jpg";
                    photoFile = new File(dir, name);

                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(data);
                    fos.close();

                    //new UploadPhoto(getApplicationContext(), dropboxApi, name, photoFile).execute();
                    new DownloadPhoto(getApplicationContext(), photoFile).execute();
                }catch (FileNotFoundException e){
                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }catch (NullPointerException e){
                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null)
            camera.release();
        camera = null;
        takePhoto.removeCallbacksAndMessages(null);
    }
}