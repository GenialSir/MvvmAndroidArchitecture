package com.genialsir.mvvmcommon.base

import android.R
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.genialsir.mvvmcommon.util.FontManager

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        //字体大小切换的逻辑
        super.attachBaseContext(FontManager.wrapContext(newBase))
    }

    //确保拿到的资源都已经带有正确的字体缩放配置
    override fun getResources(): Resources {
        val res = super.getResources()
        FontManager.applyToResources(res)
        return super.getResources()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FontManager.applyNow(this)
        initViewBinding()
        observeViewModel()
        initView()
        initListener()
    }


    protected abstract fun initViewBinding()

    protected abstract fun observeViewModel()

    protected abstract fun initView()

    protected abstract fun initListener()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}