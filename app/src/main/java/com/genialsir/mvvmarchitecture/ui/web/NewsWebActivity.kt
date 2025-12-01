package com.genialsir.mvvmarchitecture.ui.web

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.genialsir.mvvmarchitecture.databinding.ActivityNewsWebBinding
import com.genialsir.mvvmcommon.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
@AndroidEntryPoint
class NewsWebActivity : BaseActivity() {

    private lateinit var binding: ActivityNewsWebBinding

    override fun initViewBinding() {
        binding = ActivityNewsWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {
        // 当前无需 ViewModel 观察
    }

    override fun initView() {
        val url = intent.getStringExtra("url")

        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "无效的新闻链接", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initWebView()
        binding.webView.loadUrl(url)
    }

    override fun initListener() {
        // 可选：设置返回按钮，如果你的布局里有标题栏
        binding.ivBack?.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initWebView() {
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.displayZoomControls = false
        settings.builtInZoomControls = false

        // 进度 / 加载状态
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressBar?.apply {
                    visibility = View.VISIBLE
                    progress = newProgress
                    if (newProgress >= 100) visibility = View.GONE
                }
            }
        }

        // 正常页面加载
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Toast.makeText(this@NewsWebActivity, "加载失败，请稍后再试", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        binding.webView.apply {
            loadUrl("about:blank")
            clearHistory()
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
}
