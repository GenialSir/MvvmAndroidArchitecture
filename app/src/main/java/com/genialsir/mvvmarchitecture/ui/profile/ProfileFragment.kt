package com.genialsir.mvvmarchitecture.ui.profile

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.genialsir.mvvmarchitecture.BaseApp
import com.genialsir.mvvmarchitecture.ai.TensorFlowActivity
import com.genialsir.mvvmarchitecture.databinding.FragmentDiscoverBinding
import com.genialsir.mvvmarchitecture.databinding.FragmentProfileBinding
import com.genialsir.mvvmarchitecture.ui.adjustfont.AdjustFontActivity
import com.genialsir.mvvmarchitecture.ui.discover.DiscoverViewModel
import com.genialsir.mvvmcommon.base.BaseFragment
import com.genialsir.mvvmcommon.listener.setOnIntervalClickListener
import com.genialsir.mvvmcommon.util.LogUtil
import com.genialsir.mvvmcommon.util.ToastUtil
import com.genialsir.permissionx.PermissionX
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
@AndroidEntryPoint
class ProfileFragment: BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    private val TAG = ProfileFragment::class.java.simpleName

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

    /**
     * 检查蓝牙位置等权限
     */
    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        // 存储读取权限：仅在 Android 13（TIRAMISU）之前请求
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Android 12 / S 及以上使用新的蓝牙权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            // 如需广播可选地添加 BLUETOOTH_ADVERTISE
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            // 兼容旧版本
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        // 位置权限（扫描蓝牙需要）
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    // 权限已授予，可以开始操作
                    if (checkBluetoothEnabled()) {
                        LogUtil.i(TAG, "所有权限已授予，蓝牙已开启")
                    } else {
                        LogUtil.i(TAG, "请先开启蓝牙")
                    }
                } else {
                    val deniedStr = deniedList.joinToString(", ")
                    LogUtil.e(TAG, "未授予的权限: $deniedStr")
                    ToastUtil.showCenter(BaseApp.context, "需要授予蓝牙和位置权限才能使用此功能\n未授予: $deniedStr")
                }
            }
    }

    /**
     * 检查蓝牙是否开启
     */
    private fun checkBluetoothEnabled(): Boolean {
        val bluetoothManager = BaseApp.context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter?.isEnabled == true
    }
}