package com.genialsir.mvvmarchitecture.ai

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter

import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/22
 */
class MNISTClassifier(context: Context) {
    private var interpreter: Interpreter

    init {
        val model = loadModelFile(context, "mnist_model.tflite")
        interpreter = Interpreter(model)

        // 打印输入输出形状，帮助调试
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        Log.d("MNISTClassifier", "Input shape: ${inputShape.joinToString()}")
        Log.d("MNISTClassifier", "Output shape: ${outputShape.joinToString()}")
    }

    fun predictDigit(input: Array<Array<FloatArray>>): Int {
        val output = Array(1) { FloatArray(10) }
        interpreter.run(input, output)
        return output[0].indices.maxByOrNull { output[0][it] } ?: -1
    }
}



@Throws(IOException::class)
fun loadModelFile(context: Context, modelFileName: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelFileName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}
