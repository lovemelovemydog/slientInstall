package com.example.anzhuo.studiodemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends Activity {
    String mPackageName = "com.touzile.finance";
    String dir = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        使用静默安装注意：\n
//        1、不能使用私有目录缓存apk文件\n
//        2、没有授权的情况下和一般安装一样 会出现安装界面\n
//        3、只有授权了才能实现静默安装\n


        dir = Environment.getExternalStorageDirectory().getPath();
        //解析包出错 若果使用静默安装的话 不能使用是有目录
//        dir = getDir("mapp", Context.MODE_WORLD_READABLE).getPath();
//        dir = getCacheDir().getPath();
        check();
    }



    void check () {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();

        }
        if (packageInfo == null) {
            // 启用安装新线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("hao", "未安装进行安装");
                    slientInstall(); // 未安装进行安装
                }
            }).start();
        } else {
            Log.e("hao", "已经安装");
        }
    }


    /**
     * 静默安装
     *
     * @return
     */
    public boolean slientInstall() {
        createFile(); // 进行资源的转移 将assets下的文件转移到可读写文件目录下
        File file = new File(dir + "/temp.apk");

        boolean result = false;
        Process process = null;
        OutputStream out = null;
        System.out.println(file.getPath());
        if (file.exists()) {
            System.out.println(file.getPath() + "==");
            try {
                // (这里执行是系统已经开放了root权限，而不是说通过执行这句来获得root权限)
                //如果已经root，但是用户选择拒绝授权,e.getMessage() = write failed: EPIPE (Broken pipe)
                 //如果没有root，,e.getMessage()= Error running exec(). Command: [su] Working Directory: null Environment: null

                process = Runtime.getRuntime().exec("su");
                out = process.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(out);
                dataOutputStream.writeBytes("chmod 777 " + file.getPath()
                        + "\n"); // 获取文件所有权限
                dataOutputStream
                        .writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
                                + file.getPath()); // 进行静默安装命令
                // 提交命令
                dataOutputStream.flush();
                // 关闭流操作
                dataOutputStream.close();
                out.close();
                int value = process.waitFor();

                // 代表成功
                if (value == 0) {
                    Log.e("hao", "安装成功！");
                    result = true;
                } else if (value == 1) { // 失败
                    Log.e("hao", "安装失败！");
                    result = false;
                } else { // 未知情况
                    Log.e("hao", "未知情况！");
                    result = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!result) {
                Log.e("hao", "root权限获取失败，将进行普通安装");
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                result = true;
            }
        }

        return result;
    }

    public void createFile() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = MainActivity.this.getAssets().open("TouzileApp.apk");
            File file = new File(dir + "/temp.apk");
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }




}
