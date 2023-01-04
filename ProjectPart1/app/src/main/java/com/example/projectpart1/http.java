package com.example.projectpart1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.FileUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class http {
    // build a client
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000,TimeUnit.MILLISECONDS)
            .writeTimeout(10000, TimeUnit.MILLISECONDS).build();

    public static void postFile(Context context, String url, okhttp3.Callback callback, Uri videoUri, File file) throws FileNotFoundException {
        // get content
        ContentResolver contentResolver = context.getContentResolver();
        final String contentType = contentResolver.getType(videoUri);
        final AssetFileDescriptor fd = contentResolver.openAssetFileDescriptor(videoUri, "r");
        if (fd == null) {
            throw new FileNotFoundException("could not open file descriptor");
        }

        RequestBody videoFile = new RequestBody() {
            @Override public long contentLength() { return fd.getDeclaredLength(); }
            @Override public MediaType contentType() { return MediaType.parse(contentType); }
            @Override public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream is = fd.createInputStream()) {
                    //https://segmentfault.com/a/1190000038470543
                    //sink -- OUTPUTSTREAM
                    sink.writeAll(Okio.buffer(Okio.source(is)));
                }
            }
        };
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "gesture", videoFile)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


}
