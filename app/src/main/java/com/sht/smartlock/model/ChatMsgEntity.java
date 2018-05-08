package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/13.
 */
public class ChatMsgEntity {
    private static final String TAG = ChatMsgEntity.class.getSimpleName();

    //接口数据
    private String ID;
    private String content;
    private String create_time;

    private String content_type;
    private boolean isComMeg = false;
    private String voicTime;
    private String user_id;
    private String attendant_id;

    public String getVoicTime() {
        return voicTime;
    }

    public void setVoicTime(String voicTime) {
        this.voicTime = voicTime;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }



    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
        isComMeg = isComMsg;
    }

    public ChatMsgEntity() {
    }

    public static String getTAG() {
        return TAG;
    }

    public boolean isComMeg() {
        return isComMeg;
    }

    public void setIsComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAttendant_id() {
        return attendant_id;
    }

    public void setAttendant_id(String attendant_id) {
        this.attendant_id = attendant_id;
    }

    @Override
    public String toString() {
        return "ChatMsgEntity{" +
                "ID='" + ID + '\'' +
                ", content='" + content + '\'' +
                ", create_time='" + create_time + '\'' +
                ", content_type='" + content_type + '\'' +
                ", isComMeg=" + isComMeg +
                ", voicTime='" + voicTime + '\'' +
                ", user_id='" + user_id + '\'' +
                ", attendant_id='" + attendant_id + '\'' +
                '}';
    }
}
