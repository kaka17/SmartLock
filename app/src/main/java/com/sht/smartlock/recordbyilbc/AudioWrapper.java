package com.sht.smartlock.recordbyilbc;


public class AudioWrapper {

	private AudioRecorder audioRecorder;
//	private AudioReceiver audioReceiver;

	private static AudioWrapper instanceAudioWrapper;

	private static boolean isStop=false;

	private AudioWrapper() {
	}

	public static AudioWrapper getInstance() {
		if (null == instanceAudioWrapper) {
			instanceAudioWrapper = new AudioWrapper();
		}
		return instanceAudioWrapper;
	}

	public void startRecord() {
		if (null == audioRecorder) {
			audioRecorder = new AudioRecorder();
		}
		audioRecorder.startRecording();
	}

	public AudioRecorder getAudioRecorder(){
		return audioRecorder;
	}

	public void stopRecord() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}



	public void stopListen() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}


	/*
     * stop
     */
	public void setIsPlayStop(boolean isPlayStop) {
		this.isStop = isPlayStop;
	}


	public boolean getIsPlayStop(){

		return  isStop;
	}
}
