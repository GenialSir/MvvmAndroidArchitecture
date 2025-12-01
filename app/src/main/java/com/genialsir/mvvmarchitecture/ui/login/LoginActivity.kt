package com.genialsir.mvvmarchitecture.ui.login

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.genialsir.mvvmarchitecture.data.dto.login.LoginResponse
import com.genialsir.mvvmarchitecture.databinding.ActivityLoginBinding
import com.genialsir.mvvmarchitecture.ui.MainActivity
import com.genialsir.mvvmcommon.base.BaseActivity
import com.genialsir.mvvmcommon.data.DataResource
import com.genialsir.mvvmcommon.util.observeKt
import com.genialsir.mvvmcommon.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun initViewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {
        loginViewModel.loginLiveData.observe(this) { handleLoginResult(it) }
        observeKt( loginViewModel.showToast) { msg ->
            ToastUtil.showCenter(this, msg.peekContent())
        }
    }

    override fun initView() {}

    override fun initListener() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.doLogin(username, password)
        }
    }

    private fun handleLoginResult(res: DataResource<LoginResponse>) {
        when (res) {
            is DataResource.Loading -> binding.rlLoaderView.visibility = View.VISIBLE
            is DataResource.Success -> {
                binding.rlLoaderView.visibility = View.GONE
                // 可以拿到 LoginResponse
                val userInfo = res.data
                navigateToMainScreen(userInfo)
            }
            is DataResource.Error -> {
                binding.rlLoaderView.visibility = View.GONE
            }
        }
    }

    private fun navigateToMainScreen(userInfo: LoginResponse?) {
        // 可以把 userInfo 传给 MainActivity 或保存到本地
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

