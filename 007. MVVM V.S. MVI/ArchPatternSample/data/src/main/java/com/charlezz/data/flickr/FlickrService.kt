package com.charlezz.data.flickr

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    @GET("?method=flickr.photos.search")
    suspend fun search(
        @Query("text") keyword: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): FlickrResult

    @GET("?method=flickr.photos.getRecent")
    suspend fun getRecent(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): FlickrResult


}