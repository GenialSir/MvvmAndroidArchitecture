package com.genialsir.mvvmarchitecture.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.genialsir.mvvmcommon.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/16
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableLiveData("我的内容")
    val text: LiveData<String> = _text


}