package com.charlezz.data.flickr

class FlickrPhotos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<FlickrPhoto>,
)