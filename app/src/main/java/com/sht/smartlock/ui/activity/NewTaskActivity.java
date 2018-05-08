package com.sht.smartlock.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
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
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.ChatMsgEntity;
import com.sht.smartlock.recordbyilbc.AudioPlayer;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myview.MyGiftView;
import com.sht.smartlock.ui.activity.myview.TimeUtils;
import com.sht.smartlock.ui.adapter.ChatMsgViewAdapter;
import com.sht.smartlock.ui.chat.applib.adapter.VoicePlayClickListener;
import com.sht.smartlock.ui.chat.applib.utils.CommonUtils;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.util.MyVoid;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class NewTaskActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvProjectName;
    private ImageView ivVoic;
    private ImageView ivSend;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
//            ivVoicerAnim.setImageDrawable(micImages[msg.what]);
            Log.e("voice", "di ji ge tupian===" + msg.what);
        }
    };

    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 22;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.RECORD_AUDIO;//录音权限
    //    private static final String WRITE_PERMISSION02 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String WRITE_PERMISSION03 = Manifest.permission.READ_EXTERNAL_STORAGE;
    private List<String> listPermission = new ArrayList<>();

    //----------------------------------------呼叫服务功能-------------------------------
    private PowerManager.WakeLock wakeLock;
    public MyVoid voiceRecorder;
    private Animation operatingAnim;
    private EditText etContent;
    //    private String type;
    private String servicerType;
    private RelativeLayout relVoice;
    private Dialog dialog;
    private ImageView ivVoics;
    private TextView tv_time;
    private boolean isVoice = false;
    private int voicTime = 0;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AnimationDrawable voiceAnimation;
    private RelativeLayout relVoic;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    break;
                case 0://60秒倒计时结束，停止语音
                    if (dialog!=null&&dialog.isShowing()){//还在录音中
//                        AppContext.toast("录音太长，已自动结束");
//                        v.setPressed(false);
                        if (wakeLock.isHeld()) {
                            wakeLock.release();
                        }
                        isVoice = true;
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
//                            dialog.dismiss();
                            tvSure.setText(getString(R.string.GoToSave));
                            tvIsVoicIng.setText(getString(R.string.IsGoToSave));
                            ivVoicComp.setVisibility(View.VISIBLE);
                            myGift.setVisibility(View.GONE);
//                            etContent.setText(" ");
                            etContent.setHint("");
                            myTime.cancelRun();
                            if (length > 0) {
                                Log.e("taaag", "path======" + voiceRecorder.getVoiceFilePath());
                                tv_time.setText(Integer.toString(length) + " s");
                                voicTime = length;
                                relVoice.setVisibility(View.VISIBLE);
                                etContent.setFocusable(false);
                                etContent.setFocusableInTouchMode(false);
//                        setVoid(voiceRecorder.getVoiceFilePath(), Integer.toString(length));
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(NewTaskActivity.this, st1, Toast.LENGTH_SHORT).show();
                                isVoice = false;
                                relVoice.setVisibility(View.GONE);
                                etContent.setFocusable(true);
                                etContent.setFocusableInTouchMode(true);
                                etContent.requestFocus();
                            } else {
                                Toast.makeText(NewTaskActivity.this, st2, Toast.LENGTH_SHORT).show();
                                isVoice = false;
                                relVoice.setVisibility(View.GONE);
                                etContent.setFocusable(true);
                                etContent.setFocusableInTouchMode(true);
                                etContent.requestFocus();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppContext.toast(st3 + "" + e.toString());
                            isVoice = false;
                            relVoice.setVisibility(View.GONE);
                            etContent.setFocusable(true);
                            etContent.setFocusableInTouchMode(true);
                            etContent.requestFocus();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private TimeUtils myTime;
    private TextView tvSure;
    private TextView tvIsVoicIng;
    private ImageView ivVoicComp;
    private MyGiftView myGift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_task);
        mPermissionHelper = new PermissionHelper(NewTaskActivity.this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION03);
        Intent intent = getIntent();
        servicerType = intent.getStringExtra(Config.SERVICE_TYPE);
        myTime=new TimeUtils(handler);
        onBack();
        initView();
        setOnClickListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_task;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        voiceRecorder = new MyVoid(micImageHandler);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        etContent = (EditText) findViewById(R.id.etContent);
        relVoice = (RelativeLayout) findViewById(R.id.relVoice);
        ivVoics = (ImageView) findViewById(R.id.ivVoics);
        tv_time = (TextView) findViewById(R.id.tv_time);
        relVoic = (RelativeLayout) findViewById(R.id.relVoic);

        tvProjectName = (TextView) findViewById(R.id.tvProjectName);
        ivVoic = (ImageView) findViewById(R.id.ivVoic);
        ivSend = (ImageView) findViewById(R.id.ivSend);
    }

    private void setOnClickListener() {
        ivVoic.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        ivVoics.setOnClickListener(this);
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                     Log.e("EEE","----------->"+s.toString());
//                    ivVoics.setVisibility(View.GONE);
                    relVoic.setVisibility(View.GONE);
                }else {
//                    ivVoics.setVisibility(View.VISIBLE);
                    relVoic.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivVoic:
                String content = etContent.getText().toString().trim();
                if (!content.isEmpty()) {
                    AppContext.toast("语音和文字只能单一发送");
                    return;
                }
                if (!CommonUtils.isExitsSdcard()) {
                    String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                    AppContext.toast(st4);
                    return;
                }
                try {
                    v.setPressed(true);
                    wakeLock.acquire();
                    if (VoicePlayClickListener.isPlaying)
                        VoicePlayClickListener.currentPlayListener.stopPlayVoice();
//                        ivVoicerAnim.setVisibility(View.VISIBLE);
                    boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION03);
                    if (b) {
                        if (NewDoorFragment.list.get(NewDoorFragment.pos).getID().isEmpty() || NewDoorFragment.list.get(NewDoorFragment.pos).getID().equals("null")) {
                            voiceRecorder.startRecording(null, "service", NewTaskActivity.this);
                            isVoice = true;
                            showMyDialog();
                            myTime.runTimer();
                            etContent.setFocusable(false);
                            etContent.setFocusableInTouchMode(false);
                        } else {
                            voiceRecorder.startRecording(null, NewDoorFragment.list.get(NewDoorFragment.pos).getID(), NewTaskActivity.this);
                            isVoice = true;
                            showMyDialog();
                            myTime.runTimer();
                            etContent.setFocusable(false);
                            etContent.setFocusableInTouchMode(false);
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
//                        ivVoicerAnim.setVisibility(View.GONE);
                    AppContext.toast(R.string.recoding_fail + "e==" + e.toString());
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    etContent.setFocusable(true);
                    etContent.setFocusableInTouchMode(true);
                    etContent.requestFocus();
                    isVoice = false;
                    return;
                }

                break;
            case R.id.ivSend:
                if (!isVoice) {
                    String contentstr = etContent.getText().toString().trim();
                    if (!contentstr.isEmpty()) {
                        initTextSend(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), contentstr, servicerType, "1");
                    }else {
                        if (servicerType.equals("4")){
                            AppContext.toast("请输入服务需求");
                        }else {
                            initTextSend(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), "", servicerType, "1");
                        }
                    }
                } else {
                    File file = new File(voiceRecorder.getVoiceFilePath());
                    if (!file.exists()) {
                        AppContext.toast("未找到语音文件,请重新输入");
                        isVoice = false;
                        relVoice.setVisibility(View.GONE);
                    } else {//文件存在时才发送
                        byte[] fileData = fileData(voiceRecorder.getVoiceFilePath());
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
                        //4参 ：本文为1 ，语音为2
                        str = voicTime + "-2" + strs;
                        Log.e("taaag", str);
                        initVoicSend(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), str, servicerType, "2");
                    }
                }
                break;
            case R.id.ivVoics:
                try {
                    ivVoics.setImageResource(R.anim.voice_from_icon);
                    voiceAnimation = (AnimationDrawable) ivVoics.getDrawable();
                    voiceAnimation.start();
                    File file = new File(voiceRecorder.getVoiceFilePath());
                    Log.e("Tagg", "-------->path==" + voiceRecorder.getVoiceFilePath());
                    if (!file.exists()) {
                        AppContext.toast("未找到文件,请重新录制");
                        return;
                    }
                } catch (Exception e) {
                    Log.e("Taag","-------------eeee=="+e.toString());
                }
                playMusicByWav(voiceRecorder.getVoiceFilePath(),voiceAnimation,ivVoics);
                break;
        }
    }


    private void initTextSend(String room_id, String content, String type, String content_type) {
        HttpClient.instance().servicerAddTask(room_id, content, type, content_type, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(NewTaskActivity.this, "正在发送任务...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                etContent.setText("");
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode() == 1) {
                    AppContext.toast(data.getMsg());
                } else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }

    private void initVoicSend(String room_id, String content, String type, String content_type) {
        HttpClient.instance().servicerAddTask(room_id, content, type, content_type, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(NewTaskActivity.this, "正在发送任务...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                etContent.setFocusable(true);
                etContent.setFocusableInTouchMode(true);
                etContent.requestFocus();
                isVoice = false;
                relVoice.setVisibility(View.GONE);
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode() == 1) {
                    AppContext.toast(data.getMsg());
                } else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }

    private void showMyDialog() {
        dialog = new Dialog(mContext, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.voicedialog, null);
        dialog.setContentView(view);
        tvIsVoicIng = (TextView) view.findViewById(R.id.tvIsVoicIng);
        tvSure = (TextView) view.findViewById(R.id.tvSure);
        ivVoicComp = (ImageView) view.findViewById(R.id.ivVoicComp);
        myGift = (MyGiftView) view.findViewById(R.id.myGift);

        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVoice = false;
                relVoice.setVisibility(View.GONE);
                etContent.setFocusable(true);
                etContent.setFocusableInTouchMode(true);
                etContent.requestFocus();
                if (voiceRecorder != null)
                    voiceRecorder.discardRecording();
                myTime.cancelRun();
                dialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContent.setHint("");
                if (tvSure.getText().toString().equals(getString(R.string.GoToSave))){
                    dialog.dismiss();
                    return;
                }
                v.setPressed(false);
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                isVoice = true;
                String st1 = getResources().getString(R.string.Recording_without_permission);
                String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                String st3 = getResources().getString(R.string.send_failure_please);
                try {
                    int length = voiceRecorder.stopRecoding();
                    dialog.dismiss();
                    myTime.cancelRun();
                    if (length > 0) {
                        Log.e("taaag", "path======" + voiceRecorder.getVoiceFilePath());
                        tv_time.setText(Integer.toString(length) + " s");
                        voicTime = length;
                        relVoice.setVisibility(View.VISIBLE);
                        etContent.setFocusable(false);
                        etContent.setFocusableInTouchMode(false);
//                        setVoid(voiceRecorder.getVoiceFilePath(), Integer.toString(length));
                    } else if (length == EMError.INVALID_FILE) {
                        Toast.makeText(NewTaskActivity.this, st1, Toast.LENGTH_SHORT).show();
                        isVoice = false;
                        relVoice.setVisibility(View.GONE);
                        etContent.setFocusable(true);
                        etContent.setFocusableInTouchMode(true);
                        etContent.requestFocus();
                    } else {
                        Toast.makeText(NewTaskActivity.this, st2, Toast.LENGTH_SHORT).show();
                        isVoice = false;
                        relVoice.setVisibility(View.GONE);
                        etContent.setFocusable(true);
                        etContent.setFocusableInTouchMode(true);
                        etContent.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.toast(st3 + "" + e.toString());
                    isVoice = false;
                    relVoice.setVisibility(View.GONE);
                    etContent.setFocusable(true);
                    etContent.setFocusableInTouchMode(true);
                    etContent.requestFocus();
                }

            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (voiceRecorder.isRecording()) {//还在录音中
                    myTime.cancelRun();
//                    v.setPressed(false);
                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                    }
                    isVoice = true;
                    String st1 = getResources().getString(R.string.Recording_without_permission);
                    String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                    String st3 = getResources().getString(R.string.send_failure_please);
                    try {
                        int length = voiceRecorder.stopRecoding();
                        if (length > 0) {
                            Log.e("taaag", "path======" + voiceRecorder.getVoiceFilePath());
                            tv_time.setText(Integer.toString(length) + " s");
                            voicTime = length;
                            relVoice.setVisibility(View.VISIBLE);
                            etContent.setFocusable(false);
                            etContent.setFocusableInTouchMode(false);
//                        setVoid(voiceRecorder.getVoiceFilePath(), Integer.toString(length));
                        } else if (length == EMError.INVALID_FILE) {
                            Toast.makeText(NewTaskActivity.this, st1, Toast.LENGTH_SHORT).show();
                            isVoice = false;
                            relVoice.setVisibility(View.GONE);
                            etContent.setFocusable(true);
                            etContent.setFocusableInTouchMode(true);
                            etContent.requestFocus();
                        } else {
                            Toast.makeText(NewTaskActivity.this, st2, Toast.LENGTH_SHORT).show();
                            isVoice = false;
                            relVoice.setVisibility(View.GONE);
                            etContent.setFocusable(true);
                            etContent.setFocusableInTouchMode(true);
                            etContent.requestFocus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppContext.toast(st3 + "" + e.toString());
                        isVoice = false;
                        relVoice.setVisibility(View.GONE);
                        etContent.setFocusable(true);
                        etContent.setFocusableInTouchMode(true);
                        etContent.requestFocus();
                    }

                }
            }
        });
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
                } else {
                    //如果请求失败
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

}
