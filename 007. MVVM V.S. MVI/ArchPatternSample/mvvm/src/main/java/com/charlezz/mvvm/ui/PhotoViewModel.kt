package com.charlezz.mvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.charlezz.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
) : ViewModel() {

    private val keyword: MutableStateFlow<String> = MutableStateFlow("")

    val items: Flow<PagingData<PhotoUiModel>> = flowOf(
        keyword.asStateFlow().map { PagingData.empty() },
        keyword
            .flatMapLatest { keyword -> searchUseCase(keyword) }
            .map { pagingData -> pagingData.map { PhotoUiModel(it) } }
            .cachedIn(viewModelScope)
    ).flattenMerge(2)

    fun load(keyword:String){
        this.keyword.value = keyword
    }

}