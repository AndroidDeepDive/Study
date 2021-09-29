package com.charlezz.data.fake

import com.charlezz.data.flickr.FlickrPhoto
import com.charlezz.data.flickr.FlickrPhotos
import com.charlezz.data.flickr.FlickrResult
import com.charlezz.data.flickr.FlickrService
import kotlin.math.min

class FakeFlickrService : FlickrService {

    private val result = ArrayList<FlickrPhoto>()

    fun addPhoto(flickrPhoto: FlickrPhoto) {
        result.add(flickrPhoto)
    }

    override suspend fun search(keyword: String, page: Int, perPage: Int): FlickrResult {
        return getPagedList(page, perPage)
    }

    override suspend fun getRecent(page: Int, perPage: Int): FlickrResult {
        return getPagedList(page, perPage)
    }

    private fun getPagedList(page: Int, perpage: Int): FlickrResult {
        val fromIndex = 100 * (page - 1)
        val toIndex = min(fromIndex + perpage, result.size)
        val photos = result.subList(fromIndex, toIndex)
        return FlickrResult(
            FlickrPhotos(
                page = page,
                pages = result.size / perpage,
                perpage = perpage,
                result.size,
                photos
            ), "ok"
        )
    }

}