package com.example.xxx.downloaddemo70;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements DownLoadProgressListener {

    private static final String TAG = "MainActivity";
    public static final String apkName = "QQ.apk";
    private String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private Intent serviceIntent;
    private ProgressBar progressBar;
    private TextView textView;
    private File file;
    private DownLoadService downLoadService;
    private DownloadedApkInfo downloadedApkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.text);
        file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName);
    }

    public void queryStatus(View view) {
        if (downLoadService == null) return;
        String msgStatus = downLoadService.queryStatus();
        Toast.makeText(this, msgStatus, Toast.LENGTH_SHORT).show();

    }

    public void startDownLoad(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            startService();
        }
    }

    public void cancleDownLoad(View view) {
        progressBar.setProgress(0);
        textView.setText("0%");
        if (downLoadService != null) {
            unbindService(connection);
            downLoadService = null;
        }
    }

    public void clearApk(View view) {
        if (file.exists()) {
            boolean delete = file.delete();
            if (delete) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadInstalled(View view) {
        downloadedApkInfo = APKUtil.apkInfo(file.getAbsolutePath(), this);
        /**
         * 包名要一致(以qq为例)
         * 版本号要跟服务端最新版本号保持一致才可安装
         */
        if (downloadedApkInfo != null
                && TextUtils.equals(downloadedApkInfo.packageName, "com.tencent.mobileqq")
                && TextUtils.equals(downloadedApkInfo.version, "7.1.0")) {
            //当前包就是服务端最新包，无需下载
            Log.i(TAG, "loadInstalled: 之前已经下载过最新版本，直接安装gogogo");
            APKUtil.installApk(getApplication(), file);
        } else {
            Toast.makeText(this, "本地无最新文件请下载", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    Toast.makeText(this, "请开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startService() {
        serviceIntent = new Intent(this, DownLoadService.class);
        serviceIntent.putExtra("apkUrl", url);
        startService(serviceIntent);
        bindService(serviceIntent, connection, Service.BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownLoadService.DownLoadBinder downLoadBinder = (DownLoadService.DownLoadBinder) service;
            downLoadService = downLoadBinder.getService();
            downLoadService.setDownLoadProgressListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onProgress(int progress) {
        textView.setText(progress + "%");
        progressBar.setProgress(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downLoadService != null) {
            unbindService(connection);
        }
    }
}
