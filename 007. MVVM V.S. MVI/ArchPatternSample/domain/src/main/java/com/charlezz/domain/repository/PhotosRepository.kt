package com.charlezz.domain.repository

import androidx.paging.PagingData
import com.charlezz.domain.Photo
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {

    suspend fun search(keyword: String?): Flow<PagingData<Photo>>

}