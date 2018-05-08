package com.sht.smartlock.ui.activity.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.MainActivity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class GuideFragment03 extends Fragment implements View.OnClickListener{
    private View layoutView;
    private Button btn_duideMain;
    private ImageView ivGo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.activity_guide02, container, false);
        findviewbyid();
        return layoutView;
    }

    private void findviewbyid(){
        btn_duideMain = (Button)layoutView.findViewById(R.id.btn_duideMain);
        ivGo = (ImageView) layoutView.findViewById(R.id.ivGo);
        btn_duideMain.setOnClickListener(this);
        layoutView.setOnClickListener(this);
        ivGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_duideMain:
            case R.id.ivGo:
                startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
                break;
            default:
                startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
                break;
        }
    }
}
