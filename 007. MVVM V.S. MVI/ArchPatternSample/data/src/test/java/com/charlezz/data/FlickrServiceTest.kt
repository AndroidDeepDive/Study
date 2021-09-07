package com.charlezz.data

import com.charlezz.data.flickr.FlickrRetrofitModule
import com.charlezz.data.flickr.FlickrService
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class FlickrServiceTest {

    lateinit var flickrService: FlickrService

    @Before
    fun setup() {
        val intercepter = FlickrRetrofitModule.providesIntercepter()
        val okHttpClient = FlickrRetrofitModule.providesOkHttpClient(intercepter)
        val retrofit = FlickrRetrofitModule.providesRetrofit(okHttpClient)
        this.flickrService = retrofit.create(FlickrService::class.java)
    }

    @Test
    fun getRecentTest() = runBlocking{
        val result = flickrService.getRecent()
        assert(result.stat == "ok")
    }

    @Test
    fun searchTest() = runBlocking{
        val result = flickrService.search("tree")
        assert(result.stat == "ok")
    }

}