package com.genialsir.mvvmarchitecture.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.genialsir.mvvmarchitecture.data.DataRepository
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmcommon.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/16
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    // LiveData 保存分页数据
    private val _aiNewsLiveData = MutableLiveData<PagingData<AINewsItem>>()
    val aiNewsLiveData: LiveData<PagingData<AINewsItem>> get() = _aiNewsLiveData


    private val _text = MutableLiveData("首页内容")
    val text: LiveData<String> = _text

    fun refresh() {
        _text.value = "首页刷新时间: ${System.currentTimeMillis()}"
    }


    fun reqAiNewsPagingData() {
        //获取告警的视频数据
        viewModelScope.launch {
            dataRepository.reqAiNewsPagingData().collectLatest { pagingData ->
                _aiNewsLiveData.value = pagingData
            }
        }
    }
}