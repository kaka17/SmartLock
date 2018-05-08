package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.TaskCompleteAdapter;
import com.sht.smartlock.ui.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class CompleteActivity extends BaseActivity {

    private ListView lvComPleteTask;
    private TaskCompleteAdapter adapter;
    private List<TaskEntity> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_complete);
        onBack();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_complete;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView(){
        lvComPleteTask = (ListView) findViewById(R.id.lvComPleteTask);
        adapter=new TaskCompleteAdapter(getApplicationContext(),list);
        lvComPleteTask.setAdapter(adapter);


    }



}
