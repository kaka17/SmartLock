package com.sht.smartlock.ui.activity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.ChatFriendsActivity;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.RechargeActivity;
import com.sht.smartlock.ui.activity.mine.AccountManagementActivity;
import com.sht.smartlock.ui.activity.mine.LoginActivity;
import com.sht.smartlock.ui.activity.mine.MyBalanceActivity;
import com.sht.smartlock.ui.activity.mine.MyBillActivity;
import com.sht.smartlock.ui.activity.mine.MyCollectionActivity;
import com.sht.smartlock.ui.activity.mine.MyCommentsActivity;
import com.sht.smartlock.ui.activity.mine.MyInformationActivity;
import com.sht.smartlock.ui.activity.mine.MyOrderActivity;
import com.sht.smartlock.ui.activity.mine.MySettingsActivity;
import com.sht.smartlock.ui.activity.mine.SearchFriendsActivity;
import com.sht.smartlock.ui.activity.myview.CircleImageView;
import com.sht.smartlock.ui.adapter.PersonSttingAdapter;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

public class MainTabMineFragment extends Fragment {
    private View newsLayout;
    private TextView tv_username;
    private CircleImageView image_head;
    private ListView list_person_stting;
    private String str_name[] = {"我的资料", "我的订单", "我的账单","我的收藏", "我的点评", "我的好友", "我的余额", "我的设置"};
	private ImageView ivIKnow;
	private RelativeLayout reGuiDe;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		newsLayout = inflater.inflate(R.layout.main_tab_mine, container, false);

		findviewbyid();
		setOnClickListener();

		return newsLayout;
	}

	private void findviewbyid(){
		image_head = (CircleImageView)newsLayout.findViewById(R.id.image_head);
		list_person_stting = (ListView)newsLayout.findViewById(R.id.list_person_stting);
		tv_username = (TextView)newsLayout.findViewById(R.id.tv_username);
		ivIKnow = (ImageView) newsLayout.findViewById(R.id.ivIKnow);
		reGuiDe = (RelativeLayout) newsLayout.findViewById(R.id.reGuiDe);
//		list_person_stting.setDividerPadding(0);
		list_person_stting.setDivider(null);
		if (AppContext.getShareUserSessinid()!=null){
			if (AppContext.getProperty(Config.ISFIRSTLOGINTOGUIDE)==null){
				AppContext.setProperty(Config.ISFIRSTLOGINTOGUIDE,"true");
				reGuiDe.setVisibility(View.VISIBLE);
			}else {
				reGuiDe.setVisibility(View.GONE);
			}
		}
		//获取本地存储的用户帐号
		PersonSttingAdapter adapter = new PersonSttingAdapter(getActivity().getApplicationContext(),str_name);
		list_person_stting.setAdapter(adapter);
	}

	private void setOnClickListener(){
		image_head.setOnClickListener(onClickListener);
		list_person_stting.setOnItemClickListener(itemClickListener);

		ivIKnow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				reGuiDe.setVisibility(View.GONE);
			}
		});
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()){
				case R.id.image_head://登录
					if((AppContext.getProperty(Config.SHARE_USER_ACCOUNT)==null)){
						startActivity(new Intent(getActivity().getApplication(), LoginByNameActivity.class));
					}else{
						startActivity(new Intent(getActivity(), AccountManagementActivity.class));
					}
					break;
			}
		}
	};


	private void get_user_head_image() {
		HttpClient.instance().get_user_head_image(new HttpCallBack() {
			@Override
			public void onSuccess(ResponseBean responseBean) {
				LogUtil.log(responseBean.toString());
				try {
					JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
					JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
					if (!jsonObject.getString("result").equals("")) {
//                        ImageLoader.getInstance().displayImage(jsonObject.getString("result"), igUserHead);
//                        BaseApplication.toast(jsonObject.getString("result"));
						//显示图片的配置.cacheInMemory(true)
//                        .cacheOnDisk(true)
						DisplayImageOptions options = new DisplayImageOptions.Builder()
//								.showImageOnLoading(R.drawable.default_image)
								.showImageOnFail(R.drawable.default_image)
								.cacheInMemory(false)
								.cacheOnDisk(false)
								.bitmapConfig(Bitmap.Config.RGB_565)
								.build();
//                        BaseApplication.toast(jsonObject.getString("result") + "；；；" + jsonObject.getString("result"));
						AppContext.setProperty(Config.MyImg,jsonObject.getString("result"));
						ImageLoader.getInstance().displayImage(jsonObject.getString("result"), image_head,options);
					} else {
//                        BaseApplication.toast(jsonObject.getString("result") + "图片地址");
					}
				} catch (Exception e) {
					LogUtil.log(e.toString());
				}
			}

			@Override
			public void onFailure(String error, String message) {
				super.onFailure(error, message);
				ProgressDialog.disMiss();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				ProgressDialog.disMiss();
			}
		});
	}

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0://我的资料
                    if (AppContext.getShareUserSessinid() != null) {
                        startActivity(new Intent(getActivity().getApplication(), MyInformationActivity.class));
                    } else {
                        BaseApplication.toast(R.string.pleaseLogin);
                        startActivity(new Intent(getActivity().getApplication(), LoginByNameActivity.class));
                    }
                    break;
                case 1://我的订单
                    startActivity(new Intent(getActivity().getApplication(), MyOrderActivity.class));
                    break;
				case 2:
					startActivity(new Intent(getActivity().getApplication(), MyBillActivity.class));
					break;
                case 3://我的收藏
                    if (AppContext.getShareUserSessinid() != null) {
                        startActivity(new Intent(getActivity().getApplication(), MyCollectionActivity.class));
                    } else {
                        BaseApplication.toast(R.string.pleaseLogin);
                        startActivity(new Intent(getActivity().getApplication(), LoginByNameActivity.class));
                    }
                    break;
                case 4://我的点评
                    if (AppContext.getShareUserSessinid() != null) {
                        startActivity(new Intent(getActivity().getApplication(), MyCommentsActivity.class));
                    } else {
                        BaseApplication.toast(R.string.pleaseLogin);
                        startActivity(new Intent(getActivity().getApplication(), LoginByNameActivity.class));
                    }
                    break;
                case 5:
                    if (AppContext.getShareUserSessinid() != null) {
                        if (DemoHXSDKHelper.getInstance().isLogined()) {
                            startActivity(new Intent(getActivity(), ChatFriendsActivity.class));
                        } else {
                            //环信账号未登陆
                            BaseApplication.toast("聊天账号为初始化");
                        }
                    } else {
                        BaseApplication.toast(R.string.pleaseLogin);
                        startActivity(new Intent(getActivity().getApplication(), LoginByNameActivity.class));
                    }

                    break;
                case 6://我的余额
                    if (AppContext.getShareUserSessinid() != null) {
                        startActivity(new Intent(getActivity().getApplication(), MyBalanceActivity.class));
//					startActivity(new Intent(getActivity().getApplication(), RechargeActivity.class));
					}else{
						BaseApplication.toast(R.string.pleaseLogin);
						startActivity(new Intent(getActivity().getApplication(),LoginByNameActivity.class));
					}

//					startActivity(new Intent(getActivity().getApplication(), BaiduMapActivity.class));
//					startActivity(new Intent(getActivity().getApplication(), RoutePlanActivity.class));
					break;
				case 7://我的设置
					startActivity(new Intent(getActivity().getApplication(), MySettingsActivity.class));
					break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
//        BaseApplication.toast("aa"+AppContext.getShareUserName());
		if(!(AppContext.getProperty(Config.SHARE_USER_ACCOUNT)==null)&&!(AppContext.getProperty(Config.SHARE_USER_ACCOUNT).equals(""))){
			tv_username.setText(AppContext.getProperty(Config.SHARE_USER_ACCOUNT));
//			BaseApplication.toast(AppContext.getShareUserName());
		}else {
			tv_username.setText("登录/注册");
		}
		if(AppContext.getShareUserSessinid()!=null){
			get_user_head_image();
		}else{
			image_head.setImageResource(R.drawable.default_image);
		}

	}

}
