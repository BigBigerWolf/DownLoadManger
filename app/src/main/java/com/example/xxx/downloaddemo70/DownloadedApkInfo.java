package com.example.xxx.downloaddemo70;

/**
 * Created by "MrKong" on 2017/6/9.
 */

public class DownloadedApkInfo {

    public DownloadedApkInfo(String packageName, String version, int versionCode, String appName) {
        this.packageName = packageName;
        this.version = version;
        this.appName = appName;
        this.versionCode = versionCode;
    }

    public String packageName;
    public String version;
    public String appName;
    public int versionCode;

    @Override
    public String toString() {
        return "DownloadedApkInfo{" +
                "packageName='" + packageName + '\'' +
                ", version='" + version + '\'' +
                ", appName='" + appName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
