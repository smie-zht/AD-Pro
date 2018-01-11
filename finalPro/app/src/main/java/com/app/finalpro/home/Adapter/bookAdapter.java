package com.app.finalpro.home.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.finalpro.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

/**
 * Created by ZXG on 2017/12/30.
 */

public class bookAdapter extends RecyclerView.Adapter<bookAdapter.SimpleCardViewHolder> {
    private Context mContext;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private List<Map<String,Object>> mdata;
    private OnItemClickListener listener = null;
    public bookAdapter(Context c,int layout,List<Map<String,Object>> data){
        mContext = c;
        mLayoutId = layout;
        mInflater = LayoutInflater.from(c);
        mdata = data;
    }
    public interface OnItemClickListener
    {   void onClick(int position);
        boolean onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener=onItemClickListener;
    }

    //从ViewGroup中以mlayoutId布局样式膨胀出一个View，交给ViewHolder处理
    @Override                                         //parent
    public SimpleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutId,parent,false);
        SimpleCardViewHolder holder = new SimpleCardViewHolder(itemView);

        return holder;
    }

    //实现holder中视图和数据的联姻（数据加载到视图控件）,增加监听器
    @Override
    public void onBindViewHolder(final SimpleCardViewHolder holder, int position) {
        Map<String,Object> object = mdata.get(position);
        ImageView ItemPic = holder.bookPic;
        Glide.with(mContext).load(object.get("bookPic")).into(ItemPic);
        TextView ItemTitle = holder.bookName;
        ItemTitle.setText(object.get("bookName").toString());
        TextView ItemInfo = holder.bookInfo;
        ItemInfo.setText(object.get("bookInfo").toString());

        //针对瀑布流布局重载导致的高度变化。
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height= 515 ;
        holder.itemView.setLayoutParams(params);

        if(listener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public String getData(int pos, String tag){
        return mdata.get(pos).get(tag).toString();
    }
    public void removeData(int pos){    mdata.remove(pos); notifyDataSetChanged(); }
    public void clearData(){    mdata.removeAll(mdata); notifyDataSetChanged(); }

    public class SimpleCardViewHolder extends RecyclerView.ViewHolder {
        ImageView bookPic;
        TextView bookName;
        TextView bookInfo;

        public SimpleCardViewHolder(View itemView) {
            super(itemView);
            bookPic = (ImageView)itemView.findViewById(R.id.news_photo);
            bookName = (TextView)itemView.findViewById(R.id.news_title);
            bookInfo = (TextView) itemView.findViewById(R.id.news_writer);
        }
    }
}
