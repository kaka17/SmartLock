package com.sht.smartlock.model;

import java.util.Map;

/**
 * Created by szfore_chenjl on 2015/3/31.
 */
public class SerializableMap extends BaseModel {
    private Map<String,Object> map;

    public SerializableMap(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
