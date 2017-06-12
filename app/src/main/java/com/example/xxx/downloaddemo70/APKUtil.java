package com.example.xxx.downloaddemo70;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * Created by "MrKong" on 2017/6/9.
 */

public class APKUtil {

    /**
     * 获取已经下载的apk包文件的信息
     *
     * @param absPath
     * @param context
     * @return
     */
    public static DownloadedApkInfo apkInfo(String absPath, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
            String packageName = appInfo.packageName; // 得到包名
            String version = pkgInfo.versionName; // 得到版本信息
            int versionCode = pkgInfo.versionCode;
            String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);
            Log.e("apkInfo", pkgInfoStr);
            DownloadedApkInfo downloadedApkInfo = new DownloadedApkInfo(packageName, version, versionCode, appName);
            return downloadedApkInfo;
        } else {
            return null;
        }
    }

    /**
     * 安装apk，兼容Android N
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intentInstall = new Intent();
        intentInstall.setAction(Intent.ACTION_VIEW);
        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //above7.0
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".provider", file);
            //给目标应用设置权限（必要）
            intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            //below7.0
            uri = Uri.fromFile(file);
        }
        intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intentInstall);
    }
}
