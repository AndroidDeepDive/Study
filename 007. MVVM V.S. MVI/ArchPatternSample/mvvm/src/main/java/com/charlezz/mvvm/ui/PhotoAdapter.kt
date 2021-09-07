package com.charlezz.mvvm.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.charlezz.mvvm.BR
import com.charlezz.mvvm.R

class PhotoAdapter : PagingDataAdapter<PhotoUiModel, BindableViewHolder<*>>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PhotoUiModel>() {
            override fun areItemsTheSame(
                oldItem: PhotoUiModel,
                newItem: PhotoUiModel
            ): Boolean {
                return oldItem.getImageUrl() == newItem.getImageUrl()
            }

            override fun areContentsTheSame(
                oldItem: PhotoUiModel,
                newItem: PhotoUiModel
            ): Boolean {
                return true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let { it.getLayout() } ?: R.layout.view_photo
    }

    override fun onBindViewHolder(holder: BindableViewHolder<*>, position: Int) {
        getItem(position)?.let {
            holder.binding.setVariable(BR.model, it)
            holder.binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<*> {
        return BindableViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            )
        )
    }


}