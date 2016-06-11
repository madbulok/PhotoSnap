package test;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jedai.photosnapper.DownloadPhoto;
import com.jedai.photosnapper.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamAct extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;

    File photoFile;
    File pictures;

    Button shotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // наше SurfaceView имеет имя SurfaceView01
        surfaceView= (SurfaceView) findViewById(R.id.ss);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // кнопка имеет имя Button01
        shotBtn = (Button) findViewById(R.id.btnTakePicture);
        shotBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
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
                    /*Date time = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    String name = String.valueOf(dateFormat.format(time)) + "jpg";
                    photoFile = new File(pictures, name);

                    new SaveInBackground(this, pictures, name).execute()


                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(data);
                    fos.close();

                    new DownloadPhoto(getApplicationContext(), photoFile).execute();
*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v)
    {
        if (v == shotBtn)
        {
            // либо делаем снимок непосредственно здесь
            // 	либо включаем обработчик автофокуса

            //camera.takePicture(null, null, null, this);
            camera.autoFocus(this);
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            // если удалось сфокусироваться, делаем снимок
            camera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try
        {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        int previewSurfaceWidth = surfaceView.getWidth();
        int previewSurfaceHeight = surfaceView.getHeight();

        LayoutParams lp = surfaceView.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            // портретный вид
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
            ;
        }
        else
        {
            // ландшафтный
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        surfaceView.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        new SaveInBackground(getApplicationContext(), pictures).execute(data);
        camera.startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }
}


class SaveInBackground extends AsyncTask<byte[], String , String>{

    Context context;
    File directory;
    String name;
    private volatile long tempFile;

    public SaveInBackground(Context context, File directory) {
        this.context = context;
        this.directory = directory;
    }

    @Override
    protected String doInBackground(byte[]... paramArrayOfByte) {

        if(!directory.exists()){
            directory.mkdir();
            Toast toast = Toast.makeText(context, "Файл создан!", Toast.LENGTH_LONG);
            toast.show();
        }
        FileOutputStream os = null;
        try {
            Date time = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            name = dateFormat.format(time);
            Log.d("direcTORY:", String.valueOf(directory));
            tempFile = System.currentTimeMillis();
            os = new FileOutputStream(String.format(directory + "/%d.jpg", tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

            os.write(paramArrayOfByte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        File file = new File(directory + "/" + String.valueOf(tempFile) + ".jpg");
        new DownloadPhoto(context, file).execute();

    }
}