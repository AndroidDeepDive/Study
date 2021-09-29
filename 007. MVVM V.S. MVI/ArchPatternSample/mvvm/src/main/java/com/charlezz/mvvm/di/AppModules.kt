package com.charlezz.mvvm.di

import com.charlezz.data.flickr.FlickrPhotosRepository
import com.charlezz.data.flickr.FlickrService
import com.charlezz.domain.repository.PhotosRepository
import com.charlezz.domain.usecase.SearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideFlickrService(retrofit:Retrofit): FlickrService{
        return retrofit.create(FlickrService::class.java)
    }

    @Provides
    fun providePhotosRepository(flickrService: FlickrService):PhotosRepository{
        return FlickrPhotosRepository(flickrService)
    }

    @Provides
    fun provideSearchUseCase(photosRepository: PhotosRepository):SearchUseCase{
        return SearchUseCase(photosRepository)
    }

}


