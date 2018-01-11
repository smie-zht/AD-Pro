package com.app.finalpro.community;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by qqq on 2017/12/31.
 */

public class News implements Serializable {
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/socialimage/";
    private String uname;
    private String content;
    private String like;
    private String iconURL;
    private String picURL;
    //private boolean havePic; //判断图片是否为空
    public News(String uname,String content,String like,String iconURL,String picURL) {
        this.uname = uname;
        this.content = content;
        this.like = like;
        this.iconURL = iconURL;
        this.picURL = "";
        if(picURL!= null) {
                this.picURL = base_url+picURL;
        }
        else
                this.picURL ="";
    }

    public String getUname() {
        return uname;
    }

    public String getContent() {
        return content;
    }

    public String getLike() {
        return like;
    }

    public String getIconURL() {
        return iconURL;
    }



    public String getPicURL() {
        return picURL;
    }
}
