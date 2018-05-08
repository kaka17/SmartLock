/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui.chatting.model;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.ui.chatting.ChattingActivity;
import com.sht.smartlock.phone.ui.chatting.holder.BaseHolder;
import com.sht.smartlock.phone.ui.chatting.holder.VoiceRowViewHolder;
import com.sht.smartlock.phone.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;

/**
 * <p>Title: VoiceRxRow.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Beijing Speedtong Information Technology Co.,Ltd</p>
 * @author Jorstin Chan
 * @date 2014-4-17
 * @version 1.0
 */
public class VoiceRxRow extends BaseChattingRow {

	public VoiceRxRow(int type) {
		super(type);
	}
	
	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_from_voice);
            
            //use the view holder pattern to save of already looked up subviews
            VoiceRowViewHolder holder = new VoiceRowViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        } 
		return convertView;
	}

	@Override
	public void buildChattingData(Context context, BaseHolder baseHolder,
			final ECMessage detail, int position) {
		
		VoiceRowViewHolder holder = (VoiceRowViewHolder) baseHolder;
        if(detail != null) {
        	VoiceRowViewHolder.initVoiceRow(holder, detail, position, (ChattingActivity) context, true);
        	holder.voiceAnim.setVoiceFrom(true);
        }
	}
	

	@Override
	public int getChatViewType() {

		return ChattingRowType.VOICE_ROW_RECEIVED.ordinal();
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, ECMessage detail) {
		// TODO Auto-generated method stub
		return false;
	}

}
