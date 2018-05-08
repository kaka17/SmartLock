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
package com.sht.smartlock.phone.ui.group;


import android.text.TextUtils;

import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.storage.ContactSqlManager;
import com.sht.smartlock.phone.storage.GroupMemberSqlManager;
import com.sht.smartlock.phone.storage.GroupNoticeSqlManager;
import com.sht.smartlock.phone.storage.GroupSqlManager;
import com.sht.smartlock.phone.storage.IMessageSqlManager;
import com.sht.smartlock.phone.ui.contact.ECContacts;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.ecsdk.im.group.ECChangeAdminMsg;
import com.yuntongxun.ecsdk.im.group.ECChangeMemberRoleMsg;
import com.yuntongxun.ecsdk.im.group.ECDismissGroupMsg;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;
import com.yuntongxun.ecsdk.im.group.ECInviterMsg;
import com.yuntongxun.ecsdk.im.group.ECJoinGroupMsg;
import com.yuntongxun.ecsdk.im.group.ECModifyGroupMsg;
import com.yuntongxun.ecsdk.im.group.ECProposerMsg;
import com.yuntongxun.ecsdk.im.group.ECQuitGroupMsg;
import com.yuntongxun.ecsdk.im.group.ECRemoveMemberMsg;
import com.yuntongxun.ecsdk.im.group.ECReplyInviteGroupMsg;
import com.yuntongxun.ecsdk.im.group.ECReplyJoinGroupMsg;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jorstin Chan@容联•云通讯
 * @date 2014-12-31
 * @version 4.0
 */
public class GroupNoticeHelper {

	/**群组通知接口*/
	private OnPushGroupNoticeMessageListener mListener;
	
	private static GroupNoticeHelper mHelper;
	private static GroupNoticeHelper getHelper() {
		if(mHelper == null) {
			mHelper = new GroupNoticeHelper();
		}
		return mHelper;
	} 
	
	/**
	 * 处理群组通知消息
	 * @param notice
	 */
	public static void handleGroupNoticeMessage(ECGroupNoticeMessage notice , OnPushGroupNoticeMessageListener l) {
		ECGroupNoticeMessage.ECGroupMessageType type = notice.getType();
		NoticeSystemMessage message = null;
		if(type == ECGroupNoticeMessage.ECGroupMessageType.PROPOSE) {
			// 群组收到有人申请加入群组
			message = getHelper().onIMProposerMsg((ECProposerMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.PROPOSE) {
			// 群组管理员通过或拒绝用户加入群组申请
			message = getHelper().onIMReplyGroupApplyMsg((ECReplyJoinGroupMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.INVITE) {
			// 群组管理员邀请用户加入群组 - 
			message = getHelper().onIMInviterMsg((ECInviterMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.REMOVE_MEMBER) {
			// 群组管理员删除成员
			message = getHelper().onIMRemoveMemeberMsg((ECRemoveMemberMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.QUIT) {
			// 群组成员主动退出群组 
			message = getHelper().onIMQuitGroupMsg((ECQuitGroupMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.DISMISS) {
			// 删除群组（解散群组）
			message = getHelper().onIMGroupDismissMsg((ECDismissGroupMsg) notice);
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.JOIN) {

		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.REPLY_INVITE) {
			// 用户通过或拒绝群组管理员邀请加入群组的邀请
			message =getHelper().onIMInviterJoinGroupReplyMsg((ECReplyInviteGroupMsg) notice);
		}
		
		/*if(message != null) {
			GroupNoticeSqlManager.insertNoticeMsg(message);
			getHelper().notify(message);
			if(l != null) {
				l.onPushGroupNoticeMessage(message);
			}
		}*/
	}

	/**
	 * 用户申请加入群组 - PUSH到群组管理员
	 * @param notice
	 */
	private NoticeSystemMessage onIMProposerMsg(ECProposerMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		systemMessage.setMember(notice.getProposer());
		if(notice.isDiscuss()){
			
			systemMessage.setContent("<member>"+notice.getProposer()+ "</member> 加入了讨论组");
		}else {
			
			systemMessage.setContent("<member>"+notice.getProposer()+ "</member> 加入了群组");
		}
		systemMessage.setDateCreated(notice.getDateCreated());
		
		GroupService.syncGroupInfo(notice.getGroupId());
		return systemMessage;
		
	}

	/**
	 * 群组管理员通过或拒绝用户加入群组申请
	 * @param notice
	 */
	private NoticeSystemMessage onIMReplyGroupApplyMsg(ECReplyJoinGroupMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		systemMessage.setAdmin(notice.getAdmin());
		systemMessage.setConfirm(notice.getConfirm());
		if(notice.getConfirm() == 0) {
			
			if(notice.isDiscuss()){
				
				systemMessage.setContent("讨论组管理员<admin>"+notice.getAdmin()+ "</admin>拒绝<member>"+notice.getMember()+ "</member>加入讨论申请");
			}else {
			systemMessage.setContent("群管理员<admin>"+notice.getAdmin()+ "</admin>拒绝<member>"+notice.getMember()+ "</member>加入群组申请");
			}
		} else {
			
			if(notice.isDiscuss()){
				
				systemMessage.setContent("讨论组管理员<admin>"+notice.getAdmin()+ "</admin>通过<member>"+notice.getMember()+ "</member>加入讨论组申请");
			}else {
				
				systemMessage.setContent("群管理员<admin>"+notice.getAdmin()+ "</admin>通过<member>"+notice.getMember()+ "</member>加入群组申请");
			}
			
		}
		systemMessage.setMember(notice.getMember());
		return systemMessage;
	}

	/**
	 * 群组管理员邀请用户加入群组 - 
	 * PUSH到被邀请的用户
	 * @param notice
	 */
	private NoticeSystemMessage onIMInviterMsg(ECInviterMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		systemMessage.setAdmin(notice.getAdmin());
		if(notice.isDiscuss()){
			
			systemMessage.setContent("讨论组管理员<admin>"+notice.getAdmin()+ "</admin>邀请您加入讨论组");
		}else {
		systemMessage.setContent("群管理员<admin>"+notice.getAdmin()+ "</admin>邀请您加入群组");
		}
		systemMessage.setConfirm(notice.getConfirm());
		return systemMessage;
	}

	/**
	 * 群组管理员删除成员
	 * @param notice
	 */
	private NoticeSystemMessage onIMRemoveMemeberMsg(ECRemoveMemberMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		systemMessage.setMember(notice.getMember());
		
		if(notice.isDiscuss()){
			
			systemMessage.setContent("<member>"+notice.getMember()+ "</member>被移除出讨论组 " + "<groupId>"+notice.getGroupId()+"</groupId>");
		}else {
			
			systemMessage.setContent("<member>"+notice.getMember()+ "</member>被移除出群组 " + "<groupId>"+notice.getGroupId()+"</groupId>");
		}
		systemMessage.setGroupId(notice.getGroupId());
		return systemMessage;
	}

	/**
	 * 群组成员主动退出群组 
	 * - PUSH到所有用户
	 * @param notice
	 */
	private NoticeSystemMessage onIMQuitGroupMsg(ECQuitGroupMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		
		if(notice.isDiscuss()){
			
			systemMessage.setContent("讨论组成员<member>"+notice.getMember()+ "</member>退出了讨论组 " + "<groupId>"+notice.getGroupId()+"</groupId>");
		}else {
			systemMessage.setContent("群成员<member>"+notice.getMember()+ "</member>退出了群组 " + "<groupId>"+notice.getGroupId()+"</groupId>");
			
		}
		systemMessage.setMember(notice.getMember());
		return systemMessage;
	}

	/**
	 * 删除群组（解散群组）
	 * - PUSH到群组的所有用户
	 * @param notice
	 */
	private NoticeSystemMessage onIMGroupDismissMsg(ECDismissGroupMsg notice) {
		NoticeSystemMessage systemMessage = createNoticeSystemMessage(notice);
		
		if(notice.isDiscuss()){
			
			systemMessage.setContent("讨论组被解散");
		}else {
			
			systemMessage.setContent("群组被解散");
		}
		systemMessage.setGroupId(notice.getGroupId());
		return systemMessage;
	}

	/**
	 * 用户通过或拒绝群组管理员邀请加入群组的申请
	 *  – 通过PUSH到所有用户，拒绝PUSH到群组管理员
	 * @param notice
	 */
	private NoticeSystemMessage onIMInviterJoinGroupReplyMsg( ECReplyInviteGroupMsg notice) {
		return null;
	}
	
	/**
	 * 生成群组通知消息
	 * @return
	 */
	private NoticeSystemMessage createNoticeSystemMessage(ECGroupNoticeMessage notice) {
		NoticeSystemMessage message = new NoticeSystemMessage(notice.getType());
		message.setGroupId(notice.getGroupId());
		message.setIsRead(IMessageSqlManager.IMESSENGER_TYPE_UNREAD);
		return message;
	}
	
	public static void addListener(OnPushGroupNoticeMessageListener listener) {
		getHelper().mListener = listener;
	}
	
	/**
	 * @param content
	 * @return
	 */
	public static CharSequence getNoticeContent(String content) {
		if(content == null) {
			return content;
		}
		if(content.indexOf("<admin>") != -1 && content.indexOf("</admin>") != -1) {
			int start = content.indexOf("<admin>");
			int end = content.indexOf("</admin>");
			String contactId = content.substring(start + "<admin>".length(), end);
			ECContacts contact = ContactSqlManager.getContact(contactId);
			String target = content.substring(start, end + "</admin>".length());
			content = content.replace(target, contact.getNickname());
		} 
		if(content.indexOf("<member>") != -1 && content.indexOf("</member>") != -1) {
			int start = content.indexOf("<member>");
			int end = content.indexOf("</member>");
			String member = content.substring(start + "<member>".length(), end);
			ECContacts contact = ContactSqlManager.getContact(member);
			String target = content.substring(start, end + "</member>".length());
			content = content.replace(target, contact.getNickname());
		}
		if(content.indexOf("<groupId>") != -1 && content.indexOf("</groupId>") != -1) {
			int start = content.indexOf("<groupId>");
			int end = content.indexOf("</groupId>");
			String groupId = content.substring(start + "<groupId>".length(), end);
			ECGroup ecGroup = GroupSqlManager.getECGroup(groupId);
			String target = content.substring(start, end + "</groupId>".length());
			if(ecGroup == null) {
				GroupService.syncGroupInfo(groupId);
			}
			content = content.replace(target, ecGroup!= null ? ecGroup.getName() : "");
		}
		return content;
	}



    /***********************************************************************************************/

    public static final int SYSTEM_MESSAGE_NEED_REPLAY = 1;
    public static final int SYSTEM_MESSAGE_NONEED_REPLAY = 2;
    public static final int SYSTEM_MESSAGE_THROUGH = 3;
    public static final int SYSTEM_MESSAGE_REFUSE = 4;

    public static void insertNoticeMessage(ECGroupNoticeMessage instanceMsg ,OnPushGroupNoticeMessageListener l)  {

		DemoGroupNotice demoGroupNotice = new DemoGroupNotice(instanceMsg.getType().ordinal());
        String verifyMsg = "";
        int state = SYSTEM_MESSAGE_NONEED_REPLAY;
		boolean needSync = false;
		ECGroupNoticeMessage.ECGroupMessageType type = instanceMsg.getType();
		if(type == ECGroupNoticeMessage.ECGroupMessageType.JOIN) {// 直接加入
			ECJoinGroupMsg joinMsg = (ECJoinGroupMsg) instanceMsg;
			
			if(TextUtils.isEmpty(joinMsg.getNickName())) {
				
				if(joinMsg.isDiscuss()){
					
					verifyMsg = "[" + joinMsg.getMember() + "] 加入了讨论组";
				}else {
					
					verifyMsg = "[" + joinMsg.getMember() + "] 加入了群组";
				}
			} else {
				if(joinMsg.isDiscuss()){
					
					verifyMsg = "[" + joinMsg.getNickName() + "] 加入了讨论组";
				}else {
					
					verifyMsg = "[" + joinMsg.getNickName() + "] 加入了群组";
				}
			}
			demoGroupNotice.setMember(joinMsg.getMember());
			demoGroupNotice.setNickName(joinMsg.getNickName());
			demoGroupNotice.setDeclared(joinMsg.getDeclared());
			needSync = CCPAppManager.getUserId().equals(joinMsg.getMember());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.PROPOSE) {// 申请加入
			ECProposerMsg po = (ECProposerMsg) instanceMsg;
            state = SYSTEM_MESSAGE_NEED_REPLAY;
            if(TextUtils.isEmpty(po.getNickName())) {
            	
            	if(po.isDiscuss()){
            		
            		verifyMsg = "["+ po.getProposer() + "]申请加入讨论组" + instanceMsg.getGroupName();
            	}else {
            		verifyMsg = "["+ po.getProposer() + "]申请加入群组" + instanceMsg.getGroupName();
            		
            	}
            } else {
            	
            	if(po.isDiscuss()){
            		
            		verifyMsg = "[" + po.getNickName()+ "]申请加入讨论组" + instanceMsg.getGroupName();
            	
            	}else {
            		
            		verifyMsg = "[" + po.getNickName()+ "]申请加入群组" + instanceMsg.getGroupName();
            	}
            	
            }
			demoGroupNotice.setMember(po.getProposer());
			demoGroupNotice.setNickName(po.getNickName());
			demoGroupNotice.setDeclared(po.getDeclared());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.INVITE) {// 邀请加入
			ECInviterMsg invireMsg = (ECInviterMsg) instanceMsg;
			
			
			
			if(TextUtils.isEmpty(invireMsg.getNickName())) {
				
				if(invireMsg.isDiscuss()){
					verifyMsg = "[" + invireMsg.getAdmin() + "]邀请你加入讨论组 [" + instanceMsg.getGroupName() + "]";
					
				}else {
					
					verifyMsg = "[" + invireMsg.getAdmin() + "]邀请你加入群组 [" + instanceMsg.getGroupName() + "]";
				}
			} else {
				
				if(invireMsg.isDiscuss()){
					
					verifyMsg = "[" + invireMsg.getNickName() + "]邀请你加入讨论组 [" + instanceMsg.getGroupName() + "]";
				}else {
					verifyMsg = "[" + invireMsg.getNickName() + "]邀请你加入群组 [" + instanceMsg.getGroupName() + "]";
					
				}
			}
			ECGroup group = new ECGroup();
			group.setGroupId(instanceMsg.getGroupId());
			group.setName(instanceMsg.getGroupName());
			demoGroupNotice.setAdmin(invireMsg.getAdmin());
			demoGroupNotice.setNickName(invireMsg.getNickName());
			demoGroupNotice.setConfirm(invireMsg.getConfirm());
			demoGroupNotice.setDeclared(invireMsg.getDeclared());
			if(((ECInviterMsg) instanceMsg).getConfirm() == 2) {
				state = SYSTEM_MESSAGE_NEED_REPLAY;
				GroupSqlManager.insertGroup(group, false, false,invireMsg.isDiscuss()?true:false);
			} else {
				GroupSqlManager.insertGroup(group, true, false,invireMsg.isDiscuss()?true:false);
			}
		} else if (type == ECGroupNoticeMessage.ECGroupMessageType.REPLY_JOIN) { // 确认申请
			ECReplyJoinGroupMsg joinMsg = (ECReplyJoinGroupMsg) instanceMsg;
			if(TextUtils.isEmpty(joinMsg.getNickName())) {
				joinMsg.setNickName(joinMsg.getMember());
			}
            if(CCPAppManager.getClientUser().getUserId().equals(joinMsg.getMember())) {
            	
            	
            	if(joinMsg.isDiscuss()){
            		
            		verifyMsg = joinMsg.getConfirm() == 2 ? "管理员通过了您的加入讨论组请求":"管理员拒绝了您的加入讨论组请求";
            	}else {
            		
            		verifyMsg = joinMsg.getConfirm() == 2 ? "管理员通过了您的加群请求":"管理员拒绝了您的加群请求";
            	}
                if(joinMsg.getConfirm() == 2) {
					GroupSqlManager.updateJoinStatus(instanceMsg.getGroupId(),
							true);
                }
				needSync = true;
            } else {
            	
            	if(joinMsg.isDiscuss()){
            		
            		verifyMsg = joinMsg.getConfirm() == 2 ? "管理员通过了[" + joinMsg.getNickName() + "]的加入讨论组请求":"管理员拒绝了[" + joinMsg.getNickName() + "]的加入讨论组请求";
            	}else {
            		
            		verifyMsg = joinMsg.getConfirm() == 2 ? "管理员通过了[" + joinMsg.getNickName() + "]的加群请求":"管理员拒绝了[" + joinMsg.getNickName() + "]的加群请求";
            	}
            }
			demoGroupNotice.setMember(joinMsg.getMember());
			demoGroupNotice.setNickName(joinMsg.getNickName());
			demoGroupNotice.setConfirm(joinMsg.getConfirm());
			demoGroupNotice.setAdmin(joinMsg.getAdmin());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.REMOVE_MEMBER) {
			ECRemoveMemberMsg removeMsg = (ECRemoveMemberMsg) instanceMsg;
            if(TextUtils.isEmpty(removeMsg.getNickName())) {
                ECContacts c = ContactSqlManager.getContact(removeMsg.getMember());
                if(c != null) {
					removeMsg.setNickName(c.getNickname());
                } else {
					removeMsg.setNickName(removeMsg.getMember());
                }
            }
            if(TextUtils.isEmpty(instanceMsg.getGroupName())) {
                ECGroup ecGroup = GroupSqlManager.getECGroup(instanceMsg.getGroupId());
                if(ecGroup != null) {
                    instanceMsg.setGroupName(ecGroup.getName());
                } else {
                    instanceMsg.setGroupName(instanceMsg.getGroupId());
                }
            }
            if(CCPAppManager.getClientUser().getUserId().equals(removeMsg.getMember())) {
				needSync = true;
				
				if(removeMsg.isDiscuss()){
					
					verifyMsg = "您被讨论组管理员移除出讨论组";
				}else {
					
					verifyMsg = "您被群管理员移除出群组";
				}
                GroupSqlManager.updateJoinStatus(instanceMsg.getGroupId() , false);
                IMessageSqlManager.deleteAllBySession(instanceMsg.getGroupId());
                
                CCPAppManager.sendRemoveMemberBR();
                
                
                
            } else {
                if(!TextUtils.isEmpty(removeMsg.getNickName())) {
                	
                	if(removeMsg.isDiscuss()){
                		
                		verifyMsg = "[" + removeMsg.getNickName() + "]被讨论组管理员移除出讨论组";
                	}else {
                		
                		verifyMsg = "[" + removeMsg.getNickName() + "]被群管理员移除出群组";
                	}
                } else {
                	if(removeMsg.isDiscuss()){
                		
                		verifyMsg = "[" + removeMsg.getMember() + "]被讨论组管理员移除出讨论组";
                	}else {
                		
                		verifyMsg = "[" + removeMsg.getMember() + "]被群管理员移除出群组";
                	}
                }
            }
			demoGroupNotice.setMember(removeMsg.getMember());
			demoGroupNotice.setNickName(removeMsg.getNickName());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.DISMISS) {
        	
        	
        	ECDismissGroupMsg disMsg = (ECDismissGroupMsg) instanceMsg;
        	if(disMsg.isDiscuss()){
        		
        		verifyMsg = "讨论管理员解散了讨论组";
        	}else {
        		
        		verifyMsg = "群管理员解散了群组";
        	}
            GroupSqlManager.delGroup(instanceMsg.getGroupId());
			demoGroupNotice.setAdmin(disMsg.getAdmin());
			demoGroupNotice.setNickName(disMsg.getNickname());
			IMessageSqlManager.deleteAllBySession(instanceMsg.getGroupId());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.QUIT) {
			ECQuitGroupMsg quitMsg = (ECQuitGroupMsg) instanceMsg;
            if(TextUtils.isEmpty(quitMsg.getNickName())) {
				quitMsg.setNickName(quitMsg.getMember());
            }
            
            if(quitMsg.isDiscuss()){
            	
            	verifyMsg = "讨论组成员[" + quitMsg.getNickName() + "]退出了讨论组";
            }else {
            	
            	verifyMsg = "群成员[" + quitMsg.getNickName() + "]退出了群组";
            }
            if(CCPAppManager.getClientUser().getUserId().equals(quitMsg.getMember())) {
                IMessageSqlManager.deleteAllBySession(instanceMsg.getGroupId());
            }
			demoGroupNotice.setMember(quitMsg.getMember());
			needSync = CCPAppManager.getUserId().equals(quitMsg.getMember());
        } else if (type == ECGroupNoticeMessage.ECGroupMessageType.REPLY_INVITE) {
			ECReplyInviteGroupMsg rInviteMsg = (ECReplyInviteGroupMsg) instanceMsg;
			String admin=rInviteMsg.getAdmin();
			
			if(TextUtils.isEmpty(rInviteMsg.getNickName())) {
				rInviteMsg.setNickName(rInviteMsg.getMember());
			}
            if(rInviteMsg.getConfirm() == 2) {
            	if(rInviteMsg.isDiscuss()){
            		
            		verifyMsg = admin+"邀请 ["  + rInviteMsg.getNickName() + "]加入了讨论组";
            	}else {
            		
            		verifyMsg = "群管理员"+admin+"邀请 ["  + rInviteMsg.getNickName() + "]加入了群组";
            	}
            } else {
            	
            	if(rInviteMsg.isDiscuss()){
            		
            		verifyMsg = rInviteMsg.getNickName() + "拒绝加入讨论组";
            	}else {
            		
            		verifyMsg = rInviteMsg.getNickName() + "拒绝加入群组";
            	}
            }
			demoGroupNotice.setMember(rInviteMsg.getMember());
			demoGroupNotice.setNickName(rInviteMsg.getNickName());
			demoGroupNotice.setConfirm(rInviteMsg.getConfirm());
        }else if(type==ECGroupNoticeMessage.ECGroupMessageType.CHANGE_ADMIN){
        	ECChangeAdminMsg changeAdminMsg = (ECChangeAdminMsg) instanceMsg;
        	
        	verifyMsg = "[" + changeAdminMsg.getGroupName() + "] 管理员变更为"+changeAdminMsg.getNickName();
        }else if(type== ECGroupNoticeMessage.ECGroupMessageType.CHANGE_MEMBER_ROLE){
			ECChangeMemberRoleMsg changeMemberRoleMsg=(ECChangeMemberRoleMsg)instanceMsg;
			String role=changeMemberRoleMsg.getRoleMsg();
			demoGroupNotice.setDeclared(role);
			String level="";
			String leverlNum="";
			ECGroup group= GroupSqlManager.getECGroup(changeMemberRoleMsg.getGroupId());


			try {
				JSONObject object=new JSONObject(role);
				level=object.getString("role");
				leverlNum=level;
				level=level.equals("2")?"管理员":"成员";
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(group!=null){

				verifyMsg = "[" + group.getName() + "的"+changeMemberRoleMsg.getNickName()+"]"+"已变更成"+level;
			}else {

				verifyMsg = "["+changeMemberRoleMsg.getNickName()+"]"+"已变更成"+level;
			}

			if(group!=null){
				GroupMemberSqlManager.updateSelfRoleWithGroupId(changeMemberRoleMsg.getGroupId(), Integer.parseInt(leverlNum));
			}

		}
        else if (type == ECGroupNoticeMessage.ECGroupMessageType.MODIFY_GROUP) {
			// 群组资料变更通知
			ECModifyGroupMsg modifyGroupMsg = (ECModifyGroupMsg) instanceMsg;
			ECContacts contact = ContactSqlManager.getContact(modifyGroupMsg.getMember());
			if(contact == null) {
				if(modifyGroupMsg.isDiscuss()){
					verifyMsg = "[" + modifyGroupMsg.getNickName() + "] 修改了讨论组资料";
				}else {
					verifyMsg = "[" + modifyGroupMsg.getNickName() + "] 修改了群组资料";
				}
			} else {
				if(TextUtils.isEmpty(contact.getNickname())) {
					contact.setNickname(modifyGroupMsg.getMember());
				}
				if(modifyGroupMsg.isDiscuss()){
					
					verifyMsg = "[" + contact.getNickname() + "] 修改了讨论组资料";
				}else {
					
					verifyMsg = "[" + contact.getNickname() + "] 修改了群组资料";
				}
				
			}
			demoGroupNotice.setMember(modifyGroupMsg.getMember());
			demoGroupNotice.setDeclared(modifyGroupMsg.getModifyDoc());
		}
		if(needSync) GroupService.syncGroup(null);
		demoGroupNotice.setSender(instanceMsg.getSender());
        demoGroupNotice.setConfirm(state);
		demoGroupNotice.setDateCreate(instanceMsg.getDateCreated());
        demoGroupNotice.setContent(verifyMsg);
		demoGroupNotice.setGroupId(instanceMsg.getGroupId());
		demoGroupNotice.setGroupName(instanceMsg.getGroupName());
        try {
            GroupNoticeSqlManager.insertNoticeMsg(demoGroupNotice);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(instanceMsg != null) {
            getHelper().notify(demoGroupNotice);
            if(l != null) {
                l.onPushGroupNoticeMessage(demoGroupNotice);
            }
        }

    }
	
	/**
	 * 通知
	 * @param system
	 */
	private void notify(DemoGroupNotice system) {
		if(getHelper().mListener != null) {
			getHelper().mListener.onPushGroupNoticeMessage(system);
		}
	}
	
	/**
	 * 群组通知
	 */
	public interface OnPushGroupNoticeMessageListener {
		void onPushGroupNoticeMessage(DemoGroupNotice system);
	}
}
