package com.sht.smartlock.wxapi;








import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.RechargeSuccessActivity;
import com.sht.smartlock.ui.activity.Submit_OrdersActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.PayBillActivity;
import com.sht.smartlock.ui.activity.booking.PayResultActivity;
import com.sht.smartlock.ui.activity.mine.GoShoppingActivity;
import com.sht.smartlock.ui.activity.mine.OrderingActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler{

	private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
	private TextView tvPayResult;
	private TextView tvMyPayInfo;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

    	api = WXAPIFactory.createWXAPI(this, "wx58fb96f5bcbbb8d4");
        api.handleIntent(getIntent(), this);
		tvPayResult = (TextView) findViewById(R.id.tvPayResult);
		tvMyPayInfo = (TextView) findViewById(R.id.tvMyPayInfo);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.pay_result;
	}
	@Override
	protected boolean hasToolBar() {
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
//		AppContext.toast(resp.errCode+"");
//		AppContext.toast(AppContext.getProperty(Config.PAYDATApayAction)+"");
		if (resp.errCode==0){
			Intent i;
//			tvMyPayInfo.setText("支付成功");
			switch (AppContext.getProperty(Config.PAYDATApayAction)) {
				case Config.BOOKING://订房间支付
					i = new Intent(getApplicationContext(), PayResultActivity.class);
					i.putExtra(Config.KEY_BOOKING_BILLNUM, AppContext.getProperty(Config.PAYDATAstrBillnum));
					i.putExtra(Config.KEY_SHOW_ROOM_MODE,  AppContext.getProperty(Config.PAYDATAroom_mode));
					AppManager.getAppManager().finishActivity(PayBillActivity.class);
					startActivity(i);
					finish();
					break;
				case Config.ORDERING://购物，订餐支付
					if (AppContext.getProperty(Config.ISMORESERVICER)!=null&&AppContext.getProperty(Config.ISMORESERVICER).equals("false")){
						Intent intent = new Intent(getApplicationContext(), Submit_OrdersActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(Config.ORDER_TAB, AppContext.getProperty(Config.PAYDATAtab));
						bundle.putString(Config.ORDER_NUMVER, AppContext.getProperty(Config.PAYDATAstrBillnum));//订单号
						bundle.putString(Config.ROOM_ID, AppContext.getProperty(Config.PAYDATAroom_id));
						intent.putExtras(bundle);
						startActivity(intent);
					}else {
						//多服务商
						Intent intentList=new Intent();
						if (AppContext.getProperty(Config.PAYDATAtab).equals("1")){//购物
							intentList.setClass(getApplicationContext(), GoShoppingActivity.class);
						}else {
							intentList.setClass(getApplicationContext(), OrderingActivity.class);
						}
						startActivity(intentList);
					}
//					AppManager.getAppManager().finishOtherActivity(PayBillActivity.class);
					AppManager.getAppManager().finishActivity(PayBillActivity.class);
					finish();
					break;
				case Config.PAYDATARechargeActivity://充值
					i=new Intent(getApplicationContext(),RechargeSuccessActivity.class);
					startActivity(i);
					finish();
					break;
				default:
					AppContext.toast(AppContext.getProperty(Config.PAYDATApayAction)+"");
					break;
			}

		}else if (resp.errCode==-1){
//			tvMyPayInfo.setText("支付失败 原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。");
			Log.e("Pay", "可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。");
			AppContext.toast("支付失败");
			AppContext.setProperty(Config.PAYDATAstrBillnum, null);
			AppContext.setProperty(Config.PAYDATAroom_mode,null);
			AppContext.setProperty(Config.PAYDATAtab,null);
			AppContext.setProperty(Config.PAYDATAroom_id,null);
			AppContext.setProperty(Config.PAYDATApayAction,null);
			finish();
		}else if (resp.errCode==-2){
			AppContext.toast("支付失败");
			Log.e("Pay", "用户不支付了");
//			tvMyPayInfo.setText("未支付");
			AppContext.setProperty(Config.PAYDATAstrBillnum, null);
			AppContext.setProperty(Config.PAYDATAroom_mode,null);
			AppContext.setProperty(Config.PAYDATAtab,null);
			AppContext.setProperty(Config.PAYDATAroom_id,null);
			AppContext.setProperty(Config.PAYDATApayAction,null);
			finish();
		}else {
//			tvPayResult.setText("支付代码："+resp.errCode);
			finish();
		}
	}
}