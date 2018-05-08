package com.sht.smartlock.ui.activity.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.MainActivity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class GuideFragment02 extends Fragment{
    private View layoutView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.activity_guide01, container, false);
        layoutView.findViewById(R.id.ivGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
            }
        });
        return layoutView;
    }
}
