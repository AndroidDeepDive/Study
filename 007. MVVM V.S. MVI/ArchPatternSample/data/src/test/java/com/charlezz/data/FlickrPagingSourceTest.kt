package com.charlezz.data

import androidx.paging.PagingSource
import com.charlezz.data.fake.FakeFlickrService
import com.charlezz.data.flickr.FlickrPagingSource
import com.charlezz.data.flickr.FlickrPhoto
import com.charlezz.data.flickr.FlickrPhotoMapper
import com.charlezz.domain.Photo
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class FlickrPagingSourceTest {

    private val fakePhotos = listOf(
        createPhoto(),
        createPhoto(),
        createPhoto(),
    )
    private val fakeService = FakeFlickrService().apply {
        fakePhotos.forEach{ photo -> addPhoto(photo)}
    }

    @Test
    fun pagingSourceTest() = runBlocking {
        val pagingSource = FlickrPagingSource(fakeService, "")
        assertEquals(
            expected = PagingSource.LoadResult.Page<Int, Photo>(
                data = listOf(FlickrPhotoMapper.toModel(fakePhotos[0]),FlickrPhotoMapper.toModel(fakePhotos[1])),
                prevKey = null,
                nextKey = null,
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    private fun createPhoto(): FlickrPhoto {
        return FlickrPhoto(
            Random.nextLong(),
            "owner",
            "owner",
            "server",
            0,
            "title",
            0,
            0,
            0,
            0
        )

    }

}