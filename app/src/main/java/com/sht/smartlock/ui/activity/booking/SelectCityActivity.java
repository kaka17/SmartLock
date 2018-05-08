package com.sht.smartlock.ui.activity.booking;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.booking.City;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class SelectCityActivity extends BaseActivity implements View.OnClickListener {
    PullToRefreshGridView mCity;
    List<City> cityList;
    CityAdapter mCityAdapter;
    ImageView ivGoback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_select_city);
        ivGoback= (ImageView) findViewById(R.id.goBack);
        ivGoback.setOnClickListener(this);

        mCity = (PullToRefreshGridView) findViewById(R.id.gvCity);

        cityList = new ArrayList<>();
        mCityAdapter = new CityAdapter(SelectCityActivity.this, cityList);
        mCity.setAdapter(mCityAdapter);
        mCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TextView tv = (TextView) view.findViewById(R.id.gvCityItem);
                // toastSuccess(tv.getText() + "");
                AppContext.setProperty(getString(R.string.booking_cityid), cityList.get(position).getId());
                AppContext.setProperty(getString(R.string.booking_cityname), cityList.get(position).getCaption());
                finish();
            }
        });
        mCity.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                getHotelCity();
            }
        });
        ProgressDialog.show(mContext, getString(R.string.on_loading));
        getHotelCity();
    }

    private void getHotelCity() {
        HttpClient.instance().getHotelCity(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                mCity.onRefreshComplete();
                LogUtil.log("paramss re" + responseBean.toString());
                ProgressDialog.disMiss();
                mCityAdapter.clear();
                cityList = responseBean.getListDataWithGson(City.class);
                mCityAdapter.addAll(cityList);
                LogUtil.log("onsuccess");
//                if (mCityAdapter.getCount() == 0) {
//               //     LogUtil.log("count " + mCityAdapter.getCount());
//                    tvClickToRefresh.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onStart() {
                super.onStart();

                LogUtil.log("onstart");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                LogUtil.log("onfinish");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                LogUtil.log("paramss 2 " + error + "\n" + message);
                mCity.onRefreshComplete();
                ProgressDialog.disMiss();
                toastFail(R.string.failed_to_load_data);
                LogUtil.log("onfail");

//                if (mCityAdapter.getCount() == 0) {
//                    LogUtil.log("count " + mCityAdapter.getCount());
//                    tvClickToRefresh.setVisibility(View.VISIBLE);
//                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tvClickToRefresh:
//                tvClickToRefresh.setVisibility(View.GONE);
//                getHotelCity();
//                break;
            case R.id.goBack:
                finish();
                break;
        }
    }

    private class CityAdapter extends BaseAdapter {
        private Context mContext;

        List<City> mCity;

        public CityAdapter(Context context, List<City> city) {
            mContext = context;
            mCity = city;
        }

        public void addAll(List<City> data) {
            mCity.addAll(data);
            notifyDataSetChanged();
        }

        public void clear() {
            mCity.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCity.size();
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public Object getItem(int position) {
            return mCity.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_select_city_gridview_item, null);
                convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.gvCityItem)));
            }
            ListCell lc = (ListCell) convertView.getTag();

            lc.getCityname().setText(mCity.get(position).getCaption());
            return convertView;
        }
    }

    public static class ListCell {
        public TextView getCityname() {
            return cityname;
        }

        public void setCityname(TextView cityname) {
            this.cityname = cityname;
        }

        private TextView cityname;

        public ListCell(TextView textView) {
            cityname = textView;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_city;
    }

    protected boolean hasToolBar() {
        return false;
    }
}
