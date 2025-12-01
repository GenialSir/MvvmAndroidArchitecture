package com.genialsir.mvvmcommon.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/16
 *
 * 通用 BaseFragment
 * 自动管理 ViewBinding 生命周期，统一封装 MVVM 模式
 * @param VB  ViewBinding 类型
 * @param VM  ViewModel 类型（如果不用 ViewModel，可以传 BaseViewModel 或 Nothing）
 */
abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = initViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
        initListener()
    }

    protected abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected abstract fun initView()

    protected abstract fun observeViewModel()

    protected abstract fun initListener()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}