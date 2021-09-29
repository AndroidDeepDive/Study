package com.charlezz.mvvm.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url:String?){
    Glide.with(view).load(url).into(view)
}