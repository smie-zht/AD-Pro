package com.app.finalpro.douban.douban;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.finalpro.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by user on 2017/12/25.
 */
public class DbListAdpt extends RecyclerView.Adapter<DbListAdpt.MyViewHolder> {
    private ArrayList<Douban.Book> mData;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener=null;

    public DbListAdpt(Context context, ArrayList<Douban.Book> datas){
        this.mContext=context;
        this.mData=datas;
        this.inflater=LayoutInflater.from(mContext);
    }
    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount(){
        return mData.size();
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        holder.dbltitle.setText(mData.get(position).getTitle());
        holder.dblauthor.setText(mData.get(position).getAllAuthor());
        holder.dblpublisher.setText(mData.get(position).getPublisher());
        Glide.with(mContext).load(mData.get(position).getImageURL()).into(holder.dblimg);
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }
    public void addData(Douban.Book doubanbk) {
        Douban.Book tmp = new Douban.Book();

        mData.add(tmp);
        //notifyDataSetChanged();
    }
    public void change() {
        notifyDataSetChanged();
    }
    public void deleteData(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }
    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(mContext).inflate(R.layout.douban_list,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dblauthor;
        TextView dblpublisher;
        TextView dbltitle;
       ImageView dblimg;
        public MyViewHolder(View view)
        {
            super(view);
            dbltitle = (TextView) view.findViewById(R.id.doubanListTitle);
            dblauthor = (TextView) view.findViewById(R.id.doubanListAuthor);
            dblpublisher = (TextView) view.findViewById(R.id.doubanListPublisher);
            dblimg = (ImageView) view.findViewById(R.id.doubanListImg);

        }
    }
}