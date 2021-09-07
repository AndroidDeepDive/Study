package com.charlezz.data.flickr

import com.squareup.moshi.Json

data class FlickrPhoto(
    val id:Long,
    val owner:String,
    val secret:String,
    val server:String,
    val farm: Int,
    val title:String,
    val ispublic:Int,
    @Json(name = "ispublic") val isPublic:Int,
    @Json(name = "isfriend") val isFriend:Int,
    @Json(name = "isfamily") val isFamily:Int,
)