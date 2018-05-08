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
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;
import com.sht.smartlock.ui.entity.Search_ChatEntity;

import java.util.List;

/**
 * Created by Administrator on 2015/10/7.
 */
public class Search_Chat_Adapter extends RecyclerView.Adapter<Search_Chat_Adapter.SearchViewHolder>{

    private List<Search_ChatEntity> list;
    private Context context;

    private MyItemClickListener mItemClickListener;

    public Search_Chat_Adapter(Context context, List<Search_ChatEntity> list){
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
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(context).inflate(R.layout.mylock_item,parent,false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new SearchViewHolder(view,mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.tv_LockName.setText(list.get(position).getCaption());
        holder.tv_Many_Person.setText(list.get(position).getOn_num());
        holder.tv_Lock_Distance.setText(list.get(position).getAddress());
        if (!list.get(position).getPicture().equals("null")){
            ImageLoader.getInstance().displayImage(list.get(position).getPicture(),holder.iv_lock_logo);
        }

    }

    class  SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private MyItemClickListener mItemClickListener;
        private TextView tv_Many_Person;
        private TextView tv_LockName;
        private TextView tv_Lock_Distance;
        private ImageView iv_lock_logo;
        public SearchViewHolder(View itemView,MyItemClickListener mItemClickListener) {
            super(itemView);
            this.mItemClickListener=mItemClickListener;
            tv_Many_Person= (TextView) itemView.findViewById(R.id.tv_Many_Person);
            tv_LockName= (TextView) itemView.findViewById(R.id.tv_LockName);
            tv_Lock_Distance= (TextView) itemView.findViewById(R.id.tv_Lock_Distance);
            iv_lock_logo= (ImageView) itemView.findViewById(R.id.iv_lock_logo);
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
