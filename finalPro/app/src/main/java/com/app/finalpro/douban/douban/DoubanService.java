package com.app.finalpro.douban.douban;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by user on 2017/12/20.
 * https://api.douban.com/v2/book/search?q=****
 * count
 */

public interface DoubanService {

    @GET("/v2/book/search")
    Observable<Douban> getSearchByQstart(@Query("q") String s_q, @Query("start") int start);

    @GET("/v2/book/search")
    Observable<Douban> getSearchByTstart(@Query("tag") String s_t, @Query("start") int start);
}