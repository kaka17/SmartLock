/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui.chatting.holder;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sht.smartlock.R;


/**
 * @author 容联•云通讯
 * @date 2014-12-9
 * @version 4.0
 */
public class FileRowViewHolder extends BaseHolder {

	public TextView contentTv;
	public ImageView ivVideoMp4;
	
	public FrameLayout fl;
	public Button buPlayVideo;
	
	public TextView tvFile;
	/**
	 * @param type
	 */
	public FileRowViewHolder(int type) {
		super(type);

	}
	
	public BaseHolder initBaseHolder(View baseView , boolean receive) {
		super.initBaseHolder(baseView);
		
		chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
		chattingUser = ((TextView) baseView.findViewById(R.id.chatting_user_tv));
		checkBox = ((CheckBox) baseView.findViewById(R.id.chatting_checkbox));
		chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
		uploadState = ((ImageView) baseView.findViewById(R.id.chatting_state_iv));
		contentTv = ((TextView) baseView.findViewById(R.id.chatting_content_itv));
		
		ivVideoMp4=(ImageView) baseView.findViewById(R.id.iv_file_mp4);
		fl=(FrameLayout) baseView.findViewById(R.id.fl_chatting_video);
		buPlayVideo=(Button) baseView.findViewById(R.id.btn_play_video);
		tvFile=(TextView) baseView.findViewById(R.id.tv_filesize);
		if(receive) {
			type = 3;
			return this;
		}
		
		progressBar = (ProgressBar) baseView.findViewById(R.id.uploading_pb);
		type = 4;
		return this;
	}

}
