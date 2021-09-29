package com.charlezz.mvvm.ui

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BindableViewHolder<T : ViewDataBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root) {
}