package com.jedai.photosnapper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by artem on 09.06.16.
 */
public class DropBoxActivity extends Activity {

    private static final String TAG = "log";


    private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;
    private static final String APP_KEY = "2802eumntkumxpy";
    private static final String APP_SECRET = "qbl5bkd6m7g3mpm";

    private DropboxAPI<AndroidAuthSession> dropboxApi;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_box);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dropboxApi = new DropboxAPI<AndroidAuthSession>(session);

        dropboxApi.getSession().startOAuth2Authentication(DropBoxActivity.this);


    }

    protected void onResume() {
        super.onResume();

        if (dropboxApi.getSession().authenticationSuccessful()) {
            try {
                dropboxApi.getSession().finishAuthentication();

                String accessToken = dropboxApi.getSession().getOAuth2AccessToken();
                Log.d("---", accessToken);
                //new UploadPhoto(this, dropboxApi, name).execute();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }


        }
        if(dropboxApi.getSession().authenticationSuccessful()){
            Toast toast = Toast.makeText(getApplicationContext(), "ДААААА!", Toast.LENGTH_LONG);
            toast.show();
        }

    }

}