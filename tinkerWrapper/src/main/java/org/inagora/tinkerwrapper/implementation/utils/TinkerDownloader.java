package org.inagora.tinkerwrapper.implementation.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class TinkerDownloader {
    /**
     * 下载文件，并回调下载成功的File对象
     *
     * @param url     下载地址
     * @param dstPath 保存的文件位置，如：/user/rafe/a.patch 即文件保存路径+文件保存名称
     */
    public static void downloadFile(String url, final String dstPath, final TinkerDownloaderListener listener) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TinkerDownloader", "download fail, e == ${e.cause}");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.body() != null) {
                    File dstFile = new File(dstPath);
                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(dstFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    response.body().close();
                    Log.d("TinkerDownloader", "download success");
                    listener.onSuccess(dstFile);
                }
            }
        });
    }
}