package com.jedai.photosnapper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class UploadPhoto extends AsyncTask<String, Void, String> {

    private DropboxAPI dropboxApi;
    private Context context;

    File sendPicture;
    String name;

    public UploadPhoto(Context context, DropboxAPI dropboxApi, String name, File file) {
        this.context = context.getApplicationContext();
        this.dropboxApi = dropboxApi;
        this.name = name;
        this.sendPicture = file;
    }

    @Override
    protected String doInBackground(String... params) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sendPicture);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DropboxAPI.Entry response = null;
        try {
            response = dropboxApi.putFile(sendPicture.getPath(), inputStream,
                    sendPicture.length(), null, null);
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Toast toast = Toast.makeText(context, "ЗАГРУЖЕНО!", Toast.LENGTH_LONG);
        toast.show();

        CameraPreview.textView.setText("Последняя загрузка - " + name);

        if (sendPicture.exists()){
            sendPicture.delete();
        }

    }
}
