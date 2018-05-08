package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.activity.myinterface.MyItemLongClickListener;
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/15.
 */
public class MyLockG_Adapter extends RecyclerView.Adapter<MyLockG_Adapter.ViewHolder>{


    private List<LockGroupsChatEntity> list;
    private Context context;

    private MyItemClickListener mItemClickListener;
//    private MyItemLongClickListener mItemLongClickListener;


    public MyLockG_Adapter(Context context, List<LockGroupsChatEntity> list){
        this.context=context;
        this.list=list;

    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View  view= LayoutInflater.from(context).inflate(R.layout.mylock_item,viewGroup,false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view,mItemClickListener);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        BaseApplication.toast(list.size()+"");
        viewHolder.tv_LockName.setText(list.get(i).getCaption());
        viewHolder.tv_Many_Person.setText(list.get(i).getOn_num());
        viewHolder.tv_Lock_Distance.setText(list.get(i).getAddress());
        String img=list.get(i).getPicture();
        if(img!=null)
            ImageLoader.getInstance().displayImage(img,viewHolder.iv_lock_logo);

        if (i==(list.size()-1)){
            viewHolder.view_enpty.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private MyItemClickListener mItemClickListener;
        private TextView tv_Many_Person;
        private TextView tv_LockName;
        private TextView tv_Lock_Distance;
        private View view_enpty;
        private ImageView iv_lock_logo;


        public ViewHolder(View itemView,MyItemClickListener mItemClickListener) {
            super(itemView);
            this.mItemClickListener=mItemClickListener;
            tv_Many_Person= (TextView) itemView.findViewById(R.id.tv_Many_Person);
            tv_LockName= (TextView) itemView.findViewById(R.id.tv_LockName);
            tv_Lock_Distance= (TextView) itemView.findViewById(R.id.tv_Lock_Distance);
            iv_lock_logo= (ImageView) itemView.findViewById(R.id.iv_lock_logo);
            view_enpty=itemView.findViewById(R.id.view_enpty);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getPosition());

            }
        }
    }
}
