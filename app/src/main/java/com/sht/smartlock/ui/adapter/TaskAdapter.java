package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.activity.myview.MyListView;
import com.sht.smartlock.ui.entity.TaskEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 */
public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<TaskEntity> list;
    private String servicerType;
    private MyItemClickListener onClick;
    String PATTERN = "HH:mm yyyy/MM/dd";
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AnimationDrawable voiceAnimation;
    private RelativeLayout relVoic;
    private ImageView palyView;


    public TaskAdapter(Context context, List<TaskEntity> list,MyItemClickListener onClick) {
        this.context = context;
        this.list = list;
        this.onClick=onClick;
    }

    public void setType(String servicerType) {
        this.servicerType = servicerType;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHoilder mhoilder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
            mhoilder = new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        } else {
            mhoilder = (ViewHoilder) convertView.getTag();
        }

        if (list.get(position).getContent_type().equals("1")) {//文字
            mhoilder.linVoic.setVisibility(View.GONE);
            switch (servicerType) {
                case "1"://清洁
                    mhoilder.tvContent.setText(context.getString(R.string.CleanServicer) + "(" + list.get(position).getContent() + ")");
                    break;
                case "2"://更换
                    mhoilder.tvContent.setText(context.getString(R.string.ChangeServicer) + "(" + list.get(position).getContent() + ")");
                    break;
                case "4"://其他
                    mhoilder.tvContent.setText(context.getString(R.string.OtherServicer) + "(" + list.get(position).getContent() + ")");
                    break;

            }

        } else {//语音
            switch (servicerType) {
                case "1"://清洁
                    mhoilder.tvContent.setText(context.getString(R.string.CleanServicer));
                    break;
                case "2"://更换
                    mhoilder.tvContent.setText(context.getString(R.string.ChangeServicer));
                    break;
                case "4"://其他
                    mhoilder.tvContent.setText(context.getString(R.string.OtherServicer));
                    break;
            }
            mhoilder.linVoic.setVisibility(View.VISIBLE);
            String str = list.get(position).getContent();
            final String[] split = str.split("-2");
            mhoilder.tvVoiceTime.setText(split[0] + "s");
            mhoilder.ivVoic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mhoilder.ivVoic.setImageResource(R.anim.voice_from_icon);
                    String str = list.get(position).getContent();
                    final String[] split = str.split("-2");
                    byte[] decode = Base64Utils.decode(split[1]);
                    String path = playMp3Path(decode);
                        voiceAnimation = (AnimationDrawable) mhoilder.ivVoic.getDrawable();
                        voiceAnimation.start();
                    playMusicByWav(path, voiceAnimation, (ImageView) v);
                }
            });

        }
        mhoilder.relBottom.setVisibility(View.VISIBLE);
        mhoilder.tvMsgInfo.setVisibility(View.GONE);
        //服务状态
        switch (list.get(position).getState()) {

            case "-1"://已删除
                break;
            case "0"://已发布，等待确认
                mhoilder.tvTakeTask.setVisibility(View.VISIBLE);
                mhoilder.tvCacelTask.setVisibility(View.VISIBLE);
                mhoilder.tvTakeAgain.setVisibility(View.GONE);

//                mhoilder.relComplete.setVisibility(View.GONE);
//                mhoilder.relReceive.setVisibility(View.GONE);
//                mhoilder.ivRelease.setImageResource(R.drawable.pic_dot_green);
//                mhoilder.tvRelease.setTextColor(context.getResources().getColor(R.color.TaskIsIng));
//                mhoilder.tvReleaseTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCreate_time(), PATTERN));
                if (AppContext.getProperty(list.get(position).getRoom_service_id())==null){
                    mhoilder.tvMsgInfo.setVisibility(View.GONE);
                }else {
                    int num=Integer.parseInt(AppContext.getProperty(list.get(position).getRoom_service_id()));
                    mhoilder.tvMsgInfo.setVisibility(View.VISIBLE);
                }
                break;
            case "1"://已确认
            case "2"://已经在执行
                mhoilder.tvTakeTask.setVisibility(View.VISIBLE);
                mhoilder.tvCacelTask.setVisibility(View.VISIBLE);
                mhoilder.tvTakeAgain.setVisibility(View.GONE);
//                    Log.e("SAVENUM", "---------->position"+position+"--="+AppContext.getProperty(list.get(position).getRoom_service_id()));
                if (AppContext.getProperty(list.get(position).getRoom_service_id())==null){
                    mhoilder.tvMsgInfo.setVisibility(View.GONE);
                }else {
                    int num=Integer.parseInt(AppContext.getProperty(list.get(position).getRoom_service_id()));
                    mhoilder.tvMsgInfo.setVisibility(View.VISIBLE);
                }
//                mhoilder.relComplete.setVisibility(View.GONE);
//                mhoilder.relReceive.setVisibility(View.VISIBLE);
//                mhoilder.ivRelease.setImageResource(R.drawable.pic_dot_gray);
//                mhoilder.tvRelease.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                mhoilder.ivReceive.setImageResource(R.drawable.pic_dot_green);
//                mhoilder.tvReceive.setTextColor(context.getResources().getColor(R.color.TaskIsIng));

//                mhoilder.tvReleaseTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCreate_time(), PATTERN));

//                mhoilder.tvReceiveTime.setText(DateUtil.formatTimeByFormat(list.get(position).getResponse_time(), PATTERN));
//                mhoilder.tvServiceror.setText(context.getString(R.string.Serviceror) + " " + list.get(position).getAttendant_name());
//                break;
                break;
            case "3"://完成
                mhoilder.relBottom.setVisibility(View.GONE);

                mhoilder.relComplete.setVisibility(View.VISIBLE);
                mhoilder.relReceive.setVisibility(View.VISIBLE);

//                mhoilder.ivRelease.setImageResource(R.drawable.pic_dot_gray);
//                mhoilder.tvRelease.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                mhoilder.ivReceive.setImageResource(R.drawable.pic_dot_gray);
//                mhoilder.tvReceive.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                mhoilder.ivComplete.setImageResource(R.drawable.pic_dot_green);
//                mhoilder.tvComplete.setTextColor(context.getResources().getColor(R.color.TaskIsIng));

//                mhoilder.tvReleaseTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCreate_time(), PATTERN));
//
//                mhoilder.tvReceiveTime.setText(DateUtil.formatTimeByFormat(list.get(position).getResponse_time(), PATTERN));
//                mhoilder.tvServiceror.setText(context.getString(R.string.Serviceror) + " " + list.get(position).getAttendant_name());

//                mhoilder.tvCompleteTime.setText(DateUtil.formatTimeByFormat(list.get(position).getFinished_time(), PATTERN));
                break;
            case "4"://服务已取消
                mhoilder.tvTakeTask.setVisibility(View.GONE);
                mhoilder.tvCacelTask.setVisibility(View.GONE);
                mhoilder.tvTakeAgain.setVisibility(View.VISIBLE);


//                if ("".equals("null")) {//未确认
//                    mhoilder.relComplete.setVisibility(View.VISIBLE);
//                    mhoilder.ivRelease.setImageResource(R.drawable.pic_dot_gray);
//                    mhoilder.tvRelease.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                    mhoilder.ivReceive.setVisibility(View.GONE);
//                    mhoilder.tvReceive.setVisibility(View.GONE);
//                    mhoilder.tvServiceror.setVisibility(View.GONE);
//                    mhoilder.relReceive.setVisibility(View.GONE);
//
//                    mhoilder.ivComplete.setImageResource(R.drawable.pic_dot_green);
//                    mhoilder.tvComplete.setTextColor(context.getResources().getColor(R.color.TaskIsIng));
//                    mhoilder.tvComplete.setText("已取消");
////                    mhoilder.tvReleaseTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCreate_time(), PATTERN));
//
////                    mhoilder.tvCompleteTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCancel_time(), PATTERN));
//                }{//已确认
//                    mhoilder.relComplete.setVisibility(View.VISIBLE);
//                    mhoilder.relReceive.setVisibility(View.VISIBLE);
//
//                    mhoilder.ivRelease.setImageResource(R.drawable.pic_dot_gray);
//                    mhoilder.tvRelease.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                    mhoilder.ivReceive.setImageResource(R.drawable.pic_dot_gray);
//                    mhoilder.tvReceive.setTextColor(context.getResources().getColor(R.color.TextBlack087));
//
//                    mhoilder.ivComplete.setImageResource(R.drawable.pic_dot_green);
//                    mhoilder.tvComplete.setTextColor(context.getResources().getColor(R.color.TaskIsIng));
//                    mhoilder.tvComplete.setText("已取消");
////                    mhoilder.tvReleaseTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCreate_time(), PATTERN));
//
////                    mhoilder.tvReceiveTime.setText(DateUtil.formatTimeByFormat(list.get(position).getResponse_time(), PATTERN));
//                    mhoilder.tvServiceror.setText(context.getString(R.string.Serviceror) + " " + list.get(position).getAttendant_name());
//
////                    mhoilder.tvCompleteTime.setText(DateUtil.formatTimeByFormat(list.get(position).getCancel_time(), PATTERN));
//                }
            break;
            default:
                break;
        }


        ServicerActionAdapter actionAdapter=new ServicerActionAdapter(context,list.get(position).getAtions());
        mhoilder.myLvTask.setAdapter(actionAdapter);



        mhoilder.tvCacelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(v,position);
            }
        });
        mhoilder.tvTakeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(v,position);
            }
        });
        mhoilder.tvTakeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(v, position);
            }
        });

        return convertView;
    }

    class ViewHoilder {
        private TextView tvVoiceTime, tvContent;
        private RelativeLayout relComplete, relReceive, relRelease,relBottom;
        private TextView tvComplete, tvCompleteTime, tvReceive, tvReceiveTime, tvServiceror, tvRelease, tvReleaseTime,tvCacelTask,tvTakeTask,tvTakeAgain;
        private ImageView ivComplete, ivReceive, ivRelease, ivVoic;
        private LinearLayout linVoic;
        private MyListView myLvTask;
        private TextView tvMsgInfo;

        public ViewHoilder(View view) {
            relComplete = (RelativeLayout) view.findViewById(R.id.relComplete);
            relReceive = (RelativeLayout) view.findViewById(R.id.relReceive);
            relRelease = (RelativeLayout) view.findViewById(R.id.relRelease);
            relBottom = (RelativeLayout) view.findViewById(R.id.relBottom);
            //
            ivComplete = (ImageView) view.findViewById(R.id.ivComplete);
            ivReceive = (ImageView) view.findViewById(R.id.ivReceive);
            ivRelease = (ImageView) view.findViewById(R.id.ivRelease);

            tvComplete = (TextView) view.findViewById(R.id.tvComplete);
            tvCompleteTime = (TextView) view.findViewById(R.id.tvCompleteTime);

            tvReceive = (TextView) view.findViewById(R.id.tvReceive);
            tvReceiveTime = (TextView) view.findViewById(R.id.tvReceiveTime);
            tvServiceror = (TextView) view.findViewById(R.id.tvServiceror);

            tvRelease = (TextView) view.findViewById(R.id.tvRelease);
            tvReleaseTime = (TextView) view.findViewById(R.id.tvReleaseTime);
            linVoic = (LinearLayout) view.findViewById(R.id.linVoic);
            ivVoic = (ImageView) view.findViewById(R.id.ivVoic);
            tvVoiceTime = (TextView) view.findViewById(R.id.tvVoiceTime);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvCacelTask = (TextView) view.findViewById(R.id.tvCacelTask);
            tvTakeTask = (TextView) view.findViewById(R.id.tvTakeTask);
            tvTakeAgain = (TextView) view.findViewById(R.id.tvTakeAgain);
            tvMsgInfo = (TextView) view.findViewById(R.id.tvMsgInfo);

            myLvTask= (MyListView) view.findViewById(R.id.myLvTask);


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
                    v.setImageResource(R.drawable.chatfrom_voice_playing);
                } else {
                    mMediaPlayer.stop();
                    palyView.setImageResource(R.drawable.chatfrom_voice_playing);
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
                            v.setImageResource(R.drawable.chatfrom_voice_playing);

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
                        v.setImageResource(R.drawable.chatfrom_voice_playing);

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            voiceAnimation.stop();
            v.setImageResource(R.drawable.chatfrom_voice_playing);
        }

    }
}
