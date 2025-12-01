package com.genialsir.mvvmarchitecture.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.genialsir.mvvmarchitecture.ui.login.LoginActivity
import com.genialsir.mvvmarchitecture.databinding.ActivitySplashBinding
import com.genialsir.mvvmcommon.base.BaseActivity
import com.genialsir.mvvmcommon.constant.SPLASH_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
@AndroidEntryPoint
class SplashActivity: BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        navigateToMainScreen()
        navigateToMainScreen2()
//        navigateToMainScreen3()
    }

    override fun initListener() {

    }


    override fun observeViewModel() {
    }

    override fun initView() {

    }


    private fun navigateToMainScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            val nextScreenIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }, SPLASH_DELAY.toLong())
    }

    private fun navigateToMainScreen2() {
        //用lifecycleScope启动生命周期的协程，若当前activity，协程任务也会跟随销毁。不会像使用handler一样还是继续执行任务。
        lifecycleScope.launch {
            delay(SPLASH_DELAY.toLong())
            val nextScreenIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }
    }

    private fun navigateToMainScreen3() {
        //GlobalScope生命周期跟随APP
        GlobalScope.launch {
            delay(SPLASH_DELAY.toLong())
            val nextScreenIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }
    }
}