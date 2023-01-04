package com.example.projectpart1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity2 extends AppCompatActivity {
    private static int video_record = 101;
    public static Uri recordvideo_path;
    private VideoView videoView;
    private String videopath;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        videoView = findViewById(R.id.videoplay);
        switch (MainActivity.stringName) {
            case "0":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h0;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "1":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h1;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "2":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h2;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "3":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h3;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "4":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h4;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "5":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h5;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "6":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h6;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "7":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h7;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "8":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h8;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "9":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.h9;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Turn on lights":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hlighton;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Turn off lights":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hlightoff;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Turn on fans":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hfanon;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Turn off fans":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hfanoff;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Increase fan speed":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hincreasefanspeed;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Decrease fan speed":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hdecreasefanspeed;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
            case "Set Thermostat to specified temperature":
                videopath = "android.resource://" + getPackageName() + "/" + R.raw.hsetthermo;
                videoView.setVideoURI(Uri.parse(videopath));
                break;
        }
        videoView.start();
        TextView txv2 = (TextView) findViewById(R.id.txv2);
        txv2.setText("You Selected " + MainActivity.stringName);

        Button playvideo = (Button) findViewById(R.id.playvideo);
        playvideo.setOnClickListener(view -> videoView.start());

        if (iscamera()) {
            Log.i("Record_Video", "Camera Detected");
           camerapermission();
        } else {
            Log.i("Record_Video", "Camera Not Detected");
        }

    }

    public void recordthevideo(View view) {
        /*Intent gotoactivity3  = new Intent(MainActivity2.this, MainActivity3.class);
        startActivity(gotoactivity3);*/
        recordvideo();
    }

    private boolean iscamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    private void camerapermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            int camera_permission = 100;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, camera_permission);
        }
    }

    private void recordvideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        startActivityForResult(intent, video_record);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == video_record) {
            if (resultCode == RESULT_OK) {
                recordvideo_path  = data.getData();
                Log.i("Record_Video", "Video Record "+recordvideo_path);
/*                videoView.setVideoURI(recordvideo_path);
                videoView.start();*/
                //Log.i("Record_Video", "Video playing: ");
                Intent gotoactivity3  = new Intent(MainActivity2.this, MainActivity3.class);
                gotoactivity3.putExtra("videourl",recordvideo_path.toString());
                startActivity(gotoactivity3);
            }
            else if (resultCode == RESULT_CANCELED){
                Log.i("Record_Video", "Video Not Record");
            }
            else {
                Log.i("Record_Video", "error");
            }
        }
    }
}