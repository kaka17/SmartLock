package com.sht.smartlock.ui.entity;

import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.ListUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 */
public class TaskEntity implements Serializable {
    private String content;
//    private String cancel_time;
//    private String response_time;
    private String room_service_id;
    private String attendant_name;
    private String state;
    private String task_action;
//    private String finished_time;
    private String content_type;
    private List<ServicerActionEntity> ations=new ArrayList<>();

    public String getTask_action() {
        return task_action;
    }

    public void setTask_action(String task_action) {
        this.task_action = task_action;
    }

    public List<ServicerActionEntity> getAtions() {
        return ations;
    }

    public void setAtions(List<ServicerActionEntity> ations) {
        this.ations = ations;
    }

    public void pareJson(JSONObject jsonObject) {
        try {
            this.content = jsonObject.getString("content");
            this.room_service_id = jsonObject.getString("room_service_id");
            this.attendant_name = jsonObject.getString("attendant_name");
            this.state = jsonObject.getString("state");
            this.task_action = jsonObject.getString("task_action");
            this.content_type = jsonObject.getString("content_type");
            JSONArray jsonArray=new JSONArray(task_action);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                ServicerActionEntity  entity=new ServicerActionEntity();
                entity.setAction(object.getString("action"));
                entity.setAction_time(object.getString("action_time"));
                entity.setAttendant_name(object.optString("attendant_name"));
                entity.setComparableTime(DateUtil.getLongTime(object.getString("action_time")));
                ations.add(entity);
            }
            ListUitls.sortList(ations,"action_time","DESC");
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

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}
