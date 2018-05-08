package com.sht.smartlock.ui.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/11.
 */
public class TaskListEntity {
    private String content;
    private String cancel_time;
    private String response_time;
    private String room_service_id;
    private String attendant_name;
    private String state;
    private String create_time;
    private String finished_time;
    private String content_type;

    public void pareJson(JSONObject jsonObject) {
        try {
            this.content = jsonObject.getString("content");
            this.cancel_time = jsonObject.getString("cancel_time");
            this.response_time = jsonObject.getString("response_time");
            this.room_service_id = jsonObject.getString("room_service_id");
            this.attendant_name = jsonObject.getString("attendant_name");
            this.state = jsonObject.getString("state");
            this.create_time = jsonObject.getString("create_time");
            this.finished_time = jsonObject.getString("finished_time");
            this.content_type = jsonObject.getString("content_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCancel_time() {
        return cancel_time;
    }

    public void setCancel_time(String cancel_time) {
        this.cancel_time = cancel_time;
    }

    public String getResponse_time() {
        return response_time;
    }

    public void setResponse_time(String response_time) {
        this.response_time = response_time;
    }

    public String getRoom_service_id() {
        return room_service_id;
    }

    public void setRoom_service_id(String room_service_id) {
        this.room_service_id = room_service_id;
    }

    public String getAttendant_name() {
        return attendant_name;
    }

    public void setAttendant_name(String attendant_name) {
        this.attendant_name = attendant_name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFinished_time() {
        return finished_time;
    }

    public void setFinished_time(String finished_time) {
        this.finished_time = finished_time;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}
