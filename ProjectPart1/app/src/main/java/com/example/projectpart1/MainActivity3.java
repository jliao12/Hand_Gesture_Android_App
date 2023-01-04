package com.example.projectpart1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.io.IOUtils;

public class MainActivity3 extends AppCompatActivity {
    VideoView recordvideo;
    Uri videourl;
    private static String gesture;
    private String path = MainActivity2.recordvideo_path.getPath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        recordvideo = findViewById(R.id.recordvideoplay);
        videourl = Uri.parse(getIntent().getExtras().getString("videourl"));


        recordvideo.setVideoURI(videourl);
        recordvideo.start();

        Button replayvideo = (Button) findViewById(R.id.replayvideo);
        replayvideo.setOnClickListener(view -> recordvideo.start());


        switch(MainActivity.stringName){
            case "0": gesture = "Num0";
                break;
            case "1": gesture = "Num1";
                break;
            case "2": gesture = "Num2";
                break;
            case "3": gesture = "Num3";
                break;
            case "4": gesture = "Num4";
                break;
            case "5": gesture = "Num5";
                break;
            case "6": gesture = "Num6";
                break;
            case "7": gesture = "Num7";
                break;
            case "8": gesture = "Num8";
                break;
            case "9": gesture = "Num9";
                break;
            case "Turn on lights": gesture = "LightOn";
                break;
            case "Turn off lights":gesture = "LightOff";
                break;
            case "Turn on fans":gesture = "FanOn";
                break;
            case "Turn off fans":gesture = "FanOff";
                break;
            case "Increase fan speed":gesture = "FanUp";
                break;
            case "Decrease fan speed":gesture = "FanDown";
                break;
            case "Set Thermostat to specified temperature":gesture = "SetThemo";
                break;
        }


        Button upload = (Button) findViewById(R.id.upload);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverURL = "http://192.168.1.103:5000/";
                try {
                    uploadFile(serverURL,videourl,gesture);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity3.this, "Video Uploaded", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void uploadFile(String serverURL, Uri videoUri, String gesture) throws FileNotFoundException {

        OkHttpClient okHttpClient = new OkHttpClient();
//                .connectTimeout(10000, TimeUnit.MILLISECONDS)
//                .readTimeout(10000, TimeUnit.MILLISECONDS)
//                .writeTimeout(10000, TimeUnit.MILLISECONDS).build();

        ContentResolver contentResolver = MainActivity3.this.getContentResolver();
        final String contentType = contentResolver.getType(videoUri);
        final AssetFileDescriptor fd = contentResolver.openAssetFileDescriptor(videoUri, "r");
        if (fd == null) {
            throw new FileNotFoundException("could not open file descriptor");
        }
        RequestBody videoFile = new RequestBody() {
            @Override
            public long contentLength() {
                return fd.getDeclaredLength();
            }
            @Override
            public MediaType contentType() {
                return MediaType.parse(contentType);
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream is = fd.createInputStream()) {

                    sink.writeAll(Okio.buffer(Okio.source(is)));
                }
            }
        };
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", gesture, videoFile)
                .build();
        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                try {
                    fd.close();
                } catch (IOException ex) {
                    e.addSuppressed(ex);
                }
                Log.e("UPLOAD", "failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                fd.close();
                Log.e("UPLOAD", "success");

            }
        });
    }


//    private String getPath(Uri uri) {
//        String[] projection = {MediaStore.Video.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
}
