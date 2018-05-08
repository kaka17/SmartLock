package com.sht.smartlock.ui.activity.booking;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.calendar.SimpleMonthAdapter;

import org.w3c.dom.Text;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectDestinationActivity extends BaseActivity {
    TextView btnEnsure;
    EditText etDestination;
    ListView lvDestHint;
    String mBeforeTest;
    List<String> mDestList;//存放目的地
    DestAdapter mDestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnEnsure = (TextView) findViewById(R.id.tvEnsure);
        etDestination = (EditText) findViewById(R.id.etDestination);
        lvDestHint = (ListView) findViewById(R.id.lvDestHint);
        mDestList = new ArrayList<>();
        mDestAdapter = new DestAdapter(mContext, mDestList);
        lvDestHint.setAdapter(mDestAdapter);
//        if (AppContext.getProperty(getString(R.string.booking_destination)) != null) {
//            etDestination.setText(AppContext.getProperty(getString(R.string.booking_destination);))
//        }
        lvDestHint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDestList != null) {
                    etDestination.setText(mDestList.get(position));
                }
            }
        });
        btnEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.setProperty(getString(R.string.booking_destination), etDestination.getText().toString());
                finish();
            }
        });
        //设置文字-列表联动
        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeforeTest = etDestination.getText().toString();
                if (etDestination.getText().toString().length() != 0) {
                    HttpClient.instance().GetHotelDestination(mBeforeTest, new DestCallBack());
                } else {
                    mDestAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public class DestAdapter extends BaseAdapter {
        List<String> mArrList;
        Context mContext;

        public DestAdapter(Context context, List<String> arrayList) {
            mContext = context;
            mArrList = arrayList;
        }

        public void addAll(List<String> data) {
            mArrList.addAll(data);
            notifyDataSetChanged();
        }

        public Context getContext() {
            return mContext;
        }

        public void clear() {
            mArrList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mArrList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_select_destination_item, null);
                convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.lvDestItem)));
            }
            ListCell lc = (ListCell) convertView.getTag();
            lc.getDestination().setText(String.valueOf(mArrList.get(position)));
            return convertView;
        }
    }

    public static class ListCell {
        public TextView getDestination() {
            return destination;
        }

        public void setDestination(TextView destination) {
            this.destination = destination;
        }

        private TextView destination;

        public ListCell(TextView textView) {
            destination = textView;
        }
    }

    public class DestCallBack extends HttpCallBack {

        @Override
        public void onSuccess(ResponseBean responseBean) {
            LogUtil.log("paramss  " + responseBean.toString());
            LogUtil.log("paramss " + mBeforeTest + " " + etDestination.getText().toString());
            try {
                if (mBeforeTest != "" && mBeforeTest.equals(etDestination.getText().toString())) {
                    List<String> tempList = responseBean.getListDataWithGson(String.class);
                    mDestAdapter.clear();
                    mDestAdapter.addAll(tempList);
                }
            } catch (Exception e) {
               // toastFail(R.string.no_result_left);
            }
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            LogUtil.log("paramss  hint fail" + error);
        }

        @Override
        public void onStart() {
            super.onStart();
            LogUtil.log("paramss i start" + Calendar.getInstance().getTimeInMillis());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_destination;
    }

    protected boolean hasToolBar() {
        return false;
    }

}
