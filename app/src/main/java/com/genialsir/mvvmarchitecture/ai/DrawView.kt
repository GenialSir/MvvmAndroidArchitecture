package com.genialsir.mvvmarchitecture.ai

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/22
 */

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 9f
        isAntiAlias = false // 改成 false 更接近训练时二值化
    }
    private val path = Path()
    private val bitmap = Bitmap.createBitmap(280, 280, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> path.lineTo(event.x, event.y)
        }
        invalidate()
        return true
    }

    fun getBitmap(): Bitmap {
        draw(canvas)
        return bitmap
    }

    fun clear() {
        path.reset()
        invalidate()
    }

    fun saveBitmap(bitmap: Bitmap) {
        try {
            val file = File(context.getExternalFilesDir(null), "debug_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            Log.d("DrawView", "Image saved to: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
