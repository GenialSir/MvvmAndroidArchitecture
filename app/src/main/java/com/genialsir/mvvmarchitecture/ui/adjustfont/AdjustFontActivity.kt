package com.genialsir.mvvmarchitecture.ui.adjustfont


import com.genialsir.mvvmarchitecture.databinding.ActivityAdjustFontBinding
import com.genialsir.mvvmcommon.base.BaseActivity
import com.genialsir.mvvmcommon.util.FontManager


/**
 * @author genialsir@163.com (GenialSir) on 2025/10/15
 */
class AdjustFontActivity: BaseActivity() {

    private lateinit var binding: ActivityAdjustFontBinding

    override fun initViewBinding() {
        binding = ActivityAdjustFontBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {
    }

    override fun initView() {

    }

    override fun initListener() {
        binding.btnSmallFont.setOnClickListener {
            // 先保存、更新 FontManager，再重建当前 Activity
            FontManager.setScale(this, 1.0f)
            recreate()
        }
        binding.btnBigFont.setOnClickListener {
            FontManager.setScale(this, 1.5f)
            recreate()
        }
    }


}