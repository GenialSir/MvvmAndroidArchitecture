package com.genialsir.mvvmarchitecture.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.genialsir.mvvmcommon.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/16
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableLiveData("发现内容")
    val text: LiveData<String> = _text


}