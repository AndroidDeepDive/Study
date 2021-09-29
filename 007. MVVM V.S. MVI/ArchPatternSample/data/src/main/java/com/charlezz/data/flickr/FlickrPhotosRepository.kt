package com.charlezz.data.flickr

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.charlezz.domain.Photo
import com.charlezz.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow

class FlickrPhotosRepository(private val service: FlickrService) : PhotosRepository {
    override suspend fun search(keyword: String?): Flow<PagingData<Photo>> {
        return Pager(PagingConfig(30)){
            FlickrPagingSource(service, keyword)
        }.flow
    }

}