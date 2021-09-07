package com.charlezz.domain.usecase

import androidx.paging.PagingData
import com.charlezz.domain.Photo
import com.charlezz.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow

class SearchUseCase(private val photosRepository: PhotosRepository) {
    suspend operator fun invoke(keyword:String?):Flow<PagingData<Photo>>{
        return photosRepository.search(keyword)
    }
}