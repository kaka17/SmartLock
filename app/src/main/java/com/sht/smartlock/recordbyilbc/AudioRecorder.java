package com.sht.smartlock.recordbyilbc;

import android.media.AudioRecord;
import android.util.Log;

import com.googlecode.androidilbc.Codec;
import com.sht.smartlock.Base64Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class AudioRecorder implements Runnable {

    String LOG = "Recorder ";

    private boolean isRecording = false;
    private AudioRecord audioRecord;

    private static final int BUFFER_FRAME_SIZE = 480;
    private int audioBufSize = 0;

    //
    private byte[] samples;// data
    // the size of audio read from recorder
    private int bufferRead = 0;
    // samples size
    private int bufferSize = 0;
    private List<AudioData> dataList = null;

    private FileOutputStream os;

    private static final int MAX_BUFFER_SIZE = 2048;

    private byte[] decodedData = new byte[1024];// data of decoded

//    private boolean isStop;

    /*
     * start recording
     */
    public void startRecording() {
        dataList = Collections.synchronizedList(new LinkedList<AudioData>());
        bufferSize = BUFFER_FRAME_SIZE;

        audioBufSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLERATE,
                AudioConfig.RECORDER_CHANNEL_CONFIG, AudioConfig.AUDIO_FORMAT);
        if (audioBufSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG, "audioBufSize error");
            return;
        }
        Log.e("TAG","--------------->audioBufSize"+audioBufSize);
        samples = new byte[audioBufSize];
        // 初始化recorder
        if (null == audioRecord) {
            audioRecord = new AudioRecord(AudioConfig.AUDIO_RESOURCE,
                    AudioConfig.SAMPLERATE,
                    AudioConfig.RECORDER_CHANNEL_CONFIG,
                    AudioConfig.AUDIO_FORMAT, audioBufSize);
        }
        dataList.clear();
        AudioWrapper.getInstance().setIsPlayStop(false);
        new Thread(this).start();
    }

    /*
     * stop
     */
    public void stopRecording() {
        this.isRecording = false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void run() {
        System.out.println(LOG + "audioRecord startRecording()");
        audioRecord.startRecording();
        System.out.println(LOG + "start recording");

        this.isRecording = true;
        byte[] by = new byte[2048];
        int encode;
        createFile();
        while (isRecording) {
            bufferRead = audioRecord.read(samples, 0, bufferSize);
            if (bufferRead > 0) {
                // add data to encoder
                encode = Codec.instance().encode(samples, 0, bufferRead, by, 0);
                AudioData rawData = new AudioData();
                rawData.setSize(encode);
                byte[] tempData = new byte[encode];
                System.arraycopy(by, 0, tempData, 0, encode);
                rawData.setRealData(tempData);
                dataList.add(rawData);
                try {
                    os.write(samples, 0, bufferRead);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(LOG + "end recording");
        audioRecord.stop();

        //直接播放
//        play();
        AudioWrapper.getInstance().setIsPlayStop(true);
//        playPath();
    }


    private void createFile() {
        File file = new File("/sdcard/mymusic.pcm");

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void play() {
        AudioPlayer player = AudioPlayer.getInstance();
        player.startPlaying();
        int decodeSize = 0;

        while (dataList.size() > 0) {
            AudioData encodedData = dataList.remove(0);
            decodedData = new byte[MAX_BUFFER_SIZE];
            byte[] data = encodedData.getRealData();
            //
            decodeSize = Codec.instance().decode(data, 0,
                    encodedData.getSize(), decodedData, 0);

            Log.e(LOG, "解码一次 " + data.length + " 解码后的长度 " + decodeSize);
            if (decodeSize > 0) {
                // add decoded audio to player
                player.addData(decodedData, decodeSize);
                // clear data
                decodedData = new byte[decodedData.length];
            }
        }
        player.stopPlaying();


    }


    public String playPath() {
//        AudioPlayer player = AudioPlayer.getInstance();
//        player.startPlaying();
//        int decodeSize = 0;
        int bytesize = 0;
        /**
         *  获取音频文件的长度
         *
         */
        for (int i = 0; i < dataList.size(); i++) {
            AudioData encodedData = dataList.get(i);
            bytesize += encodedData.getSize();
            Log.e(LOG, "------->bytesize=" + bytesize);
        }
        byte[] b = new byte[bytesize];
        int len = 0;
        /**
         *  把所以的音频数据复制到一个byte[]上并返回
         */
        for (int i = 0; i < dataList.size(); i++) {
            AudioData encodedData = dataList.get(i);
            System.arraycopy(encodedData.getRealData(), 0, b, len, encodedData.getSize());
            len += encodedData.getSize();
            Log.e(LOG, "------->返回编码数据len" + len);
        }
        if (dataList.size()==0){
            return null;
        }


//        //把数据解码 ，，，测试用
//        byte[] bb=new byte[2048*1024];
//        int decode = Codec.instance().decode(b, 0, b.length, bb, 0);
//        byte[] bbb=new byte[decode];
//        System.arraycopy(bb, 0, bbb, 0, decode);
//        //传递为编码数据
//        String str = Base64Utils.encode(bbb);
        //专递已编码的数据
        String str = Base64Utils.encode(b);




//        decodedData = new byte[MAX_BUFFER_SIZE * 1024];
//        decodeSize = Codec.instance().decode(b, 0,
//                len, decodedData, 0);
//        Log.e(LOG, "解码一次 " + b.length + " 解码后的长度 " + decodeSize);
//        byte[] bb=new byte[50];
//        int lenght=0;
//       int num=( decodeSize/bb.length)+1;
//        for (int i=0;i<num;i++ ){
//            System.arraycopy(decodedData, lenght, bb, 0, bb.length);
//            player.addData(bb, bb.length);
//            bb=new byte[50];
//            lenght+=bb.length;
//        }
//        player.stopPlaying();



        return str;
    }

    private void playFiel() {
        AudioPlayer player = AudioPlayer.getInstance();
        player.play("/sdcard/mymusic.mp3");
    }


    /**
     * 文件图像文件
     *
     * @param prePath 图片之前存放路径
     * @param curPath 图片拷贝之后存放路径
     */
    private static void copyImgFile(String prePath, String curPath) {
        long segmentLength = 1024 * 1024;   //1M大小
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(prePath, "r");
            File tempFile = new File("D:\\testFile\\", "temp.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile, true);

            int currentSegment = 0;
            int count = (int) (randomAccessFile.length() / segmentLength) + (randomAccessFile.length() % segmentLength > 0 ? 1 : 0);
            for (int i = 0; i < count; i++) {
                currentSegment = i + 1;
                System.out.println("分段：" + currentSegment);
                randomAccessFile.seek(i * segmentLength);

                long toal = 0; //当前已读取的长度
                int read = 0; //一次读取的长度
                int bufferLenght = 500 * 1024; //缓存长度
                byte[] buffer = new byte[bufferLenght];
                while ((read = randomAccessFile.read(buffer, 0, bufferLenght)) > 0) {
                    System.out.println("bufferLength:" + bufferLenght);
                    fileOutputStream.write(buffer, 0, read);
                    toal += read;
                    bufferLenght = (int) ((toal + bufferLenght) > segmentLength ? segmentLength - toal : bufferLenght);
                }

            }
            fileOutputStream.flush();
            fileOutputStream.close();

            System.out.println("源文件大小：" + randomAccessFile.length() + " , 拷贝后文件大小：" + tempFile.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
