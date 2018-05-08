package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.MyChatHistoryEntity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/29.
 */
public class MyChatHistory_Adapter extends RecyclerView.Adapter<MyChatHistory_Adapter.MyViewHolder>{

    private Context context;
    private List<MyChatHistoryEntity> list;
    private MyItemClickListener mItemClickListener;
    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public  MyChatHistory_Adapter( Context context,List<MyChatHistoryEntity> list){
        this.context=context;
        this.list=list;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.mychat_history_item,parent,false);
        return new MyViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_Chat_Name.setText(list.get(position).getName());
        holder.tv_Chat_message.setText(list.get(position).getContent());


    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private MyItemClickListener mItemClickListener;
        private ImageView iv_head;
        private TextView tv_Chat_Num;
        private TextView tv_Chat_Name;
        private TextView tv_Chat_id;
        private TextView tv_Time;
        private TextView tv_Chat_message;

        public MyViewHolder(View itemView,MyItemClickListener mItemClickListener) {
            super(itemView);
            this.mItemClickListener=mItemClickListener;
            this.iv_head= (ImageView) itemView.findViewById(R.id.iv_head);
            this.tv_Chat_Num= (TextView) itemView.findViewById(R.id.tv_Chat_Num);
            this.tv_Chat_Name= (TextView) itemView.findViewById(R.id.tv_Chat_Name);
            this.tv_Chat_id= (TextView) itemView.findViewById(R.id.tv_Chat_id);
            this.tv_Time= (TextView) itemView.findViewById(R.id.tv_Time);
            this.tv_Chat_message= (TextView) itemView.findViewById(R.id.tv_Chat_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener!=null){
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }
}
