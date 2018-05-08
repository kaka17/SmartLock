package com.sht.smartlock.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import jodd.bean.BeanUtil;

public class JPushBean extends JSONObject implements Serializable {
    private final static String TYPE = "type";

    public JPushBean() {
        super();
    }

    public JPushBean(JSONObject copyFrom, String[] names)
            throws JSONException {
        super(copyFrom, names);
    }

    public JPushBean(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    @SuppressWarnings("rawtypes")
    public JPushBean(Map copyFrom) {
        super(copyFrom);
    }

    public JPushBean(String json) throws JSONException {
        super(json);
    }

    public String getType() {
        String data = "";
        try {
            data = getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> T getData(Class<T> cls) {
        return toBean(this, cls);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T toBean(JSONObject jo, Class<T> cls) {
        Iterator<String> it = jo.keys();
        T obj = null;
        try {
            obj = cls.newInstance();
        } catch (Exception e) {
        }
        while (it.hasNext()) {
            String name = it.next();
            Object o = jo.opt(name);

            if (obj instanceof Map) {
                ((Map) obj).put(it.next(), o);
            } else {
                BeanUtil.setPropertyForcedSilent(obj, name, o);
            }
        }
        return obj;
    }

    public Object getValue(String key) {
        try {
            return get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
