package com.charlezz.mvvm.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.mvvm.R
import com.charlezz.mvvm.databinding.ActivityPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.paging.LoadState

@AndroidEntryPoint
class PhotoActivity : AppCompatActivity() {

    private val viewModel: PhotoViewModel by viewModels()

    private val adapter = PhotoAdapter()

    private val layoutManager: GridLayoutManager by lazy {
        GridLayoutManager(this, calculateSpanCount())
    }

    private val binding: ActivityPhotoBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_photo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager

        binding.search.setOnClickListener {
            viewModel.load(binding.editText.text.toString())
        }

        adapter.addLoadStateListener { loadStates ->
            binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.items.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layoutManager.spanCount = calculateSpanCount()
    }

    private fun calculateSpanCount(): Int {
        return resources.displayMetrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            105.0f,
            resources.displayMetrics
        ).toInt()
    }

}