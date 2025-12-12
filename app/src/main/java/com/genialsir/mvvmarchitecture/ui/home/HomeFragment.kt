package com.genialsir.mvvmarchitecture.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.databinding.FragmentHomeBinding
import com.genialsir.mvvmcommon.adapter.LoadStateAdapter
import com.genialsir.mvvmcommon.base.BaseFragment
import com.genialsir.mvvmcommon.util.LogUtil
import com.genialsir.mvvmcommon.util.observeKt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/16
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()

    private val aiNewsAdapter = AiNewsAdapter()

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.rvAiNews.layoutManager = LinearLayoutManager(requireContext())
        // 设置Adapter + LoadStateFooter
        val footerAdapter = LoadStateAdapter { aiNewsAdapter.retry() }
        binding.rvAiNews.adapter = aiNewsAdapter.withLoadStateFooter(footer = footerAdapter)
        binding.srlAiNews.setColorSchemeResources(android.R.color.holo_blue_light)
    }

    override fun observeViewModel() {
        observeKt(viewModel.aiNewsLiveData, ::handleAiNews)
        //请求新闻数据
        viewModel.reqAiNewsPagingData()
    }

    override fun initListener() {
        binding.srlAiNews.setOnRefreshListener {
            lifecycleScope.launch {
                aiNewsAdapter.refresh()
            }
        }

        aiNewsAdapter.addLoadStateListener(loadStateListener)
    }

    private fun handleAiNews(newsPagingData: PagingData<AINewsItem>) {
        lifecycleScope.launch {
            aiNewsAdapter.submitData(newsPagingData)
        }
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = { loadStates ->
        when (val refreshState = loadStates.refresh) {
            is LoadState.Loading -> {
                binding.srlAiNews.isRefreshing = true
                LogUtil.d("HomeFragment", "新闻列表加载中...")
            }

            is LoadState.NotLoading -> {
                binding.srlAiNews.isRefreshing = false
                LogUtil.d("HomeFragment", "新闻列表加载完成")
            }

            is LoadState.Error -> {
                binding.srlAiNews.isRefreshing = false
                LogUtil.e("HomeFragment", "新闻加载失败: ${refreshState.error.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        aiNewsAdapter.removeLoadStateListener(loadStateListener)
    }
}
