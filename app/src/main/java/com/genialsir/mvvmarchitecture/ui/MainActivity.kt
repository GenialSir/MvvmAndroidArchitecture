package com.genialsir.mvvmarchitecture.ui

import android.Manifest
import androidx.lifecycle.lifecycleScope
import com.genialsir.mvvmarchitecture.R
import com.genialsir.mvvmarchitecture.databinding.ActivityMainBinding
import com.genialsir.mvvmarchitecture.ui.discover.DiscoverFragment
import com.genialsir.mvvmarchitecture.ui.home.HomeFragment
import com.genialsir.mvvmarchitecture.ui.profile.ProfileFragment
import com.genialsir.mvvmcommon.base.BaseActivity
import com.genialsir.mvvmcommon.base.BaseFragment
import com.genialsir.mvvmcommon.bus.GlobalEventBus
import com.genialsir.mvvmcommon.util.LogUtil
import com.genialsir.permissionx.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding


    private val alarmFragment by lazy { HomeFragment() }
    private val healthFragment by lazy { DiscoverFragment() }
    private val profileFragment by lazy { ProfileFragment() }

    private var currentFragment: BaseFragment<*, *>? = null


    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionX.init(this@MainActivity)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .request({ allGranted, grantedList, deniedList ->
                if (allGranted) {
                    lifecycleScope.launch {
                        LogUtil.d(TAG, "TensorFlowActivity 权限申请成功")
                    }
                } else {
                    LogUtil.d(TAG, "TensorFlowActivity 权限申请失败: $deniedList")
                }
            })
    }

    override fun observeViewModel() {
    }

    override fun initView() {

    }

    override fun initListener() {
        //校验是否需要重新登录
        CoroutineScope(Dispatchers.Main).launch {
            GlobalEventBus.tokenInvalidEvent.collect {
                LogUtil.d(TAG, "处理Token失效逻辑")
            }
        }
        //创建主页的Fragment
        configFragment()
    }

    private fun configFragment() {
        //每次 Activity 重建后，确保 tab 和 fragment 一致
        showFragment(alarmFragment)
        //在bottomNavigation完全布局完成后执行(主要处理recreate后fragment和tab不对应的问题)
        binding.bottomNavigation.post {
            binding.bottomNavigation.selectedItemId = R.id.tab_home
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                    showFragment(alarmFragment)
                }

                R.id.tab_discover -> {
                    showFragment(healthFragment)
                }

                R.id.tab_profile -> {
                    showFragment(profileFragment)
                }
            }
            true
        }
    }

    private fun showFragment(fragment: BaseFragment<*, *>) {
        val transaction = supportFragmentManager.beginTransaction()

        // 遍历所有 Fragment，隐藏它们
        supportFragmentManager.fragments.forEach {
            transaction.hide(it)
        }

        // 如果 Fragment 已经添加过，则 show
        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.nav_host_fragment, fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }


}