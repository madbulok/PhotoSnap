package test;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.jedai.photosnapper.DownloadPhoto;
import com.jedai.photosnapper.R;

public class CamAct extends Activity {

    SurfaceView surfaceView;
    Camera camera;

    TextView textView;

    File photoFile;
    File pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.test);

        pictures = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);



        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
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

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null)
            camera.release();
        camera = null;
    }

    public void onClickPicture(View view) {
        camera.takePicture(null, null, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    Date time = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    String name = String.valueOf(dateFormat.format(time)) + "jpg";
                    photoFile = new File(pictures, name);

                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(data);
                    fos.close();

                    new DownloadPhoto(getApplicationContext(), photoFile).execute();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}