package com.sht.smartlock.phone.ui.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.CommomUtil;
import com.sht.smartlock.phone.common.utils.ECPreferenceSettings;
import com.sht.smartlock.phone.common.utils.ECPreferences;
import com.sht.smartlock.phone.common.utils.ToastUtil;
import com.sht.smartlock.phone.ui.ECSuperActivity;
import com.yuntongxun.ecsdk.ECDevice;

import java.io.InvalidClassException;

public class ECSetUpServerActivity extends ECSuperActivity implements
		OnClickListener {

	private EditText etConnect;
	private EditText etConnectPort;
	private EditText etLVS;
	private EditText etLVSPort;
	private EditText etFile;
	private EditText etFilePort;
	private EditText etAppid;
	private EditText etAppToken;

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.setup_server_layout;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
				R.drawable.btn_style_green, null, getString(R.string.save),
				getString(R.string.setup_server), null, this);

		initViews();
	}

	private void initViews() {

		etConnect = (EditText) findViewById(R.id.setup_connect);
		etConnectPort = (EditText) findViewById(R.id.setup_connect_port);
		etLVS = (EditText) findViewById(R.id.setup_lvs);
		etLVSPort = (EditText) findViewById(R.id.setup_lvs_port);
		etFile = (EditText) findViewById(R.id.setup_fileserver);
		etFilePort = (EditText) findViewById(R.id.setup_fileserver_port);
		etAppid = (EditText) findViewById(R.id.setup_appid);
		etAppToken = (EditText) findViewById(R.id.setup_apptoken);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			hideSoftKeyboard();
			finish();
			break;
		case R.id.text_right:

			String connect = etConnect.getText().toString().trim();
			String connectPort = etConnectPort.getText().toString().trim();
			String lvs = etLVS.getText().toString().trim();
			String lvsport = etLVSPort.getText().toString().trim();
			String file = etFile.getText().toString().trim();
			String fileport = etFilePort.getText().toString().trim();
			String appkey = etAppid.getText().toString().trim();
			String apptoken = etAppToken.getText().toString().trim();

			if (TextUtils.isEmpty(connect) || TextUtils.isEmpty(connectPort)
					|| TextUtils.isEmpty(lvs) || TextUtils.isEmpty(lvsport)
					|| TextUtils.isEmpty(appkey) || TextUtils.isEmpty(apptoken)
					|| TextUtils.isEmpty(file) || TextUtils.isEmpty(fileport)){
				
				ToastUtil.showMessage("请输入完整信息!");
				return;
			}

				try {
					ECPreferences.savePreference(
							ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY,
							appkey, true);

					ECPreferences.savePreference(
							ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN,
							apptoken, true);
					ECPreferences.savePreference(
							ECPreferenceSettings.SETTINGS_SERVER_CUSTOM,
							Boolean.TRUE, true);

				} catch (InvalidClassException e) {
					e.printStackTrace();
				}

			ECDevice.initServer(this, CommomUtil.setUpXml(connect, connectPort,
					lvs, lvsport, file, fileport));
			ToastUtil.showMessage("保存成功");
			finish();

			break;
		default:
			break;
		}
	}

}
