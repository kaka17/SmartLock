package com.sht.smartlock.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件操作工具包
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final int BUFFER = 8192;
    private static final long ONE_DAY_MILLIS = 0x5265c00L;

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readInStream(InputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory()
                    + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtil.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtil.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @param dir
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     *
     * @param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
        return status;
    }

    /**
     * 检查是否安装外置的SD卡
     *
     * @return
     */
    public static boolean checkExternalSDExists() {

        Map<String, String> evn = System.getenv();
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param fileName
     * @return
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                // delete all files within the specified directory and then
                // delete the directory
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/"
                                + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除空目录
     * <p/>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 清空一个文件夹
     *
     * @param filePath
     */
    public static void clearFileWithPath(String filePath) {
        List<File> files = FileUtil.listPathFiles(filePath);
        if (files.isEmpty()) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearFileWithPath(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    /**
     * 获取SD卡的根目录
     *
     * @return
     */
    public static String getSDRoot() {

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取手机外置SD卡的根目录
     *
     * @return
     */
    public static String getExternalSDRoot() {

        Map<String, String> evn = System.getenv();

        return evn.get("SECONDARY_STORAGE");
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 获取一个文件夹下的所有文件
     *
     * @param root
     * @return
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.isFile())
                allDir.add(f);
            else
                listPath(f.getAbsolutePath());
        }
        return allDir;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     *
     * @param newPath
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return PathStatus.EXITS;
        }
        if (path.mkdir()) {
            return PathStatus.SUCCESS;
        } else {
            return PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
     *
     * @param context
     * @param dir
     * @return
     */
    public static String getAppCache(Context context, String dir) {
        String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
        File savedir = new File(savePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        savedir = null;
        return savePath;
    }


    public static InputStream byteToInputSteram(byte abyte0[]) {
        ByteArrayInputStream bytearrayinputstream = null;
        if (abyte0 != null && abyte0.length > 0)
            bytearrayinputstream = new ByteArrayInputStream(abyte0);
        return bytearrayinputstream;
    }

    public static void combineTextFile(File[] files, File file)
            throws IOException {
        if (file != null && files != null) {
            BufferedReader brSource = null;
            BufferedWriter brTarget = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file)));
            for (int i = 0; i < files.length; i++) {
                brSource = new BufferedReader(new InputStreamReader(
                        new FileInputStream(files[i])));
                String s = brSource.readLine();
                String s1;
                while ((s1 = brSource.readLine()) != null) {
                    brTarget.write(s);
                    brTarget.newLine();
                    s = s1;
                }
                brTarget.write(s);
                if (i != -1 + files.length)
                    brTarget.newLine();
                brTarget.flush();
            }
            if (brSource != null)
                brSource.close();
            if (brTarget != null)
                brTarget.close();
        }
    }

    public static void copy(File source, File target) throws IOException {
        if (source != null && !source.exists())
            Log.i(TAG, "the source file is not exists: "
                    + source.getAbsolutePath());
        else if (source.isFile())
            copyFile(source, target);
        else
            copyDirectory(source, target);
    }

    public static void copyDirectory(File sourceDir, File targetDir)
            throws IOException {
        targetDir.mkdirs();
        if (sourceDir != null) {
            File[] f = sourceDir.listFiles();
            for (int i = 0; i < f.length; i++) {
                if (f[i].isFile()) {
                    copyFile(
                            f[i],
                            new File((new StringBuilder(String
                                    .valueOf(targetDir.getAbsolutePath())))
                                    .append(File.separator)
                                    .append(f[i].getName()).toString()));
                } else if (f[i].isDirectory()) {
                    copyDirectory(new File(sourceDir, f[i].getName()),
                            new File(targetDir, f[i].getName()));
                }
            }
        }
    }

    public static void copyFile(File source, File target) throws IOException {
        if (source != null && target != null) {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(source));
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(target));
            try {
                byte[] buffer = new byte[BUFFER];
                int i = -1;
                while ((i = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, i);
                }
                bos.flush();
            } finally {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            }
        }
    }

    public static boolean delete(File file) {
        boolean flag;
        if (file != null && !file.exists()) {
            Log.i(TAG, "the file is not exists: " + file.getAbsolutePath());
            return false;
        } else if (file != null && file.isFile())
            flag = deleteFile(file);
        else
            flag = deleteDirectory(file, true);
        return flag;
    }

    public static boolean deleteDirectory(File file, boolean flag) {
        return deleteDirectory(file, null, flag, false);
    }

    public static boolean deleteDirectory(File file, String s, boolean flag,
                                          boolean flag1) {
        if (file == null) {
            return false;
        }
        if (!file.exists() || !file.isDirectory()) {
            Log.i(TAG, "the directory is not exists: " + file.getAbsolutePath());
            return false;
        }
        boolean flag3 = true;
        File[] f = file.listFiles();
        for (int i = 0; i < f.length; i++) {
            if (f[i].isFile()) {
                if (s == null
                        || f[i].getName().toLowerCase()
                        .endsWith("." + s.toLowerCase())) {
                    flag3 = deleteFile(f[i]);
                    if (!flag3) {
                        break;
                    }
                }
            } else {
                if (!flag1) {
                    flag3 = deleteDirectory(f[i], true);
                    if (!flag3) {
                        break;
                    }
                }// goto _L7; else goto _L9
            }// goto _L6; else goto _L5
        }
        if (!flag3) {
            Log.i(TAG, "delete directory fail: " + file.getAbsolutePath());
        } else if (flag) {
            if (file.delete())
                return true;
            else
                Log.i(TAG, "delete directory fail: " + file.getAbsolutePath());
        } else {
            return true;
        }
        return false;
    }

    public static boolean deleteDirectoryByTime(File file, int time) {
        boolean flag = true;
        if ((file == null || file.exists()) && file.isDirectory()) {
            if (file != null) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        if (System.currentTimeMillis() - f.lastModified()
                                - ONE_DAY_MILLIS * (long) time > 0L)
                            if (f.isDirectory())
                                flag = deleteDirectory(f, true);
                            else
                                flag = delete(f);
                    }
                }
            }
        } else {
            Log.i(TAG, "the directory is not exists: " + file.getAbsolutePath());
            return false;
        }
        return flag;
    }

    public static boolean deleteFile(File file) {
        if (file != null && file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            Log.i(TAG, "the file is not exists: " + file.getAbsolutePath());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableStorageSize(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            StatFs statfs = new StatFs(file.getPath());
            return statfs.getBlockSize() * (long) statfs.getAvailableBlocks();
        }
        return -1;
    }

    public static void move(File file, File file1) throws IOException {
        copy(file, file1);
        delete(file);
    }

    public static byte[] readFileToByte(File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(
                (int) file.length());
        InputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        try {
            int i = -1;
            byte[] buffer = new byte[1024];
            while ((i = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, i);
            }
            return bos.toByteArray();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readTextFile(File file) throws IOException {
        String text = null;
        if (file != null) {
            FileInputStream fis = new FileInputStream(file);
            text = readTextInputStream(fis);
            if (fis != null)
                fis.close();
        }
        return text;
    }

    public static String readTextInputStream(InputStream is) throws IOException {
        if (is != null) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            if (br != null)
                br.close();
            return sb.toString();
        }
        return null;
    }

    public static int writeFile(File file, InputStream is) throws IOException {
        long l = 0;
        if (is != null && file != null) {
            byte[] abyte0 = new byte[BUFFER];
            OutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            DataInputStream dis = new DataInputStream(is);
            int i = -1;
            while ((i = dis.read(abyte0)) != -1) {
                dos.write(abyte0, 0, i);
                l += i;
            }
            if (dis != null)
                dis.close();
            if (dos != null)
                dos.close();
        }
        return (int) (l / 1024L);
    }

    public static void writeFile(File file, byte[] data) throws Exception {
        if (file != null && data != null) {
            OutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write(data);
            if (dos != null)
                dos.close();
        }
    }

    public static void writeTextFile(File file, String content)
            throws IOException {
        if (file != null) {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write(content.getBytes());
            if (dos != null)
                dos.close();
        }
    }

    public static void writeTextFile(File file, String[] lines)
            throws IOException {
        String content = "";
        if (file != null && lines != null) {
            for (int i = 0; i < lines.length; i++) {
                content = new StringBuilder(content).append(lines[i])
                        .toString();
                if (i != -1 + lines.length)
                    content = new StringBuilder(content).append("\r\n")
                            .toString();
            }
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream os = new DataOutputStream(fos);
            os.write(content.getBytes());
            if (os != null)
                os.close();
        }
    }

    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir(path);
        }
        dir.delete();
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(dirName);
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        file.isFile();
        return file.exists();
    }
    // 读取文件数据转换成byte[]数据
    public static byte[] fileData(String imageFilePath) {
        File file = new File(imageFilePath);
        FileInputStream fStream;
        try {
            fStream = new FileInputStream(file);
            // 获取文件大小
            int leng_file = fStream.available();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bufferSize = 1024 * 8;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
//            if (leng_file > (20 * 1024 * 1024)) {
////                handler.sendEmptyMessage(0);
//                return null;
//            }
            while ((length = fStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            fStream.close();
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}