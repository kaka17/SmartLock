package com.sht.smartlock.api.base;

public abstract class HttpCallBack {
	public void onStart() { }

	public abstract void onSuccess(ResponseBean responseBean);

	public void onFailure(String error, String message){ }

	public void onFinish() { }
}