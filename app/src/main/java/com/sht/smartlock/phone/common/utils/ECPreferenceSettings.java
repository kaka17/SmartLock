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
package com.sht.smartlock.phone.common.utils;

/**
 * @author Jorstin Chan@容联•云通讯
 * @date 2014-12-10
 * @version 4.0
 */
public enum ECPreferenceSettings {

    /**
     * Whether is the first use of the application
     *
     */
    SETTINGS_FIRST_USE("com.yuntongxun.ecdemo_first_use" , Boolean.TRUE),
    /**坚持云通讯登陆账号*/
    SETTINGS_YUNTONGXUN_ACCOUNT("com.yuntongxun.ecdemo_yun_account" , ""),
    /**检查是否需要自动登录*/
    SETTINGS_REGIST_AUTO("com.yuntongxun.ecdemo_account" , ""),
    /**是否使用回车键发送消息*/
    SETTINGS_ENABLE_ENTER_KEY("com.yuntongxun.ecdemo_sendmessage_by_enterkey" , Boolean.TRUE),
    /**聊天键盘的高度*/
    SETTINGS_KEYBORD_HEIGHT("com.yuntongxun.ecdemo_keybord_height" , 0),
    /**新消息声音*/
    SETTINGS_NEW_MSG_SOUND("com.yuntongxun.ecdemo_new_msg_sound" , true),
    /**新消息震动*/
    SETTINGS_NEW_MSG_SHAKE("com.yuntongxun.ecdemo_new_msg_shake" , true),
    SETTING_CHATTING_CONTACTID("com.yuntongxun.ecdemo_chatting_contactid" , ""),
    /**图片缓存路径*/
    SETTINGS_CROPIMAGE_OUTPUTPATH("com.yuntongxun.ecdemo_CropImage_OutputPath" , ""),





    
   SETTINGS_APPKEY("com.yuntongxun.ecdemo_appkey" , "20150314000000110000000000000010"),
   SETTINGS_TOKEN("com.yuntongxun.ecdemo_token" , "17E24E5AFDB6D0C1EF32F3533494502B"),

    SETTINGS_ABSOLUTELY_EXIT("com.yuntongxun.ecdemo_absolutely_exit" , Boolean.FALSE),
    SETTINGS_FULLY_EXIT("com.yuntongxun.ecdemo_fully_exit" , Boolean.FALSE),
    SETTINGS_PREVIEW_SELECTED("com.yuntongxun.ecdemo_preview_selected" , Boolean.FALSE),
    SETTINGS_OFFLINE_MESSAGE_VERSION("com.yuntongxun.ecdemo_offline_version" , 0),
    /**设置是否是匿名聊天*/
    SETTINGS_SHOW_CHATTING_NAME("com.yuntongxun.ecdemo_show_chat_name" , false),
    
    SETTINGS_CUSTOM_APPKEY("com.yuntongxun.ecdemo_custom_appkey" , ""),
    SETTINGS_CUSTOM_TOKEN("com.yuntongxun.ecdemo_custom_token" , ""),
    SETTINGS_SERVER_CUSTOM("com.yuntongxun.ecdemo_setserver" , false),
    SETTINGS_NOTICE_CUSTOM("com.yuntongxun.ecdemo_notice" , Boolean.FALSE), ;


    private final String mId;
    private final Object mDefaultValue;

    /**
     * Constructor of <code>CCPPreferenceSettings</code>.
     * @param id
     *            The unique identifier of the setting
     * @param defaultValue
     *            The default value of the setting
     */
    private ECPreferenceSettings(String id, Object defaultValue) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    /**
     * Method that returns the unique identifier of the setting.
     * @return the mId
     */
    public String getId() {
        return this.mId;
    }

    /**
     * Method that returns the default value of the setting.
     *
     * @return Object The default value of the setting
     */
    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    /**
     * Method that returns an instance of {@link com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings} from
     * its. unique identifier
     *
     * @param id
     *            The unique identifier
     * @return CCPPreferenceSettings The navigation sort mode
     */
    public static ECPreferenceSettings fromId(String id) {
        ECPreferenceSettings[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mId == id) {
                return values[i];
            }
        }
        return null;
    }
}
