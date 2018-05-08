package com.sht.smartlock.api.base;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jodd.bean.BeanUtil;

public class ResponseBean extends JSONObject implements Serializable {
    private final static String ID = "id";
    private final static String ERROR = "error";
    private final static String CODE = "code";
    private final static String MESSAGE = "message";
    private final static String RESULT = "result";
    private final static String JSON_RPC = "jsonrpc";

    public ResponseBean() {
        super();

    }

    public ResponseBean(JSONObject copyFrom, String[] names)
            throws JSONException {
        super(copyFrom, names);

    }

    public ResponseBean(JSONTokener readFrom) throws JSONException {
        super(readFrom);

    }

    @SuppressWarnings("rawtypes")
    public ResponseBean(Map copyFrom) {
        super(copyFrom);

    }

    public ResponseBean(String json) throws JSONException {
        super(json);

    }

    /**
     * 判断是否返回成功
     *
     * @return
     */
    public boolean isSuccess() {

        return this.optString(ERROR, "") == "";
    }

    public boolean isFailure() {
        return this.optString(ERROR, "") != "";
    }

    /**
     * 获取id
     *
     * @return
     */
    public int getId() {
        try {
            return this.getInt(ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getJsonrpc() {
        try {
            return this.getString(JSON_RPC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取错误代码
     *
     * @return
     */
    public int getErrCode() {
        if (this.optString(ERROR, "") != "") {

            try {
                JSONObject json = new JSONObject(this.getString(ERROR));
                return json.getInt(CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    /**
     * 获取返回信息
     *
     * @return
     */
    public String getMsg() {
        if (this.optString(ERROR, "") != "") {
            try {
                JSONObject json = new JSONObject(this.getString(ERROR));
                return json.getString(MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取数据
     *
     * @param cls
     * @return
     */
    public <T> T getData(Class<T> cls) {
        T t = null;
        try {



            t = toBean(getJSONObject(RESULT), cls);//mResponse.getResult()
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }

    public <T> T getBaseData(Class<T> cls) {
        return new Gson().fromJson(this.toString(), cls);
    }
    public <T> T GetResultModel(Class<T> cls){
        try {
            JSONObject c=this.getJSONObject("result");
            return new Gson().fromJson(c.toString(),cls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
return null;
    }
    public <T> T getData(Class<T> cls, String key) {
        T t = null;
        try {
            t = toBean(getJSONObject(key), cls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }

    public String getData() {
        String data = "";
        try {
            data = getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取List
     *
     * @param cls
     * @return
     */
    @Deprecated
    public <T> List<T> getListData(Class<T> cls) {
        List<T> lists = new ArrayList<T>();
        try {
            JSONArray arr = getJSONArray(RESULT);
            final int length = arr.length();
            for (int i = 0; i < length; i++) {
                lists.add(toBean(arr.getJSONObject(i), cls));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public <T> List<T> getListDataWithGson(Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(getData()).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, cls));
        }
        return list;
    }

    //数据转换
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

    /**
     * 获取List
     *
     * @param cls
     * @return
     */
    public <T> List<T> getListData(Class<T> cls, String key) {
        List<T> lists = new ArrayList<T>();
        try {
            JSONArray arr = getJSONArray(key);
            final int length = arr.length();
            for (int i = 0; i < length; i++) {
                lists.add(toBean(arr.getJSONObject(i), cls));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
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
