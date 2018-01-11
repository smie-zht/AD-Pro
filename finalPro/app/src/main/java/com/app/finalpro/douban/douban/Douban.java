package com.app.finalpro.douban.douban;

import java.util.List;

/**
 * Created by user on 2017/12/24.
 * 用来接收网络数据的实体集
 */

public class Douban {
    public int total;
    public List<Book> books;
    public static class Book{
        public String image ;
        public List<String> author;
        public String title;
        public String publisher;
        public String id;
        public String summary;
        public Rating rating;

        public String getAverageRating() {
            return rating.average;
        }
        public String getNumRaters() {  return rating.numRaters == 0 ? "暂无读者评分":String.valueOf(rating.numRaters)+"人评分"; }
        public String getTitle() {
            if(title!=null)
               return title;
            else
                return "";
        }
        public String getAllAuthor() {
            String allauthor="";
            if(author.size()>0)
                allauthor=allauthor+author.get(0);
            for(int i=1;i<author.size();i++)
                allauthor=allauthor+","+author.get(i);
            allauthor = allauthor + " ";
            if(allauthor.trim().equals("")) return "作者空缺";
            return allauthor;
        }
        public String getPublisher() {
            if(publisher!=null) {
                if (publisher.trim().equals(""))    return "出版社空缺";
                return publisher;
            }
            else return "";
        }
        public String getImageURL() {
            return image;
        }
        public String getSummary() {
            if (summary == null)
                return "暂无简介";
            if (summary.trim().equals(""))
                return "暂无简介";
            return summary;
        }
        public static  class Rating {
            public String average;
            public int numRaters;
        }
    }
}

