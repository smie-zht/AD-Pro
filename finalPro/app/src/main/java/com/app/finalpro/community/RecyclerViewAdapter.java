package com.app.finalpro.community;

/**
 * Created by qqq on 2017/12/31.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.finalpro.R;
import com.bumptech.glide.Glide;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private OnRcyclerClickListener listener;
    private List<News> newses;       //数据源
    private Context context;
    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的
    private View mHeaderView;
    private View mFooterView;
    private final int DONE=1;//已经点赞
    private final int NO=0;//还没有点赞
    private final int NO1=-1;//初始状态。
    private final int MAX_LINE_COUNT = 3;
    private final int STATE_UNKNOW = -1;
    private final int STATE_NOT_OVERFLOW = 1;//文本行数不能超过限定行数
    private final int STATE_COLLAPSED = 2;//文本行数超过限定行数，进行折叠
    private final int STATE_EXPANDED = 3;//文本超过限定行数，被点击全文展开
    private SparseArray<Integer> mTextStateList; //键值对代表此时的文本的数量和相应的文本的位置！
    private SparseArray<Integer> good;//是否点赞。
    public static enum ITEM_TYPE {
        ITEM_TYPE_Theme,
        ITEM_TYPE_Video
    }   //判断类型
    private TextView themeTitle;//t头部。
    private ImageView theme;//设置头像！
    public interface OnRcyclerClickListener{
        void onClicked(int itemPosition, View view);
        void onLongClicked(int itemPosition, View view);
    }

    public RecyclerViewAdapter(List<News> newses, Context context) {
        this.newses = newses;
        this.context=context;
        mTextStateList = new SparseArray<>();
        good = new SparseArray<>();
    }
    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView usericon;
        ImageView photo;
        TextView username;
        TextView num;     //数量
        ImageView zan;    //赞
        TextView expandOrCollapse;
        TextView content;//显示的具体的内容。
        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.card_view);
            photo=(ImageView) itemView.findViewById(R.id.fabu);//发布的图片
            usericon= (ImageView) itemView.findViewById(R.id.user_icon);
            username= (TextView) itemView.findViewById(R.id.user_name);
            num=(TextView)itemView.findViewById(R.id.num);  //数字
            zan=(ImageView)itemView.findViewById(R.id.zan); //点赞的图片。
            content = (TextView) itemView.findViewById(R.id.tv_content);//得到的为此时的正常的输入的内容。
            expandOrCollapse = (TextView) itemView.findViewById(R.id.tv_expand_or_collapse);//点击全文和隐藏的的按钮。
        }
    }//主要内容的
    public class ThemeVideoHolder extends RecyclerView.ViewHolder{
        public ThemeVideoHolder(View itemView) {
            super(itemView);
            theme = (ImageView) itemView.findViewById(R.id.hometab1_theme_title);
        }
    }   //文字的。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType== ITEM_TYPE.ITEM_TYPE_Theme.ordinal()){
            View view = LayoutInflater.from(context).inflate(R.layout.videothemelist,viewGroup,false);
            return new ThemeVideoHolder(view);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_Video.ordinal()){
            View v;
            v= LayoutInflater.from(context).inflate(R.layout.news_item1,viewGroup,false);
            NewsViewHolder nvh=new NewsViewHolder(v);   ///修改此时的布局。
            return nvh;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ThemeVideoHolder){
            theme.setImageResource(R.drawable.background);
            theme.setScaleType(ImageView.ScaleType.FIT_XY);
//            theme.setMaxWidth("");
        }
        else if(holder instanceof NewsViewHolder){
            Glide.with(context).load(newses.get(i).getIconURL()).into(((NewsViewHolder) holder).usericon);
            if(!newses.get(i).getPicURL().trim().equals("")){
                ((NewsViewHolder) holder).photo.setVisibility(View.VISIBLE);
                Glide.with(context).load(newses.get(i).getPicURL()).into(((NewsViewHolder) holder).photo);
                Log.v("year","day");
            }
            else{
                ((NewsViewHolder) holder).photo.setVisibility(View.GONE);
                Log.v("认定图片：","失败");
            }
            ((NewsViewHolder)holder).username.setText(newses.get(i).getUname());
            int state=mTextStateList.get(i,STATE_UNKNOW);
            if(state==STATE_UNKNOW){   //初始状态：隐藏超过三行的内容，设置展开全文按钮
                ((NewsViewHolder)holder).content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
//                    这个回掉会调用多次，获取玩行数后记得注销监听
                        ((NewsViewHolder)holder).content.getViewTreeObserver().removeOnPreDrawListener(this);
//                    holder.content.getViewTreeObserver().addOnPreDrawListener(null);
//                    如果内容显示的行数大于限定显示行数
                        if (((NewsViewHolder)holder).content.getLineCount()>MAX_LINE_COUNT) {
                            ((NewsViewHolder)holder).content.setMaxLines(MAX_LINE_COUNT);//设置最大显示行数
                            ((NewsViewHolder)holder).expandOrCollapse.setVisibility(View.VISIBLE);//让其显示全文的文本框状态为显示

                       //     ((NewsViewHolder)holder).photo.setVisibility(View.GONE);
                            ((NewsViewHolder)holder).expandOrCollapse.setText("全文");//设置其文字为全文
                            mTextStateList.put(i, STATE_COLLAPSED);
                        }
                        else{
//                            ((NewsViewHolder)holder).photo.setVisibility(View.GONE);
                            ((NewsViewHolder)holder).expandOrCollapse.setVisibility(View.GONE);//显示全文隐藏
                            mTextStateList.put(i,STATE_NOT_OVERFLOW);//让其不能超过限定的行数
                        }
                        return true;
                    }
                });
                ((NewsViewHolder)holder).content.setMaxLines(Integer.MAX_VALUE);//设置文本的最大行数，为整数的最大数值
                ((NewsViewHolder)holder).content.setText(newses.get(i).getContent());//用Util中的getContent方法获取内容
            }
            else{
//            如果之前已经初始化过了，则使用保存的状态，无需在获取一次
                switch (state){
                    case STATE_NOT_OVERFLOW:
                        ((NewsViewHolder)holder).expandOrCollapse.setVisibility(View.GONE);
                        break;
                    case STATE_COLLAPSED:
                        ((NewsViewHolder)holder).content.setMaxLines(MAX_LINE_COUNT);
                        ((NewsViewHolder)holder).expandOrCollapse.setVisibility(View.VISIBLE);
                        ((NewsViewHolder)holder).expandOrCollapse.setText("全文");
                        break;
                    case STATE_EXPANDED:
                        ((NewsViewHolder)holder).content.setMaxLines(Integer.MAX_VALUE);
                        ((NewsViewHolder)holder).expandOrCollapse.setVisibility(View.VISIBLE);
                        ((NewsViewHolder)holder).expandOrCollapse.setText("收起");
                        break;
                }
                ((NewsViewHolder)holder).content.setText(newses.get(i).getContent());
            }
            ((NewsViewHolder)holder).expandOrCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state=mTextStateList.get(i,STATE_UNKNOW);
                    if (state==STATE_COLLAPSED){
                        ((NewsViewHolder)holder).content.setMaxLines(Integer.MAX_VALUE);
                        ((NewsViewHolder)holder).expandOrCollapse.setText("收起");
                        mTextStateList.put(i,STATE_EXPANDED);
                    }
                    else if (state==STATE_EXPANDED){
                        ((NewsViewHolder)holder).content.setMaxLines(MAX_LINE_COUNT);
                        ((NewsViewHolder)holder).expandOrCollapse.setText("全文");
                        mTextStateList.put(i,STATE_COLLAPSED);
                    }
                }
            });
            int state1=good.get(i,NO1);//先得到没有点赞的状态。
            if(state1==NO1){
                ((NewsViewHolder)holder).num.setText(newses.get(i).getLike()); //数字。
            }
            else{
                switch (state1){
                    case NO:
                        String str1=newses.get(i).getLike();
                        Log.v("AAAAAA","dasdasdasdasdasdasadasdasdsadasd");
                        int year;
                        year = Integer.parseInt(str1);
                        year++;
                        String str2= String.valueOf(year);
                        ((NewsViewHolder)holder).num.setText(str2); //数字。
                        notifyItemChanged(i);
                        break;
                    case DONE:
//                        String str3=newses.get(i).getNum();
//                        Log.v("CCCC","dasdasdasdasdasdasadasdasdsadasd");
//                        int year1;
//                        year1 = Integer.getInteger(str3);
//                        year1--;
//                        String str4=String.valueOf(year1);
//                        ((NewsViewHolder)holder).num.setText(str4); //数字。
//                        notifyItemChanged(i);
                        break;
                }
            }
            ((NewsViewHolder)holder).zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state1=good.get(i,DONE);//先得到没有点赞的状态。
                    Log.v("AAA","state1");
                    if(state1==NO){
                        good.put(i,DONE);
                        String str1=newses.get(i).getLike();
//                        Log.v("AAAAAA","dasdasdasdasdasdasadasdasdsadasd");
//                        int year;
//                        year = Integer.parseInt(str1);
//                        year--;
//                        String str2=String.valueOf(year);
                        ((NewsViewHolder)holder).num.setText(str1); //数字。
//                        notifyItemChanged(i);
                    }
                    else{  //点赞之后。
                        good.put(i,NO);
                        String str3=newses.get(i).getLike();
                        Log.v("CCCC","dasdasdasdasdasdasadasdasdsadasd");
                        int year1;
                        year1 = Integer.parseInt(str3);
                        year1++;
                        String str4= String.valueOf(year1);
                        ((NewsViewHolder)holder).num.setText(str4); //数字。
//                        notifyItemChanged(i);
                        Log.v("AAA","dasdasda");
                    }
                }
            });
        }
    }

    public int getItemViewType(int position){
        return position == 0 ? ITEM_TYPE.ITEM_TYPE_Theme.ordinal() : ITEM_TYPE.ITEM_TYPE_Video.ordinal();
    }
    @Override
    public int getItemCount() {
        return newses.size();
    }
}
