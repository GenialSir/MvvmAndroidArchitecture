package com.genialsir.mvvmarchitecture.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.genialsir.mvvmarchitecture.R

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/22
 */
class TensorFlowActivity : AppCompatActivity() {
    private lateinit var classifier: MNISTClassifier
    private lateinit var drawView: DrawView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor_flow)

        classifier = MNISTClassifier(this)
        drawView = findViewById(R.id.drawView)

        findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val bitmap = drawView.getBitmap()

            //这么写是识别不准的，和MNIST数据分布不一致
            //简单缩放会把整个画板等比例压缩到 28×28。
            //问题：
            //数字可能贴边或者太小。
            //不符合 MNIST 训练分布（数字居中 + padding）。
            //所以必须裁剪 + 缩放 + 居中。
//            val resized = Bitmap.createScaledBitmap(bitmap, 28, 28, true)
//            // 转成 0~1 的 float 数组
//            val input = Array(1) { Array(28) { FloatArray(28) } }
//            for (x in 0 until 28) {
//                for (y in 0 until 28) {
//                    val pixel = resized.getPixel(x, y)
//                    val value = 1f - (Color.red(pixel) / 255.0f)
//                    input[0][y][x] = value
//                }
//            }

            drawView.saveBitmap(bitmap) // 保存原始位图
            val input = preprocessBitmapCentroid(bitmap)
            val digit = classifier.predictDigit(input)
            findViewById<TextView>(R.id.tvResult).text = "预测结果: $digit"
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            drawView.clear()
        }
    }

    /**
     * 裁剪 + 居中 + 缩放：保证手写数字和 MNIST 数据分布一致
     * 灰度化 + 归一化：用 1 - gray/255f，黑色像素对应 1
     * 关闭抗锯齿：画笔更接近 MNIST 手写笔画
     */
    private fun preprocessBitmap(bitmap: Bitmap): Array<Array<FloatArray>> {
        // 1. 裁剪数字边界
        val cropped = cropToContent(bitmap)
//        val cropped = cropToContentFast(bitmap)
        // 2. 缩放到 20x20
        //MNIST 的每张手写数字图片是 28x28 的灰度图，但数字本身并没有占满整个图片，数字通常是 居中且大小大约 20x20 左右，四周留有空白（padding）。这样做有几个原因：
        //保留边缘空白可以 防止数字贴边，影响模型学习。
        //模型训练时的数据都是这种居中数字 + 四周空白的格式，如果输入图片和 MNIST 的分布不一致，识别效果会差。
        val scaled = Bitmap.createScaledBitmap(cropped, 20, 20, false)
        // 3. 放到 28x28 中心
        val centered = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(centered)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(scaled, 4f, 4f, null)

        // 4. 转 float 数组 0~1
        val input = Array(1) { Array(28) { FloatArray(28) } }
        for (y in 0 until 28) {
            for (x in 0 until 28) {
                val pixel = centered.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                input[0][y][x] = 1f - gray / 255f
            }
        }
        return input
    }

    //left = 数字最左边的像素 x 坐标
    //right = 数字最右边的像素 x 坐标
    //top = 数字最上边的像素 y 坐标
    //bottom = 数字最下边的像素 y 坐标
    //这样就能用这四个边界把数字“抠出来”，得到裁剪后的矩形区域。
    private fun cropToContent(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var top = height
        var left = width
        var right = 0
        var bottom = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                //只要像素不是接近纯白，就当成数字的一部分。
                //这里的 gray 是像素的灰度值，范围 0–255（0=黑，255=白）。
                //判断条件是 gray < 250，意思是“只要像素不是接近纯白，就当成是数字的一部分”。
                //为什么不是 gray < 255？因为抗锯齿、灰色笔迹等可能会导致边缘不是纯白，而是 250 左右的浅灰。如果阈值放宽一些，可以把这些笔迹算进去。
                if (gray < 250) { // 非白色
                    //left 一开始被初始化为整张图的最大宽度（width）。
                    //这里如果找到更靠左的“非白色”像素，就更新 left。
                    //最终 left 会是数字的最左边界。
                    if (x < left) left = x
                    //right 一开始为 0。
                    //如果发现更靠右的“非白色”像素，就更新 right。
                    //最终 right 会是数字的最右边界。
                    if (x > right) right = x
                    //top 一开始为整张图的高度（height）。
                    //这里是找数字的最上边界。
                    if (y < top) top = y
                    //bottom 一开始为 0。
                    //这里是找数字的最下边界。
                    if (y > bottom) bottom = y
                }
            }
        }

        if (left > right || top > bottom) {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height) // 全白，返回原图
        }

        return Bitmap.createBitmap(bitmap, left, top, right - left + 1, bottom - top + 1)
    }

    private fun cropToContentFast(
        bitmap: Bitmap,
        threshold: Int = 240,   // 灰度阈值，越小越严格
        padding: Int = 4        // 裁剪后再加的边距
    ): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        var left = w
        var right = -1
        var top = h
        var bottom = -1

        var idx = 0
        for (y in 0 until h) {
            val rowStart = y * w
            for (x in 0 until w) {
                val color = pixels[rowStart + x]
                val alpha = (color ushr 24) and 0xff
                if (alpha == 0) { idx++; continue } // 透明当做背景
                val r = (color shr 16) and 0xff
                val g = (color shr 8) and 0xff
                val b = color and 0xff
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                if (gray < threshold) {
                    if (x < left) left = x
                    if (x > right) right = x
                    if (y < top) top = y
                    if (y > bottom) bottom = y
                }
                idx++
            }
        }

        if (right < left || bottom < top) {
            // 全白：返回一个小的全白图，避免后续缩放出现异常
            return Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.WHITE)
            }
        }

        val l = (left - padding).coerceAtLeast(0)
        val t = (top - padding).coerceAtLeast(0)
        val r = (right + padding).coerceAtMost(w - 1)
        val b = (bottom + padding).coerceAtMost(h - 1)
        return Bitmap.createBitmap(bitmap, l, t, r - l + 1, b - t + 1)
    }

    /**
     * 把用户手写图裁剪到“非白色区域”。
     *
     * 等比缩放，使数字最长边为 20px（保持纵横比）。
     *
     * 把缩放后的图放到 28×28 的白底画布中心（初步居中）。
     *
     * 将 28×28 图像转成灰度并计算像素加权重心（质心）。
     *
     * 把图像平移，使质心落在 28×28 的中央（进一步居中）。
     *
     * 输出最终的 Array(1) { Array(28) { FloatArray(28) } }，黑色像素映射为 1，白色为 0，作为模型输入。
     */
    private fun preprocessBitmapCentroid(bitmap: Bitmap): Array<Array<FloatArray>> {
        // 1. 裁剪
        val cropped = cropToContentFast(bitmap)

        // 2. 缩放到 20×20（保持纵横比）
        val w = cropped.width
        val h = cropped.height
        val scale = if (w > h) 20f / w else 20f / h
        val scaledW = (w * scale).toInt().coerceAtLeast(1)
        val scaledH = (h * scale).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(cropped, scaledW, scaledH, false)

        // 3. 放到 28×28 白底
        var centered = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(centered)
        canvas.drawColor(Color.WHITE)
        val left = (28 - scaledW) / 2f
        val top = (28 - scaledH) / 2f
        canvas.drawBitmap(scaled, left, top, null)

        // 4. 转灰度数组
        val pixels = IntArray(28 * 28)
        centered.getPixels(pixels, 0, 28, 0, 0, 28, 28)
        val img = Array(28) { FloatArray(28) }
        var sumX = 0f
        var sumY = 0f
        var sumWeight = 0f

        for (y in 0 until 28) {
            for (x in 0 until 28) {
                val c = pixels[y * 28 + x]
                val gray = (Color.red(c) + Color.green(c) + Color.blue(c)) / 3f
                val value = 1f - gray / 255f // 黑色=1，白色=0
                img[y][x] = value
                if (value > 0.05f) { // 忽略很浅的点
                    sumX += x * value
                    sumY += y * value
                    sumWeight += value
                }
            }
        }

        // 5. 计算重心并平移
        if (sumWeight > 0) {
            val cx = sumX / sumWeight
            val cy = sumY / sumWeight
            val shiftX = (28 / 2 - cx).toInt()
            val shiftY = (28 / 2 - cy).toInt()

            // 平移矩阵
            val matrix = android.graphics.Matrix()
            matrix.postTranslate(shiftX.toFloat(), shiftY.toFloat())

            val shifted = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888)
            val canvas2 = Canvas(shifted)
            canvas2.drawColor(Color.WHITE)
            canvas2.drawBitmap(centered, matrix, null)
            centered = shifted
        }

        // 6. 再转一次 float 数组作为最终输入
        val result = Array(1) { Array(28) { FloatArray(28) } }
        centered.getPixels(pixels, 0, 28, 0, 0, 28, 28)
        for (y in 0 until 28) {
            for (x in 0 until 28) {
                val c = pixels[y * 28 + x]
                val gray = (Color.red(c) + Color.green(c) + Color.blue(c)) / 3f
                result[0][y][x] = 1f - gray / 255f
            }
        }
        return result
    }


}