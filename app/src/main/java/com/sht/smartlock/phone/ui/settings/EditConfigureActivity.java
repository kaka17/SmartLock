package com.sht.smartlock.phone.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;


import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.ECPreferenceSettings;
import com.sht.smartlock.phone.common.utils.ECPreferences;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.sht.smartlock.phone.ui.chatting.base.EmojiconEditText;
import com.sht.smartlock.phone.ui.group.CreateGroupActivity;

import java.io.InvalidClassException;

public class EditConfigureActivity extends ECSuperActivity implements View.OnClickListener{

    public static final String EXTRA_EDIT_TITLE = "edit_title";
    public static final String EXTRA_EDIT_HINT = "edit_hint";
    private int mSettingType;
    private EmojiconEditText mEdittext;
    private ECPreferenceSettings mSettings;
    
    public static boolean isTop=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTop=true;
        mSettingType = getIntent().getIntExtra("setting_type" , -1);
        String title = "";
        if(mSettingType == SettingsActivity.CONFIG_TYPE_APPKEY) {
            title = getString(R.string.edit_appkey);
            mSettings = ECPreferenceSettings.SETTINGS_APPKEY;
        } else if (mSettingType == SettingsActivity.CONFIG_TYPE_TOKEN) {
            title = getString(R.string.edit_token);
            mSettings = ECPreferenceSettings.SETTINGS_TOKEN;
        } else if (mSettingType == SettingsActivity.CONFIG_TYPE_SERVERIP) {
            title = getString(R.string.edit_serverip);
            // mSettings = ECPreferenceSettings.SETTINGS_SERVERIP;
        } else {
            mSettingType = -1;
            title = getIntent().getStringExtra("edit_title");
        }

        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.dialog_ok_button),
                title, null, this);
        initView();
    }

    private void initView() {
        mEdittext = (EmojiconEditText) findViewById(R.id.content);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = filter;
        mEdittext.setFilters(inputFilters);
        if(mSettingType != -1) {
            String config = getConfig(mSettings);
            mEdittext.setText(config);
            mEdittext.setSelection(mEdittext.getText().length());
            return ;
        }
        String defaultData = getIntent().getStringExtra("edit_default_data");
        if(!TextUtils.isEmpty(defaultData)) {
            mEdittext.setText(defaultData);
            mEdittext.setSelection(mEdittext.getText().length());
        } else if (getIntent().hasExtra(EXTRA_EDIT_HINT)) {
            mEdittext.setHint(getIntent().getStringExtra(EXTRA_EDIT_HINT));
        }
    }

    private String getConfig(ECPreferenceSettings settings) {
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        String value = sharedPreferences.getString(settings.getId() , (String)settings.getDefaultValue());
        return value;
    }

    private void saveSettings(ECPreferenceSettings settings) {
        String settingsValue = mEdittext.getText().toString().trim();
        try {
            ECPreferences.savePreference(settings ,settingsValue,true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_configure;
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	isTop=false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.text_right:
                hideSoftKeyboard();
                if(mSettingType == -1) {
                    Intent intent = new Intent();
                    intent.putExtra("result_data" , mEdittext.getText().toString().toString());
                    setResult(RESULT_OK ,intent);
                }  else {
                    saveSettings(mSettings);
                    setResult(RESULT_OK);
                }
                finish();
                break;
            default:
                break;
        }
    }

    final InputFilter filter = new InputFilter () {

        private int limit = 128;
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            LogUtil.i(LogUtil.getLogUtilsTag(CreateGroupActivity.class), source
                    + " start:" + start + " end:" + end + " " + dest
                    + " dstart:" + dstart + " dend:" + dend);
            float count = calculateCounts(dest);
            int overplus = limit - Math.round(count) - (dend - dstart);
            if(overplus <= 0) {
                if ((Float.compare(count, (float) (limit - 0.5D)) == 0)
                        && (source.length() > 0)
                        && (!(DemoUtils.characterChinese(source.charAt(0))))) {
                    return source.subSequence(0, 1);
                }
                ToastUtil.showMessage("超过最大限制");
                return "";
            }

            if( overplus >= (end - start)) {
                return null;
            }
            int tepmCont = overplus + start;
            if((Character.isHighSurrogate(source.charAt(tepmCont - 1))) && (--tepmCont == start)) {
                return "";
            }
            return source.subSequence(start, tepmCont);
        }

    };
    /**
     *
     * @param text
     * @return
     */
    public static float calculateCounts(CharSequence text) {

        float lengh = 0.0F;
        for(int i = 0; i < text.length() ; i++) {
            if(!DemoUtils.characterChinese(text.charAt(i))) {
                lengh += 1.0F;
            } else {
                lengh += 0.5F;
            }
        }

        return lengh;
    }

}
