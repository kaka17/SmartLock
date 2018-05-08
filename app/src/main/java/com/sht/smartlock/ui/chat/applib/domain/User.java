/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sht.smartlock.ui.chat.applib.domain;

import com.easemob.chat.EMContact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User extends EMContact {
	private int unreadMsgCount;
	private String header;
	private String avatar;
	private int img_Num=-1;//记录聊天室会员的头像是哪个

	//自定义添加属性
	private String id_image;
	private String name;//姓名 或者说是昵称
	private String emid;//环信账号

	
	public User(){}
	
	public User(String username){
	    this.username = username;
	}

	public int getImg_Num() {
		return img_Num;
	}

	public void setImg_Num(int img_Num) {
		this.img_Num = img_Num;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}
	
	

	public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}

	public String getId_image() {
		return id_image;
	}

	public void setId_image(String id_image) {
		this.id_image = id_image;
		this.avatar=id_image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		//设置昵称
		this.nick=name;
	}

	public String getEmid() {
		return emid;
	}

	public void setEmid(String emid) {
//		StringBuffer sb = new StringBuffer();
//		if(emid!=null){
//
////			Pattern p=Pattern.compile("[a-zA-Z]");
////			Matcher m =
////			m=p.matcher(txt);
////			if(m.matches()){
////				Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
////			}
//
//			for(int i=0;i<emid.length();i++){
//				char c = emid.charAt(i);
//				if(Character.isUpperCase(c)){
//					sb.append(Character.toLowerCase(c));
//				}else if(Character.isLowerCase(c)){
//					sb.append(c);
//				}
//			}
//		}
//		emid=sb.toString();

		this.emid = emid;
		this.username=emid;
	}
}
