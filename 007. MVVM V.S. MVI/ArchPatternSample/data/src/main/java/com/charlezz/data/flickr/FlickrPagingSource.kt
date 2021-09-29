package com.charlezz.data.flickr

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.charlezz.domain.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlickrPagingSource(
    private val service: FlickrService,
    private val keyword: String?
) : PagingSource<Int, Photo>() {

    companion object {
        private const val FIRST_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {

        val loadSize = params.loadSize
        val key = params.key ?: FIRST_PAGE

        return withContext(Dispatchers.IO){
            val result = if (keyword.isNullOrEmpty()) {
                service.getRecent(key, loadSize)
            } else {
                service.search(keyword, key, loadSize)
            }
            val data = result.photos.photo.map { FlickrPhotoMapper.toModel(it) }

            val prevKey: Int? = if (result.photos.page == 1) { // first page
                null
            } else {
                result.photos.page - 1
            }

            val nextKey: Int? = if (result.photos.page < result.photos.pages) { // last page
                result.photos.page + 1
            } else {
                null
            }
            LoadResult.Page(data, prevKey, nextKey)
        }

    }
}