package com.genialsir.mvvmarchitecture.ui.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.genialsir.mvvmarchitecture.ai.TensorFlowActivity
import com.genialsir.mvvmarchitecture.databinding.FragmentDiscoverBinding
import com.genialsir.mvvmarchitecture.databinding.FragmentProfileBinding
import com.genialsir.mvvmarchitecture.ui.adjustfont.AdjustFontActivity
import com.genialsir.mvvmarchitecture.ui.discover.DiscoverViewModel
import com.genialsir.mvvmcommon.base.BaseFragment
import com.genialsir.mvvmcommon.listener.setOnIntervalClickListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
@AndroidEntryPoint
class ProfileFragment: BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel
        get() = ProfileViewModel()

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun initView() {

    }

    override fun observeViewModel() {

    }

    override fun initListener() {
        binding.llOptionSettings.setOnIntervalClickListener {
            Intent(context, AdjustFontActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.llTensorflowLite.setOnIntervalClickListener {
            Intent(context, TensorFlowActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}