package com.sht.smartlock.util;

import android.content.Context;
import android.os.Handler;


import com.sht.smartlock.AppContext;

import java.util.HashSet;
import java.util.Set;



/**
 * Created by chenjl on 2015/5/9.
 */
public class JPushUtil {
    private Context mContext;
    private static JPushUtil instance;

    public JPushUtil(Context context) {
        this.mContext = context;
    }

    public static class Jpush{
        private String alias;
        private String tag;
        private Set<String> tags = new HashSet<>();

        public Jpush(String alias, Set<String> tags) {
            this.alias = alias;
            this.tags = tags;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
            tags.add(tag);
        }

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }
    }

    public static JPushUtil instance(Context context) {
        if (instance == null) {
            synchronized (JPushUtil.class) {
                instance = new JPushUtil(context);
            }
        }
        return instance;
    }

    public void set(String alias, String tag){
        Set<String> tags = new HashSet<>();
        tags.add(tag);
        Jpush jpush = new Jpush(alias, (tag == null || tag.equals("")) ? null : tags);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIASANDTAG, jpush));
    }

    public void clean(){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIASANDTAG, new Jpush("", null)));
    }

    public void setAliasAndTag(JPushUtil.Jpush jpush){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIASANDTAG, jpush));
    }

    public void setAlias(String alias){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    public void setTag(String tag){
        Set<String> tags = new HashSet<>();
        tags.add(tag);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAG, tags));
    }

//    private final TagAliasCallback mCallback = new TagAliasCallback() {
//        @Override
//        public void gotResult(int code, String alias, Set<String> tags) {
//            System.out.println("code:" + code + ", alias:" + alias);
//            switch (code) {
//                case 0:
//                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
//                    AppContext.instance().setAliasd();
//                    AppContext.instance().setTagd();
//                    break;
//                case 6002:
//                    // 延迟 60 秒来调用 Handler 设置别名
//                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
//                    if(tags != null && !tags.isEmpty()){
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAG, tags));
//                    }
//                    break;
//            }
//        }
//    };

    private static final int MSG_SET_ALIASANDTAG = 1000;
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAG = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case MSG_SET_ALIASANDTAG:
//                    Jpush jpush = (Jpush) msg.obj;
//                    JPushInterface.setAliasAndTags(mContext, jpush.getAlias(), jpush.getTags());
//                    break;
//                case MSG_SET_ALIAS:
//                    //if(AppContext.instance().isAliasd()) {
//                    JPushInterface.setAlias(mContext, (String) msg.obj, mCallback);
//                    //}
//                    break;
//                case MSG_SET_TAG:
//                    //if(AppContext.instance().isTagd()) {
//                    JPushInterface.setTags(mContext, (Set<String>) msg.obj, mCallback);
//                    //}
//                    break;
            }
        }
    };
}
