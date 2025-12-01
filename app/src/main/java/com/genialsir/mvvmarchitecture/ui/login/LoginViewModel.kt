package com.genialsir.mvvmarchitecture.ui.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.genialsir.mvvmarchitecture.data.DataRepository
import com.genialsir.mvvmarchitecture.data.dto.login.LoginRequest
import com.genialsir.mvvmarchitecture.data.dto.login.LoginResponse
import com.genialsir.mvvmcommon.base.BaseViewModel
import com.genialsir.mvvmcommon.data.DataResource
import com.genialsir.mvvmcommon.error.CHECK_YOUR_FIELDS
import com.genialsir.mvvmcommon.util.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    // 登录结果
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _loginLiveData = MutableLiveData<DataResource<LoginResponse>>()
    val loginLiveData: LiveData<DataResource<LoginResponse>> get() = _loginLiveData

    // Toast消息
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _showToast = MutableLiveData<SingleEvent<String>>()
    val showToast: LiveData<SingleEvent<String>> get() = _showToast

    fun doLogin(userName: String, password: String) {
        if (userName.isBlank() || password.isBlank()) {
            _loginLiveData.value = DataResource.Error(CHECK_YOUR_FIELDS)
            return
        }

        _loginLiveData.value = DataResource.Loading()

        viewModelScope.launch {
            // 模拟延迟
            delay(1000)
            dataRepository.doLogin(LoginRequest(userName, password))
                .collect { res ->
                    when (res) {
                        is DataResource.Success -> _loginLiveData.value = res
                        is DataResource.Error -> {
                            _loginLiveData.value = res.errorCode?.let { DataResource.Error(it) }
                            showToastMessage("账号或密码错误")
                        }

                        is DataResource.Loading -> _loginLiveData.value = DataResource.Loading()
                    }
                }
        }
    }

    fun showToastMessage(msg: String) {
        _showToast.value = SingleEvent(msg)
    }
}
