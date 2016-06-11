package com.jedai.photosnapper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;


public class DownloadPhoto extends AsyncTask<String, Void, String>{

    Context context;
    File file;
    String url = "http://test.aimodels.ru/upload.php";

    public DownloadPhoto(Context context, File file) {
        this.context = context;
        this.file = file;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(url);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart("userfile", cbFile);
        httppost.setEntity(mpEntity);
        System.out.println("executing request " + httppost.getRequestLine());

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());
        if (resEntity != null) {
            try {
                System.out.println(EntityUtils.toString(resEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resEntity != null) {
            try {
                resEntity.consumeContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpclient.getConnectionManager().shutdown();

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //CameraPreview.textView.setText("Последняя загрузка - " + file.getName());
        Toast toast = Toast.makeText(context, "UPLOAD!!", Toast.LENGTH_LONG);
        toast.show();
    }
}

