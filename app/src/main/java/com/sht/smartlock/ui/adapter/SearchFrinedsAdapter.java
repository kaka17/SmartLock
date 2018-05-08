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
import com.sht.smartlock.model.SearchFrinedsInfo;
import com.sht.smartlock.ui.activity.myview.CircleImageView;
import com.sht.smartlock.ui.entity.Search_ChatEntity;
import com.sht.smartlock.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/2/25.
 */
public class SearchFrinedsAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<SearchFrinedsInfo> list;

    private ListItemClickHelp clickHelp;

    public SearchFrinedsAdapter(Context context, List<SearchFrinedsInfo> list,ListItemClickHelp clickHelp){
        this.context=context;
        this.list=list;
        this.clickHelp = clickHelp;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int i, View view,final ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.search_frids_item, null);
            viewHolder = new ViewHolder();
            viewHolder.search_frineds_head = (CircleImageView)view.findViewById(R.id.search_frineds_head);
            viewHolder.tvSearchFrinds = (TextView)view.findViewById(R.id.tvSearchFrinds);
            viewHolder.igButtonAddFrinds = (ImageView)view.findViewById(R.id.igButtonAddFrinds);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        LogUtil.log("aaaaa==" + list.get(i).getId_image());
        ImageLoader.getInstance().displayImage(list.get(i).getId_image(), viewHolder.search_frineds_head);
        viewHolder.tvSearchFrinds.setText(list.get(i).getName());

        final int position = i;
        final View v = view;
        final int btnbancle = viewHolder.igButtonAddFrinds.getId();

        viewHolder.igButtonAddFrinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHelp.onClick(v, viewGroup, position, btnbancle);
            }
        });

        return view;
    }

    class ViewHolder{
        private CircleImageView search_frineds_head;
        private TextView tvSearchFrinds;
        private ImageView igButtonAddFrinds;
    }

    public interface ListItemClickHelp {
        void onClick(View item, View widget, int position, int which);
    }

}
