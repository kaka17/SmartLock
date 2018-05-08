package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.R;
import com.sht.smartlock.model.CommentInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class CommentAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<CommentInfo> commentInfoList;

    public CommentAdapter(Context context,List<CommentInfo> commentInfoList){
        this.context = context;
        this.commentInfoList = commentInfoList;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return commentInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            view = layoutInflater.inflate(R.layout.my_comment_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvTimeDate = (TextView)view.findViewById(R.id.tvTimeDate);
//            viewHolder.tvEvaluation = (TextView)view.findViewById(R.id.tvEvaluation);
            viewHolder.tvData = (TextView)view.findViewById(R.id.tvData);
            viewHolder.image_Hotel_Comment = (ImageView)view.findViewById(R.id.image_Hotel_Comment);
            viewHolder.tv_Hotel_CommentName = (TextView)view.findViewById(R.id.tv_Hotel_CommentName);
            viewHolder.tv_Comment = (TextView)view.findViewById(R.id.tv_Comment);
            viewHolder.tv_HotelComment_Address = (TextView)view.findViewById(R.id.tv_HotelComment_Address);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.tvTimeDate.setText(commentInfoList.get(i).getCreate_time());
//        viewHolder.tvEvaluation.setText(commentInfoList.get(i).get);
        viewHolder.tvData.setText(commentInfoList.get(i).getContent());
//        viewHolder.tvTimeDate.setText();
        viewHolder.tv_Hotel_CommentName.setText(commentInfoList.get(i).getCaption());
        viewHolder.tv_Comment.setText(commentInfoList.get(i).getTotal_com()+"人评论");
        viewHolder.tv_HotelComment_Address.setText(commentInfoList.get(i).getAddress());
        ImageLoader.getInstance().displayImage(commentInfoList.get(i).getPicture(), viewHolder.image_Hotel_Comment);
        return view;
    }

    class ViewHolder{
        private TextView tvTimeDate;
        private TextView tvEvaluation;
        private TextView tvData;
        private ImageView image_Hotel_Comment;
        private TextView tv_Hotel_CommentName;
        private TextView tv_Comment;
        private TextView tv_HotelComment_Address;

        public ImageView getImage_Hotel_Comment() {
            return image_Hotel_Comment;
        }

        public void setImage_Hotel_Comment(ImageView image_Hotel_Comment) {
            this.image_Hotel_Comment = image_Hotel_Comment;
        }

        public TextView getTv_Comment() {
            return tv_Comment;
        }

        public void setTv_Comment(TextView tv_Comment) {
            this.tv_Comment = tv_Comment;
        }

        public TextView getTv_Hotel_CommentName() {
            return tv_Hotel_CommentName;
        }

        public void setTv_Hotel_CommentName(TextView tv_Hotel_CommentName) {
            this.tv_Hotel_CommentName = tv_Hotel_CommentName;
        }

        public TextView getTv_HotelComment_Address() {
            return tv_HotelComment_Address;
        }

        public void setTv_HotelComment_Address(TextView tv_HotelComment_Address) {
            this.tv_HotelComment_Address = tv_HotelComment_Address;
        }

        public TextView getTvData() {
            return tvData;
        }

        public void setTvData(TextView tvData) {
            this.tvData = tvData;
        }

        public TextView getTvEvaluation() {
            return tvEvaluation;
        }

        public void setTvEvaluation(TextView tvEvaluation) {
            this.tvEvaluation = tvEvaluation;
        }

        public TextView getTvTimeDate() {
            return tvTimeDate;
        }

        public void setTvTimeDate(TextView tvTimeDate) {
            this.tvTimeDate = tvTimeDate;
        }
    }

}
