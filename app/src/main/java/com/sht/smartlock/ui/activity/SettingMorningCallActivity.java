package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.SoundMeter;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.ChatMsgEntity;
import com.sht.smartlock.recordbylame.AudioRecorder2Mp3Util;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myview.MyListView;
import com.sht.smartlock.ui.adapter.ChatMsgViewAdapter;
import com.sht.smartlock.ui.adapter.ServicerActionAdapter;
import com.sht.smartlock.ui.chat.applib.adapter.VoicePlayClickListener;
import com.sht.smartlock.ui.chat.applib.utils.CommonUtils;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.TaskEntity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.MyVoid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingMorningCallActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private ImageView chatting_mode_btn, volume;
    //    private TextView mBtnRcd;
    private Button mBtnSend;
    private RelativeLayout mBottom;
    private EditText mEditTextContent;

    private SoundMeter mSensor;
    private int flag = 1;
    private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
            voice_rcd_hint_tooshort;
    private boolean btn_vocie = false;
    private LinearLayout del_re;
    private View rcChat_popup;
    private Handler mHandler = new Handler();
    private boolean isShosrt = false;
    private ImageView img1, sc_img1;
    private long startVoiceT, endVoiceT;
    //    private String voiceName;
    private ListView mListView;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private String service_type;
    //    private MediaPlayer mMediaPlayer = new MediaPlayer();
    //    private AudioWrapper audioWrapper;
//    AudioPlayer player;
    private boolean isfist = false;
    private AudioRecorder2Mp3Util util = null;
    private boolean canClean = false;


    private String amrFilePath = null;
    private String mp3FilePath = null;


    private View btn_press_to_speak;
    private TextView recordingHint;
    private View recordingContainer;
    private ImageView micImage;
    private Drawable[] micImages;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
            Log.e("voice", "di ji ge tupian===" + msg.what);
        }
    };

    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 22;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.RECORD_AUDIO;//录音权限
    private static final String WRITE_PERMISSION02 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String WRITE_PERMISSION03 = Manifest.permission.READ_EXTERNAL_STORAGE;
    private List<String> listPermission = new ArrayList<>();
    private TextView tvContent;
    private ImageView ivVoic;
    private RelativeLayout relBottom;
    String PATTERN = "HH:mm yyyy/MM/dd";
    private TaskEntity taskEntity;
    private LinearLayout linVoic;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AnimationDrawable voiceAnimation;
    private RelativeLayout relVoic;
    private MyListView myLvTask;
    private ServicerActionAdapter actionAdapter;
    private InputMethodManager inputMethodManager;
    private TextView tvVoiceTime;
    private  MyBroadcastReceiver myBroadcastReceiver;
    private MySQLiteOpenHelper mydbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 启动activity时不自动弹出软键盘
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mPermissionHelper = new PermissionHelper(SettingMorningCallActivity.this);
        listPermission.add(WRITE_PERMISSION);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        listPermission.add(WRITE_PERMISSION02);
//        listPermission.add(WRITE_PERMISSION03);
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14)};

        voiceRecorder = new MyVoid(micImageHandler);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        micImage = (ImageView) findViewById(R.id.mic_image);


        amrFilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "test_audio_recorder_for_mp3.raw";
        mp3FilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "test_audio_recorder_for_mp3.mp3";
        Bundle bundle = getIntent().getExtras();
        service_type = bundle.getString(Config.SERVICE_TYPE);
        taskEntity = (TaskEntity) bundle.getSerializable(Config.TASKEntity);
        findviewbyid();
        setBroadReceiver();
        setClickLister();
        /*第三个参数service_type 服务类型
            1 – 房间清扫
            2 – 更换床上用品
            3 - Morning Call
            4 - 其它
          */
        if (service_type.equals("1")) {
            tvTitlePanel.setText(R.string.Clear_Room);
        } else if (service_type.equals("2")) {
            tvTitlePanel.setText(R.string.Chang_Room_Pm);
        } else if (service_type.equals("3")) {
            tvTitlePanel.setText(R.string.SettingMorningCall);
        } else if (service_type.equals("4")) {
            tvTitlePanel.setText(R.string.AnOther);
        }
//        initData();
    }

    private void findviewbyid() {
        //为了 初始化录音限权

        btn_press_to_speak = findViewById(R.id.btn_press_to_speak);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        recordingContainer = findViewById(R.id.recording_container);

        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
        mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
        del_re = (LinearLayout) findViewById(R.id.del_re);

        //top
        linVoic = (LinearLayout) findViewById(R.id.linVoic);
        tvVoiceTime = (TextView) findViewById(R.id.tvVoiceTime);

        tvContent = (TextView) findViewById(R.id.tvContent);

        ivVoic = (ImageView) findViewById(R.id.ivVoic);

        relBottom = (RelativeLayout) findViewById(R.id.relBottom);
        relBottom.setVisibility(View.GONE);
        myLvTask = (MyListView) findViewById(R.id.myLvTask);

        setTaskInfo();
        //bottm
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
//        mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
        mBtnSend.setOnClickListener(this);
        mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
        chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
        volume = (ImageView) this.findViewById(R.id.volume);
        rcChat_popup = this.findViewById(R.id.rcChat_popup);
        img1 = (ImageView) this.findViewById(R.id.img1);
        sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
        del_re = (LinearLayout) this.findViewById(R.id.del_re);
        voice_rcd_hint_rcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding);
        voice_rcd_hint_loading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading);
        voice_rcd_hint_tooshort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort);

        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);

        //语音文字切换按钮
        chatting_mode_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (btn_vocie) {
                    btn_press_to_speak.setVisibility(View.GONE);
                    btn_vocie = false;
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_voice_btn);
                    mBtnSend.setVisibility(View.VISIBLE);
                    //软件盘消失
//                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    mEditTextContent.setVisibility(View.VISIBLE);
                    mEditTextContent.setFocusable(true);
                    mEditTextContent.setFocusableInTouchMode(true);
                    mEditTextContent.requestFocus();
                } else {
                    mEditTextContent.setVisibility(View.GONE);
                    btn_press_to_speak.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                    hideSoftKeyboard();
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_keyboard_btn);
                    btn_vocie = true;
                    //出现软件盘
                }
            }
        });


        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());

        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);

    }

    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setTaskInfo() {
        actionAdapter = new ServicerActionAdapter(getApplicationContext(), taskEntity.getAtions());
        myLvTask.setAdapter(actionAdapter);


        ivVoic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVoic.setImageResource(R.anim.voice_from_icon);
                String str = taskEntity.getContent();
                final String[] split = str.split("-2");
                byte[] decode = Base64Utils.decode(split[1]);
                String path = playMp3Path(decode);
                voiceAnimation = (AnimationDrawable) ivVoic.getDrawable();
                voiceAnimation.start();
                playMusicByWav(path, voiceAnimation, (ImageView) v);
            }
        });
        if (taskEntity.getContent_type().equals("1")) {//文字
            linVoic.setVisibility(View.GONE);
            switch (service_type) {
                case "1"://清洁
                    tvContent.setText(getString(R.string.CleanServicer) + "(" + taskEntity.getContent() + ")");
                    break;
                case "2"://更换
                    tvContent.setText(getString(R.string.ChangeServicer) + "(" + taskEntity.getContent() + ")");
                    break;
                case "4"://其他
                    tvContent.setText(getString(R.string.OtherServicer) + "(" + taskEntity.getContent() + ")");
                    break;

            }

        } else {//语音
            linVoic.setVisibility(View.VISIBLE);
            String str = taskEntity.getContent();
            final String[] split = str.split("-2");
            tvVoiceTime.setText(split[0] + "s");
        }


    }

    /**
     * @param path
     * @Description
     */
    private void playMusicByWav(String path, final AnimationDrawable voiceAnimation, final ImageView v) {
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                voiceAnimation.stop();
                v.setImageResource(R.drawable.chatfrom_voice_playing);

            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
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
            Log.e("tag", "----------->ee" + e.toString());
            voiceAnimation.stop();
            v.setImageResource(R.drawable.chatfrom_voice_playing);
        }

    }


    // 读取文件数据转换成byte[]数据
    private byte[] fileData(String imageFilePath) {
        File file = new File(imageFilePath);
        FileInputStream fStream;
        try {
            fStream = new FileInputStream(file);
//            // 获取文件大小
//            int leng_file = fStream.available();
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


    private void setClickLister() {
        btn_cancle.setOnClickListener(listener);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rcChat_popup.setVisibility(View.GONE);
            }
        });
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppManager.getAppManager().finishActivity();
        }
    };


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void send() {
        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setCreate_time(getDate());
            entity.setMsgType(false);
            entity.setContent(contString);
            entity.setContent_type("1");
            entity.setAttendant_id("null");
            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();

            mEditTextContent.setText("");

            mListView.setSelection(mListView.getCount() - 1);
            if (NewDoorFragment.list.size() == 0) {
                //无数据
                return;
            }
//            HttpClient.instance().addhotelService(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), contString, service_type, "1", new NetworkRequestLoginResult());
            HttpClient.instance().servicerSendMsg(taskEntity.getRoom_service_id(), contString, "1", new NetworkRequestLoginResult());
            mEditTextContent.setText("");
        }
    }


    class NetworkRequestLoginResult extends HttpCallBack {
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            Entitys data = responseBean.getData(Entitys.class);
            if (data.getCode() == 0) {
                AppContext.toast("任务发送失败，或者不在服务时间");
            }
            initData();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
                + mins);

        return sbBuffer.toString();
    }

    //获取该服务的数据
    private void initData() {
        if (NewDoorFragment.list.size() == 0) {
            //无数据
            return;
        }
        HttpClient.instance().servicerMsgList(taskEntity.getRoom_service_id(), new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog(responseBean.toString());
                if (AppContext.getProperty(taskEntity.getRoom_service_id()) != null) {
                    AppContext.setProperty(taskEntity.getRoom_service_id(), null);
                    seleteDbData(taskEntity.getRoom_service_id());
                }
                List<ChatMsgEntity> list = responseBean.getListData(ChatMsgEntity.class);
                mDataArrays.clear();
                mDataArrays.addAll(list);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mListView.getCount() - 1);
                AppContext.toLog(list.size() + ";;;;;;;" + list.toString());

            }
        });
    }

    private void  seleteDbData(String room_service_id){
        mydbHelper = MySQLiteOpenHelper.getInstance(SettingMorningCallActivity.this);
        String sqlString2 = "select * from NUNoMSG where room_servicer_id=?";
        int count = mydbHelper.selectCount(
                sqlString2,
                new String[] {room_service_id});

        if (count > 0) {
            // "数据已经存在需要更改
//            String sqlUpda="update service_type set msgnum=? where room_servicer_id='"+room_service_id+"'";
//            boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
            String sql="delete from NUNoMSG where room_servicer_id='"+room_service_id+"'";
            boolean isdelete=mydbHelper.delete(sql);
        } else {
            // 先插入数据再查询数据
        }

    }

    public String playMp3Path(byte[] byteWav) {
        String path = getApplicationContext().getCacheDir().getParent() + File.separator;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_morning_call;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }


    //---------------------------------------------------修改录音------------------------------
    private PowerManager.WakeLock wakeLock;
    public MyVoid voiceRecorder;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        AppContext.toast(st4);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION);
                        if (b) {
                            if (NewDoorFragment.list.get(NewDoorFragment.pos).getID().isEmpty() || NewDoorFragment.list.get(NewDoorFragment.pos).getID().equals("null")) {
                                voiceRecorder.startRecording(null, "service", getApplicationContext());
                            } else {
                                voiceRecorder.startRecording(null, NewDoorFragment.list.get(NewDoorFragment.pos).getID(), getApplicationContext());
                            }
                        } else {
                            mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        AppContext.toast(R.string.recoding_fail);
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
//                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
//                                        Integer.toString(length), false);
                                Log.e("taaag", "path======" + voiceRecorder.getVoiceFilePath());
                                setVoid(voiceRecorder.getVoiceFilePath(), Integer.toString(length));
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppContext.toast(st3 + "" + e.toString());
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }



    private void setVoid(String marFilePath, String time) {
        File file = new File(marFilePath);
        if (!file.exists()) {
        } else {//文件存在时才发送
            byte[] fileData = fileData(marFilePath);
            String strs = Base64Utils.encode(fileData);
            //  删除数据
//            file.delete();

            String str = null;
            //1 房间id, 2 内容，
                            /*第三个参数service_type		服务类型
                            1 – 房间清扫
                            2 – 更换床上用品
                            3 - Morning Call
                            4 - 其它
                            */
            str = time + "-2" + strs;
            //4参 ：本文为1 ，语音为2
            Log.e("taaag", str);
            HttpClient.instance().servicerSendMsg(taskEntity.getRoom_service_id(), str, "2", new NetworkRequestLoginResult());
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setMsgType(false);
            entity.setVoicTime(time + "\"");
            entity.setCreate_time(getDate());
            entity.setContent(str);
            entity.setAttendant_id("null");
            entity.setContent_type("2");
            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mListView.getCount() - 1);
            rcChat_popup.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
//                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                    startActivityForResult(intent, CAMERE_REQUEST_CODE);
                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开录音权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);// 不需要时注销
    }

    private void setBroadReceiver(){
        // 注册广播
        myBroadcastReceiver=new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.SERVICERMESSAGE);
        this.registerReceiver(myBroadcastReceiver, filter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "intent:" + intent);
            String servicerRoomID = intent.getStringExtra(Config.SERVICERMESSAGE_BYSRID);
            initData();
//            Bundle bundle = intent.getExtras();

        }
    }

}
