package com.googlecode.androidilbc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sht.smartlock.R;

public class Demo extends Activity {
    class Player {
        static final private int DEFAULT_BUFFER_SIZE = 1024;
        
        private AudioTrack mTrack;
        
        private int mBufferSize;
        
        public Player() {
            int bufferSize;
            bufferSize = AudioRecord.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);
        }
        
        public void play(byte[] data, int offset, int length) {
            mTrack.play();
            mTrack.write(data, 0, length);
        }
        
        public void play(String path) {
            File file = new File(path);
            
            int audioLength = (int) (file.length());
            byte[] audio = new byte[audioLength];
            try {
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(file)));
                // Read the file into the buffer
                int i = 0;
                while (dis.available() > 0) {
                    audio[i] = dis.readByte();
                    i++;
                }
                
                // Close the input streams.
                dis.close();
                
            } catch (Exception e) {
                
            }
        }
    }
    
    public static final String TAG = "ilbc";
    
    class Recorder {
        
        private String mPath;
        
        private int mBufferSize;
        
        private AudioRecord mRecorder;
        
        private FileOutputStream mOut;
        
        class RecordThread extends Thread {
            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                
                while (true) {
                    
                    if (null != mRecorder
                            && AudioRecord.RECORDSTATE_RECORDING == mRecorder
                                    .getRecordingState()) {
                        
                        final byte[] samples;
                        final byte[] data;
                        final int bytesRecord;
                        final int bytesEncoded;
                        
                        samples = new byte[mBufferSize];
                        // TODO: calculate data size.
                        data = new byte[mBufferSize];
                        
                        bytesRecord = mRecorder.read(samples, 0, mBufferSize);
                        
                        if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.e(TAG,
                                    "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                        } else if (bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(TAG,
                                    "read() returned AudioRecord.ERROR_BAD_VALUE");
                        } else if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.e(TAG,
                                    "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                        }
                        
                        bytesEncoded = Codec.instance().encode(samples, 0,
                                bytesRecord, data, 0);
                        
                        try {
                            mOut.write(data, 0, bytesEncoded);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to write");
                        }
                    } else {
                        break;
                    }
                }
                
            }
        }
        
        public void setSavePath(String path) throws IOException {
            File file = new File(path);
            
            if (file.exists()) {
                Log.i(TAG, "Remove exists file, " + path);
            }
            
            file.createNewFile();
            
            if (!file.canWrite()) {
                throw new IOException("Cannot write to " + path);
            }
            
            mPath = path;
            
        }
        
        public void start() throws IOException {
            
            int truncated;
            
            mBufferSize = AudioRecord
                    .getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            
            if (mBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(TAG, "buffer error");
                return;
            }
            
            // 480 bytes for 30ms(y mode)
            truncated = mBufferSize % 480;
            if (truncated != 0) {
                mBufferSize += 480 - truncated;
                Log.i(TAG, "Extend buffer to " + mBufferSize);
            }
            
            try { 
                mOut = new FileOutputStream(new File(mPath));
                
                // Write File Header
                mOut.write("#!iLBC30\n".getBytes());
                
            } catch (FileNotFoundException e) {
                
                throw new IOException("File not found");
            }
            
            mRecorder = new AudioRecord(AudioSource.MIC, 8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            
            mRecorder.startRecording();
            
            new RecordThread().start();
        }
        
        public void stop() {
            
            if (null == mRecorder) {
                
                Log.w(TAG, "Recorder has not start yet");
                return;
            }
            mRecorder.stop();
            try {
                mOut.close();
            } catch (IOException e) {
            }
        }
        
        private void record() {
            
            int bufferSize;
            bufferSize = AudioRecord
                    .getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            
            if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(TAG, "buffer error");
                return;
            }
            
            bufferSize *= 10;
            
            AudioRecord record = new AudioRecord(AudioSource.MIC, 8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            
            Log.d(TAG, "buffer size: " + bufferSize);
            byte[] tempBuffer = new byte[bufferSize];
            record.startRecording();
            
            int bufferRead = record.read(tempBuffer, 0, bufferSize);
            if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_INVALID_OPERATION");
            } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_BAD_VALUE");
            } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_INVALID_OPERATION");
            }
            // Close resources…
            record.stop();
            Log.e(TAG, "playing, " + bufferRead);
            
            byte[] samples = new byte[4096 * 10];
            byte[] data = new byte[4096 * 10];
            
            int samplesLength;
            int dataLength;
            
            dataLength = Codec.instance().encode(tempBuffer, 0, bufferRead,
                    data, 0);
            
            Log.d(TAG, "encode " + bufferRead + " to " + dataLength);
            
            Log.d(TAG, "data[0]: " + data[0] + "data[dataLength-1]: "
                    + data[dataLength - 1]);
            
            samplesLength = Codec.instance().decode(data, 0, dataLength,
                    samples, 0);
            
            Log.d(TAG, "decode " + dataLength + " to " + samplesLength);
            
            Log.d(TAG, "samples[0]: " + samples[0]
                    + "samples[samplesLength-1]: " + samples[samplesLength - 1]);
            new Player().play(samples, 0, samplesLength);
            
            // new Player().play(tempBuffer, 0, bufferRead);
            Log.e(TAG, "rec");
            
        }
        
    }
    
    private Button mButton;
    
    private Recorder mRecorder;
    
    private Handler mHandler = new Handler() {
        
        public void handleMessage(Message msg) {
            
        }
    };
    
    public static final int STATE_IDLE = 70;
    
    public static final int STATE_RECORDING = 71;
    
    public static final int STATE_RECORDED = 72;
    
    public static final int STATE_PLAYING = 73;
    
    private int mState;
    
    // / Called when the activity is first created. /
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        
        mRecorder = new Recorder();
        
//        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                
                switch (mState) {
                case STATE_IDLE:
                    updateState(STATE_RECORDING);
                    break;
                case STATE_RECORDING:
                    updateState(STATE_RECORDED);
                    
                case STATE_RECORDED:
                    updateState(STATE_IDLE);
                
                default:
                    break;
                }
                
            }
        });
        updateState(STATE_IDLE);
        
        
    }
    
    private void updateState(int state) {
        
        mState = state;
        switch (state) {
        case STATE_IDLE:
            mButton.setText("Record");
            break;
        
        case STATE_RECORDING:
            mButton.setText("Recording");
            try {
                mRecorder.setSavePath("/sdcard/rec.ilbc");
                mRecorder.start();
            } catch (IOException e) {
                Log.e(TAG, " " + e);
            }
            
            break;
        case STATE_RECORDED:
            mRecorder.stop();
            mButton.setText("Play");
            break;
        case STATE_PLAYING:
            mButton.setText("Record");
            break;
        }
    }
}