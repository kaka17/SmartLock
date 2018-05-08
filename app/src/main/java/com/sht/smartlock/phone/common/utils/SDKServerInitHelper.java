package com.sht.smartlock.phone.common.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

/**
 * 可以通过此工具栏制定SDK访问服务器地址.
 * 1、写入配置文件到SDK应用目录
 * SDKServerInitHelper.initServer(this);
 * 2、将已经存在的服务器配置文件拷贝到SDK应用目录
 * SDKServerInitHelper.initServerFromLocal(this , "/sdcard/ECDemo_Msg/sdk_server_config.xml");
 */
public class SDKServerInitHelper {

    public static final String SDK_SERVER_NAME = "sdk_server_config";
    public static final String SERVER_XML =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<ServerAddr version=\"2\">" +
                "<Connector>" +
                    "<server>" +
                        "<host>123.57.33.80</host>" +
                        "<port>8085</port>" +
                    "</server>" +
                    "<server>" +
                        "<host>123.57.215.63</host>" +
                        "<port>8085</port>" +
                    "</server>" +
                "</Connector>" +
                "<LVS>" +
                    "<server>" +
                        "<host>123.56.135.81</host>" +
                        "<port>8888</port>" +
                    "</server>" +
                "</LVS>" +
                "<FileServer>" +
                    "<server>" +
                        "<host>123.57.215.95</host>" +
                        "<port>8090</port>" +
                    "</server>" +
                "</FileServer>" +
            "</ServerAddr>";

    /**
     * 初始化SDK服务器配置文件
     * @param ctx Android应用上下文
     * @return 是否成功
     */
    public static boolean initServer(Context ctx) {
        if(ctx == null) {
            return false;
        }
        String serverFolder = getSDKServerFolder(ctx);
        if(TextUtils.isEmpty(serverFolder)) {
            return false;
        }
        return copyFile(serverFolder , SDK_SERVER_NAME , SERVER_XML.getBytes()) == 0;
    }

    /**
     * 判断配置文件是否存在
     * @param ctx
     * @return
     */
    public static boolean isServerConfigExist(Context ctx) {
        if(ctx == null) {
            throw new IllegalArgumentException("Context can't null.");
        }
        String sdkServerFolder = getSDKServerFolder(ctx);
        return new File(sdkServerFolder , SDK_SERVER_NAME).exists();
    }

    /**
     * 删除配置文件
     * @param ctx
     * @return
     */
    public static boolean delServerConfig(Context ctx) {
        if(ctx == null) {
            throw new IllegalArgumentException("Context can't null.");
        }
        String sdkServerFolder = getSDKServerFolder(ctx);
        File file = new File(sdkServerFolder, SDK_SERVER_NAME);
        if(file.exists()) {
            file.delete();
        }
        return true;
    }

    /**
     * 将制定目录下的服务器配置文件初始化到SDK访问路径
     * @param ctx Android应用上下文
     * @param serverPathName 本地配置文件目录
     * @return 是否成功
     */
    public static boolean initServerFromLocal(Context ctx , String serverPathName) {
        if(ctx == null) {
            return false;
        }
        String serverFolder = getSDKServerFolder(ctx);
        if(TextUtils.isEmpty(serverFolder)) {
            return false;
        }
        return copyFile(serverFolder, SDK_SERVER_NAME, readFlieToByte(serverPathName, 0, decodeFileLength(serverPathName))) == 0;
    }

    /**
     * 获得SDK服务器配置文件访问目录
     * @param ctx
     * @return
     */
    private static String getSDKServerFolder(Context ctx) {
        if(ctx == null) {
            return null;
        }
        String packageName = ctx.getPackageName();
        if(packageName == null || packageName.length() <= 0) {
            return null;
        }
        File serverFolder = new File("/data/data/" + packageName + "/ECSDK_Msg");
        if(!serverFolder.exists() && !serverFolder.mkdirs()) {
            return null;
        }
        return serverFolder.getAbsolutePath();
    }


    /**
     * 拷贝文件
     * @param fileDir
     * @param fileName
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir ,String fileName , byte[] buffer) {
        if(buffer == null) {
            return -2;
        }

        try {
            File file = new File(fileDir);
            if(!file.exists()) {
                file.mkdirs();
            }
            File resultFile = new File(file, fileName);
            if(!resultFile.exists()) {
                resultFile.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(resultFile, true));
            bufferedOutputStream.write(buffer);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return 0;

        } catch (Exception e) {
        }
        return -1;
    }


    /**
     * 将文件读取到内存中
     * @param filePath 文件本地路径
     * @param seek 读取的开始位置
     * @param length 读取的长度
     * @return 读取的字节
     */
    private static byte[] readFlieToByte (String filePath , int seek , int length) {
        if(TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return null;
        }
        if(length == -1) {
            length = (int)file.length();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bs = new byte[length];
            randomAccessFile.seek(seek);
            randomAccessFile.readFully(bs);
            randomAccessFile.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(LogUtil.getLogUtilsTag(FileUtils.class), "readFromFile : errMsg = " + e.getMessage());
            return null;
        }
    }

    /**
     * decode file length
     * @param filePath 文件本地路径
     * @return 文件的长度
     */
    private static int decodeFileLength(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return 0;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return 0;
        }
        return (int)file.length();
    }
}
