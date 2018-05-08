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
package com.sht.smartlock.phone.ui.videomeeting;

import com.yuntongxun.ecsdk.meeting.ECVideoMeetingMember;


/**
 * <p>Title: MulitVideoMember.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Beijing Speedtong Information Technology Co.,Ltd</p>
 * @author Jorstin Chan
 * @date 2014-8-28
 * @version 1.0
 */
public class MulitVideoMember extends ECVideoMeetingMember {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2095128100173227953L;

	private boolean publish = true;
	private boolean requestVideoFrame;
	private int width;
	private int height;
	private boolean isMobile;
	
	/**
	 * 
	 */
	public MulitVideoMember() {

	}
	
	/**
	 * 
	 */
	public MulitVideoMember(ECVideoMeetingMember member) {
		setMeetingNo(member.getMeetingNo());
		
		setNumber(member.getNumber());
		
		setType(member.getType());
		setPort(member.getPort());
		setIp(member.getIp());
		
		setPublish(member.isPublish());
		publish = true;
		requestVideoFrame = false;
	}
	
	/**
	 * @return the publish
	 */
	public boolean isPublish() {
		return publish;
	}
	/**
	 * @param publish the publish to set
	 */
	public void setPublish(boolean publish) {
		this.publish = publish;
	}
	/**
	 * @return the requestVideoFrame
	 */
	public boolean isRequestVideoFrame() {
		return requestVideoFrame;
	}
	/**
	 * @param requestVideoFrame the requestVideoFrame to set
	 */
	public void setRequestVideoFrame(boolean requestVideoFrame) {
		this.requestVideoFrame = requestVideoFrame;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

}
