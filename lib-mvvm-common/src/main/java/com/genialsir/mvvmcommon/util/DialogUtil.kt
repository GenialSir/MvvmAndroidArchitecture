package com.genialsir.mvvmcommon.util

import android.content.Context
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlin.apply
import kotlin.let
import kotlin.ranges.coerceAtMost

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/29
 * 1. 保持旧方法签名不变（兼容所有已有调用）
 * 2. 新增可选参数版本（支持 hint / inputType）
 * 3. 输入框自动 padding，避免贴边
 * 4. 平板宽度自适应统一封装
 */
object DialogUtil {

    /* ---------------------- 原方法 ---------------------- */
    fun showInputDialog(
        context: Context,
        title: String,
        initialText: String? = null,
        onConfirm: (String) -> Unit
    ) {
        showInputDialogInternal(
            context = context,
            title = title,
            initialText = initialText,
            hint = "请输入内容",
            inputType = null,
            onConfirm = onConfirm
        )
    }

    /* ---------------------- 新增重载方法（支持 hint / inputType） ---------------------- */
    fun showInputDialog(
        context: Context,
        title: String,
        initialText: String? = null,
        hint: String = "请输入内容",
        inputType: Int? = null,
        onConfirm: (String) -> Unit
    ) {
        showInputDialogInternal(context, title, initialText, hint, inputType, onConfirm)
    }

    /* ---------------------- 内部统一处理逻辑 ---------------------- */
    private fun showInputDialogInternal(
        context: Context,
        title: String,
        initialText: String?,
        hint: String,
        inputType: Int?,
        onConfirm: (String) -> Unit
    ) {
        val edit = EditText(context).apply {
            setText(initialText)
            this.hint = hint
            setSelection(text.length)
            setPadding(40, 50, 40, 50)  // 避免贴边，体验更好
            inputType?.let { this.inputType = it }
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(edit)
            .setPositiveButton("确定") { _, _ ->
                onConfirm(edit.text.toString())
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
        applyTabletWidth(dialog, context)
    }

    /* ---------------------- 确认弹窗 ---------------------- */
    fun showConfirmDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onConfirm: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onConfirm() }
            .setNegativeButton(negativeText, null)
            .create()

        dialog.show()
        applyTabletWidth(dialog, context)
    }

    /* ---------------------- 删除确认弹窗 ---------------------- */
    fun showDeleteDialog(
        context: Context,
        title: String = "删除提示",
        message: String = "确定要删除吗？",
        positiveText: String = "删除",
        negativeText: String = "取消",
        onConfirm: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onConfirm() }
            .setNegativeButton(negativeText, null)
            .create()

        dialog.show()
        applyTabletWidth(dialog, context)
    }


    /* ---------------------- 平板宽度适配（统一封装） ---------------------- */
    private fun applyTabletWidth(dialog: AlertDialog, context: Context) {
        val metrics = context.resources.displayMetrics
        val widthPx = (metrics.widthPixels * 0.8).toInt()
        val maxWidthPx = (800 * metrics.density).toInt()

        dialog.window?.setLayout(
            widthPx.coerceAtMost(maxWidthPx),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}