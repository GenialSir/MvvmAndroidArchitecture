package com.genialsir.mvvmcommon.util

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ref.WeakReference
import kotlin.apply


/**
 * @author genialsir@163.com (GenialSir) on 2025/11/19
 */
object LoadingDialog {

    private var dialogRef: WeakReference<AlertDialog>? = null
    private var messageViewRef: WeakReference<TextView>? = null

    /**
     * 显示加载框
     * @param activity Activity
     * @param message 显示文字，默认 "加载中..."
     */
    fun show(activity: Activity?, message: String = "加载中...") {
        if (activity == null) return

        val dialog = dialogRef?.get()
        if (dialog == null) {
            val progressBar = ProgressBar(activity).apply {
                isIndeterminate = true
                val size = 100
                layoutParams = LinearLayout.LayoutParams(size, size)
            }

            val container = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(50, 50, 50, 50)
                gravity = Gravity.CENTER_VERTICAL
                addView(progressBar)

                val textView = TextView(activity).apply {
                    text = message
                    setPadding(30, 0, 0, 0)
                    setTextColor(Color.BLACK)
                    textSize = 16f
                }
                addView(textView)

                messageViewRef = WeakReference(textView)
            }

            val alertDialog = MaterialAlertDialogBuilder(activity)
                .setView(container)
                .setCancelable(true) // 点击外部取消
                .create()

            dialogRef = WeakReference(alertDialog)
            alertDialog.show()
        } else {
            // 更新文字
            messageViewRef?.get()?.text = message
            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }

    /** 隐藏加载框 */
    fun hide() {
        dialogRef?.get()?.dismiss()
        dialogRef = null
        messageViewRef = null
    }

    /** 是否正在显示加载框 */
    fun isShowing(): Boolean {
        return dialogRef?.get()?.isShowing == true
    }
}
