/*
 *
 *  MIT License
 *
 *  Copyright (c) 2017 Alibaba Group
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package com.app.finalpro.douban.douban;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.finalpro.R;

import static java.security.AccessController.getContext;

/**
 * Created by mikeafc on 15/11/26.
 */
public class UltraPagerAdapter extends PagerAdapter {
    private static String[] alltoptitle = {"新名字的故事","鱼王","不可思议的朋友","杀死一只知更鸟","步履不停","长夜难明","小说课","现代艺术150年","被仰望与被遗忘的","黑旗"};
    private static int[] alltopcover = {R.drawable.top1cover,R.drawable.top2cover,R.drawable.top3cover,R.drawable.top4cover,R.drawable.top5cover,
            R.drawable.top6cover,R.drawable.top7cover,R.drawable.top8cover,R.drawable.top9cover,R.drawable.top10cover};
    private static String[] alltopauthor = {"[意] 埃莱娜·费兰特","[俄] 维克托·阿斯塔菲耶夫","[日] 田岛征彦","[美] 哈珀·李","[日] 是枝裕和",
            "紫金陈","毕飞宇","[英] 威尔·贡培兹","[美] 盖伊·特立斯","[美] 乔比·沃里克"};
    private static String[] alltoppublisher = {"人民文学出版社","理想国 | 广西师范大学出版社","北京联合出版公司","译林出版社","北京联合出版公司",
            "云南人民出版社","人民文学出版社","理想国 | 广西师范大学出版社","上海人民出版社","中信出版社"};
    private static float[] alltoprating = {9.2f,9.1f,9.1f,9.1f,8.7f,8.6f,8.6f,9.0f,8.3f,8.4f};
    private static int[] alltopnumraters = {3884,10391,776,1777,7601,4743,1517,857,898,1171};
    private static int[] alltopsummary = {R.string.top1summary,R.string.top2summary,R.string.top3summary,R.string.top4summary,R.string.top5summary,
           R.string.top6summary,R.string.top7summary,R.string.top8summary,R.string.top9summary,R.string.top10summary};
    private boolean isMultiScr;
    Context context;
    public UltraPagerAdapter(Context context,boolean isMultiScr) {
        this.context = context;
        this.isMultiScr = isMultiScr;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(container.getContext()) .inflate(R.layout.bookflow,null);
      //  LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.coverflow_book, null);
        //new LinearLayout(container.getContext());
        //TextView textView = (TextView) linearLayout.findViewById(R.id.pager_textview);
       // textView.setText(position + "");
        ImageView imageView = (ImageView) constraintLayout.findViewById(R.id.top_cover);
        imageView.setAdjustViewBounds(true);
        imageView.setImageResource(alltopcover[position]);
        ViewGroup.LayoutParams para;
        para = imageView.getLayoutParams();
        para.height = 600;
        para.width = 400;
        imageView.setLayoutParams(para);
        TextView topn = (TextView) constraintLayout.findViewById(R.id.top_n);
        topn.setText("TOP "+String.valueOf(position+1));
        topn.setTextSize(17);
        TextView toptitle = (TextView) constraintLayout.findViewById(R.id.top_title);
        toptitle.setText(alltoptitle[position]);
        constraintLayout.setId(R.id.item_id);
        container.addView(constraintLayout);
//        linearLayout.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, container.getContext().getResources().getDisplayMetrics());
//        linearLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, container.getContext().getResources().getDisplayMetrics());
        return constraintLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ConstraintLayout view = (ConstraintLayout) object;
        container.removeView(view);
    }
    public String getTitle(int position) {  return alltoptitle[position];   }
    public String getAuthor(int position) { return alltopauthor[position];  }
    public String getPublisher(int position) {  return alltoppublisher[position];  }
    public int getCoverRes(int position) {  return alltopcover[position];   }
    public float getRating(int position) {  return alltoprating[position];  }
    public int getSummaryRes(int position) {return alltopsummary[position]; }
    public int getNumraters(int position) { return alltopnumraters[position];   }

}
