package com.sht.smartlock.phone.ui.chatting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.CommomUtil;
import com.sht.smartlock.phone.common.utils.FileUtils;
import com.sht.smartlock.phone.common.utils.MimeTypesTools;
import com.sht.smartlock.phone.ui.ECSuperActivity;

import java.io.File;
import java.io.IOException;

public class VideoRecordActivity extends ECSuperActivity implements
		OnClickListener, SurfaceHolder.Callback, OnInfoListener,
		OnErrorListener {
	private static final String TAG = "VideoRecordActivity";
	private VideoView mVideoView;
	private Button btn_switch;
	private int frontCamera = 0;// 0是backCamera 1是frontCamera
	private MediaRecorder mediaRecorder;
	private Button btn_start;
	private View btn_retry;
	private File tempFile;
	private Chronometer chronometer;
	private ImageView btn_send;
	private View btn_play;
	private Camera mCamera;
	private SurfaceHolder mSurfaceHolder;
	private ImageView imageview;
	private Button btn_stop;
	private boolean isPlaying;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
				R.drawable.btn_style_green, null,
				getString(R.string.video_send),
				getString(R.string.video_record), null, this);

		initViews();


	}

	@Override
	protected void onResume() {
		super.onResume();
		startTime = 0;
		endTime = 0;
		if (!initCamera())
			finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		stopRecording();
	}



	@SuppressLint("NewApi")
	private boolean initCamera() {

		try {
			if (frontCamera == 0) {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			} else {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			}
			Camera.Parameters camParams = mCamera.getParameters();
			mCamera.lock();
			mSurfaceHolder = mVideoView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mCamera.setDisplayOrientation(90);
		} catch (RuntimeException re) {
			Log.v(TAG, "Could not initialize the Camera");
			re.printStackTrace();
			return false;
		}
		return true;
	}

	private void initViews() {
		btn_stop = (Button) findViewById(R.id.stop);
		btn_stop.setOnClickListener(this);
		btn_switch = (Button) findViewById(R.id.switch_btn);
		btn_switch.setOnClickListener(this);
		imageview = (ImageView) findViewById(R.id.imageview);
		mVideoView = (VideoView) findViewById(R.id.surface_video_record);

		mSurfaceHolder = mVideoView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		chronometer = (Chronometer) findViewById(R.id.chronometer);
		btn_play = findViewById(R.id.btn_play);
		btn_play.setOnClickListener(this);
		btn_retry = findViewById(R.id.btn_retry);
		btn_retry.setOnClickListener(this);
		btn_start = (Button) findViewById(R.id.start);
		btn_start.setOnClickListener(this);
		btn_send = getTopBarView().getRightButton();

		btn_send.setEnabled(false);

	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null)
			initCamera();
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.v(TAG, "Could not start the preview");
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		Log.v(TAG, "surfaceChanged: Width x Height = " + width + "x" + height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			releaseCamera();
		}
		Log.v(TAG, "surfaceDestroyed ");
	}

	@SuppressLint("NewApi")
	public void startRecording() {

		if (mediaRecorder == null)
			initRecorder();
		mediaRecorder.setOnInfoListener(this);
		mediaRecorder.setOnErrorListener(this);
		mediaRecorder.start(); // ???
	}

	@SuppressLint("NewApi")
	private void initRecorder() {
		if (mCamera == null)
			initCamera();

		mVideoView.setVisibility(View.VISIBLE);
		imageview.setVisibility(View.GONE);
		btn_start.setVisibility(View.VISIBLE);
		btn_retry.setVisibility(View.INVISIBLE);

		mCamera.stopPreview();
		mediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);

		// init
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		if (frontCamera == 1) {
			mediaRecorder.setOrientationHint(270);
		} else {
			mediaRecorder.setOrientationHint(90);// 视频旋转
		}
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// other work
		tempFile = CommomUtil.TackVideoFilePath();
		// 设置视频编码方式
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置音频编码方式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediaRecorder.setOutputFile(tempFile.getPath());
		mediaRecorder.setMaxDuration(30000);
		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		// prepare
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stopRecording() {
		if (mediaRecorder != null) {
			mediaRecorder.setOnErrorListener(null);
			mediaRecorder.setOnInfoListener(null);
			try {
				mediaRecorder.stop();
			} catch (IllegalStateException e) {
				// This can happen if the recorder has already stopped.
				Log.e(TAG, "Got IllegalStateException in stopRecording");
			}
		}
		releaseRecorder();

		if (mCamera != null) {
			mCamera.stopPreview();
			releaseCamera();
		}

	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private void releaseRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	private long startTime = 0;
	private long endTime = 0;

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.start || v.getId() == R.id.stop
				|| v.getId() == R.id.btn_retry) {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			} else {

				endTime = System.currentTimeMillis();

				if (endTime - startTime < 1500) {
					return;
				}
				startTime = System.currentTimeMillis();

			}

		}

		switch (v.getId()) {

			case R.id.switch_btn: {

				flipit();
				break;
			}
			case R.id.btn_left:
				hideSoftKeyboard();
				stopRecording();
				finish();

				break;
			case R.id.text_right:

				if (btn_send.isEnabled()) {
					handleVideoSend();
				}
				break;

			case R.id.btn_play: {
				if (tempFile != null) {

					snedFilePrevieIntent(tempFile.getAbsolutePath());
				}
				break;
			}

			case R.id.start: {
				// 开始拍摄 按钮从开始拍摄 切到停止拍摄
				startRecording();
				btn_switch.setVisibility(View.INVISIBLE);
				btn_start.setVisibility(View.INVISIBLE);
				btn_stop.setVisibility(View.VISIBLE);
				// 重置其他
				btn_play.setVisibility(View.INVISIBLE);
				chronometer.setBase(SystemClock.elapsedRealtime());
				// to do
				chronometer.start();
				btn_send.setEnabled(false);
			}
			break;
			case R.id.stop: {
				// 点击停止拍摄 状态切换到重新拍照 和预览
				stopRecording();

				btn_retry.setVisibility(View.VISIBLE);
				btn_stop.setVisibility(View.INVISIBLE);

				mVideoView.setVisibility(View.GONE);
				btn_switch.setVisibility(View.INVISIBLE);
				btn_play.setVisibility(View.VISIBLE);
				btn_send.setEnabled(true);

				chronometer.stop();
				imageview.setVisibility(View.VISIBLE);
				if (tempFile == null)
					return;

				Bitmap createVideoThumbnail = FileUtils //生成缩图
						.createVideoThumbnail(tempFile.getAbsolutePath());
				if (createVideoThumbnail != null) {
					imageview.setImageBitmap(createVideoThumbnail);
					// saveBitmapFile(createVideoThumbnail);
				}

				break;
			}
			case R.id.btn_retry: {
				if (isPlaying) {
					return;
				}

				// 切换回预览
				mVideoView.setMediaController(null);
				imageview.setVisibility(View.GONE);
				mVideoView.setVisibility(View.VISIBLE);
				btn_switch.setVisibility(View.VISIBLE);
				btn_start.setVisibility(View.VISIBLE);
				btn_play.setVisibility(View.INVISIBLE);
				btn_stop.setVisibility(View.INVISIBLE);
				btn_retry.setVisibility(View.INVISIBLE);
				if (mCamera == null)
					initCamera();
				try {
					mCamera.setPreviewDisplay(mSurfaceHolder);
					mCamera.startPreview();
				} catch (IOException e) {
					Log.v(TAG, "Could not start the preview");
					e.printStackTrace();
				}
				break;
			}
		}

	}

	private void handleVideoSend() {

		if (tempFile == null) {
			return;
		}



		Intent intent = new Intent();
		intent.putExtra("file_name", tempFile.getName());
		intent.putExtra("file_url", tempFile.getAbsolutePath());
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_chat_videorecord;
	}

	@SuppressLint("NewApi")
	public void flipit() {
		// mCamera is the Camera object
		if (mCamera == null) {
			return;
		}
		if (Camera.getNumberOfCameras() >= 2) {
			btn_switch.setEnabled(false);
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			// "which" is just an integer flag
			switch (frontCamera) {
				case 0:
					mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
					frontCamera = 1;
					break;
				case 1:
					mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
					frontCamera = 0;
					break;
			}
			try {
				// Camera.Parameters parameters = mCamera.getParameters();
				mCamera.lock();
				mCamera.setDisplayOrientation(90);
				// mCamera.setParameters(parameters);
				mCamera.setPreviewDisplay(mVideoView.getHolder());
				mCamera.startPreview();
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
			}
			btn_switch.setEnabled(true);
		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		Log.i(TAG, "got a recording event");
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			Log.i(TAG, "...max duration reached");
			stopRecording();

			btn_retry.setVisibility(View.VISIBLE);
			btn_stop.setVisibility(View.INVISIBLE);

			mVideoView.setVisibility(View.GONE);
			btn_switch.setVisibility(View.INVISIBLE);
			btn_play.setVisibility(View.VISIBLE);
			btn_send.setEnabled(true);

			chronometer.stop();
			if (tempFile == null)
				return;

			imageview.setVisibility(View.VISIBLE);
			Bitmap createVideoThumbnail = FileUtils
					.createVideoThumbnail(tempFile.getAbsolutePath());
			if (createVideoThumbnail != null) {
				imageview.setImageBitmap(createVideoThumbnail);
			}
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		Log.e(TAG, "got a recording error");
		stopRecording();
		Toast.makeText(this,
				"Recording error has occurred. Stopping the recording",
				Toast.LENGTH_SHORT).show();
	}

	void snedFilePrevieIntent(String fileName) {
		String type = "";
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			type = MimeTypesTools
					.getMimeType(getApplicationContext(), fileName);
			File file = new File(fileName);
			intent.setDataAndType(Uri.fromFile(file), type);
			startActivity(intent);
		} catch (Exception e) {
			System.out
					.println("android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.VIEW dat=file:///mnt/sdcard/xxx typ="
							+ type + " flg=0x10000000");
		}
	}



}
