package com.charlezz.data.flickr

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


@InstallIn(SingletonComponent::class)
@Module
object FlickrRetrofitModule {

    // TODO: 2021/09/08 API_KEY 숨기기
    @Provides
    fun providesIntercepter():Interceptor{
        return Interceptor { chain->
            var request = chain.request()
            val newUrl = request.url().newBuilder()
                .addQueryParameter("api_key", "82373875cee5bbf255dc5a0be0aba815")
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback","1")
                .build()

            request = request.newBuilder().url(newUrl).build()
            chain.proceed(request)
        }
    }

    @Provides
    fun providesOkHttpClient(interceptor: Interceptor):OkHttpClient{
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
    }

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return  Retrofit.Builder()
            .baseUrl("https://www.flickr.com/services/rest/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
}