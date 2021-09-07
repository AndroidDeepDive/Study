package com.charlezz.data.flickr

import com.charlezz.domain.Photo

object FlickrPhotoMapper{
    fun toModel(dto:FlickrPhoto):Photo{
        return Photo(
            title = dto.title,
            url = "https://live.staticflickr.com/${dto.server}/${dto.id}_${dto.secret}.jpg"
        )
    }
}

