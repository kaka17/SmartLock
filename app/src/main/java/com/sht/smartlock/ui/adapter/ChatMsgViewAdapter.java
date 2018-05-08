package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.androidilbc.Codec;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.ChatMsgEntity;
import com.sht.smartlock.recordbyilbc.AudioPlayer;
import com.sht.smartlock.recordbyilbc.Pcm2Wav;
import com.sht.smartlock.util.DateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2015/10/13.
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;
    private boolean isPlaying = false;
    private Context ctx;
    private ImageView palyView;
    private LayoutInflater mInflater;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AudioPlayer player = AudioPlayer.getInstance();
    private AnimationDrawable voiceAnimation;
    private int n = 0;
    private Thread thread1 = null;
    private Thread thread2 = null;
    private String pcmFilePath = null;

    private String wavFilePath = null;
    private boolean isRight=true;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
        pcmFilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "record.pcm";
        wavFilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "record.wav";
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        ChatMsgEntity entity = coll.get(position);

        if (entity.getAttendant_id().equals("null")) {
            return IMsgViewType.IMVT_COM_MSG;
        } else {
            return IMsgViewType.IMVT_TO_MSG;
        }

    }

    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatMsgEntity entity = coll.get(position);
        String isComMsg = entity.getAttendant_id();

        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (isComMsg.equals("null")) {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_text_right, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_text_left, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView
                    .findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView
                    .findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tv_time);
            viewHolder.ivVoic = (ImageView) convertView.findViewById(R.id.ivVoic);
//            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = DateUtil.getFormatTime(DateUtil.getLongTime(entity.getCreate_time()));
        viewHolder.tvSendTime.setText(time);


        if (entity.getContent_type().contains("2")) {
            viewHolder.ivVoic.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setVisibility(View.GONE);
            String str = entity.getContent();
            final String[] split = str.split("-2");
            viewHolder.tvTime.setText(split[0] + "s");
        } else {
            viewHolder.ivVoic.setVisibility(View.GONE);
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setText(entity.getContent());
            viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            viewHolder.tvTime.setText("");
        }
        viewHolder.ivVoic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (entity.getContent_type().contains("2")) {

                    if (entity.getAttendant_id().equals("null")) {
                        ((ImageView) v).setImageResource(R.anim.voice_to_icon);
                        isRight = true;
                    } else {
                        ((ImageView) v).setImageResource(R.anim.voice_from_icon);
                        isRight = false;
                    }
                    String str = entity.getContent();
                    final String[] split = str.split("-2");
                    byte[] decode = Base64Utils.decode(split[1]);
                    String s = playMp3Path(decode);
                    //ilbc解码，创建pcm文件
//                    String musicbyWav = createMusicbyWav(decode);
//                    //把pcm转化成wav文件
//                    pcm2wav(musicbyWav, wavFilePath);
                    AppContext.toLog(entity.toString());
                    AppContext.toLog("split[1]==" + split[1]);
                    voiceAnimation = (AnimationDrawable) ((ImageView) v).getDrawable();
                    voiceAnimation.start();
                    playMusicByWav(s, voiceAnimation, (ImageView) v);

                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvTime;
        private ImageView ivVoic;
        public boolean isComMsg = true;
    }

    /**
     * @param decode
     * @Description
     */
    private void playMusic(byte[] decode, final AnimationDrawable voiceAnimation, final ImageView v) {
        byte[] decodedData = new byte[2048 * 1024];
        int len = 0;
        int decodeSize = Codec.instance().decode(decode, 0,
                decode.length, decodedData, 0);
        Log.e("settingMorningCall", "解码一次 " + decode.length + " 解码后的长度 " + decodeSize);
        byte[] bb = new byte[50];
        int lenght = 0;
        int num = (decodeSize / bb.length) + 1;
        player.startPlaying();
        for (int i = 0; i < num; i++) {
            System.arraycopy(decodedData, lenght, bb, 0, bb.length);
            player.addData(bb, bb.length);
            bb = new byte[50];
            lenght += bb.length;
        }
        player.stopPlaying();
    }

    private String createMusicbyWav(byte[] decode) {
        byte[] decodedData = new byte[2048 * 1024];
        int len = 0;
        int decodeSize = Codec.instance().decode(decode, 0,
                decode.length, decodedData, 0);
        byte[] bytes = new byte[decodeSize];
        System.arraycopy(decodedData, 0, bytes, 0, bytes.length);
        String path = android.os.Environment.getExternalStorageDirectory() + File.separator;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File tempWav = new File(file, "temp.pcm");
            if (!tempWav.exists()) {
                tempWav.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(tempWav);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return path + "temp.pcm";
        } catch (Exception e) {

        }
        return null;
    }

    private void pcm2wav(String pcmFilePath, String wavFilePath) {
        Pcm2Wav tool = new Pcm2Wav();
        try {
            tool.convertAudioFiles(pcmFilePath, wavFilePath);
            Log.e("TAG", "PCM Convert into Wav File Successfully");
        } catch (Exception e) {
            Log.e("TAG", "pcm failed to convert into wav File:" + e.getMessage());
        }
    }

    /**
     * @param name
     * @Description
     */
    private void playMusicByWav(String name, final AnimationDrawable voiceAnimation, final ImageView v) {
        try {
            if (mMediaPlayer.isPlaying()) {
                if (palyView == v) {
                    mMediaPlayer.stop();
                    voiceAnimation.stop();
                   if (isRight){
                        v.setImageResource(R.drawable.chatto_voice_playing);
                   }else {
                       v.setImageResource(R.drawable.chatfrom_voice_playing);
                   }
                } else {
                    mMediaPlayer.stop();
                    if (isRight){
                        v.setImageResource(R.drawable.chatto_voice_playing);
                    }else {
                        v.setImageResource(R.drawable.chatfrom_voice_playing);
                    }
                    palyView = v;
                    //
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(name);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    int duration = mMediaPlayer.getDuration();
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            voiceAnimation.stop();
                            if (isRight){
                                v.setImageResource(R.drawable.chatto_voice_playing);
                            }else {
                                v.setImageResource(R.drawable.chatfrom_voice_playing);
                            }

                        }
                    });
                }
            } else {
                palyView = v;
                //
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(name);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                int duration = mMediaPlayer.getDuration();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        voiceAnimation.stop();
                        if (isRight){
                            v.setImageResource(R.drawable.chatto_voice_playing);
                        }else {
                            v.setImageResource(R.drawable.chatfrom_voice_playing);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            voiceAnimation.stop();
            if (isRight){
                v.setImageResource(R.drawable.chatto_voice_playing);
            }else {
                v.setImageResource(R.drawable.chatfrom_voice_playing);
            }
        }

    }

    public String playMp3Path(byte[] byteWav) {
//        String path = ctx.getCacheDir().getParent() + File.separator;
        String path = android.os.Environment.getExternalStorageDirectory() + File.separator;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File tempWav = new File(file, "temp.mp3");
            if (!tempWav.exists()) {
                tempWav.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(tempWav);
            fos.write(byteWav);
            fos.flush();
            fos.close();
            return path + "temp.mp3";
        } catch (Exception e) {
        }
        return null;
    }


}
