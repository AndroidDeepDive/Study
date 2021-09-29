package com.charlezz.mvvm.ui

import com.charlezz.domain.Photo
import com.charlezz.mvvm.R

class PhotoUiModel(private val photo: Photo) {

    fun getLayout(): Int = R.layout.view_photo

    fun getImageUrl(): String {
        return photo.url
    }

}