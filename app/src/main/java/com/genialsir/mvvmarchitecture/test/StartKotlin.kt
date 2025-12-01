package com.genialsir.mvvmarchitecture.test

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/26
 */
class StartKotlin {

    init{
        //测试coroutineScope的受外部作用域影响的示例代码
//        example()
        //测试coroutineScope的受内部协程控制的示例代码
        main()
    }

    private suspend fun testCoroutineScope() {
        //需要放在挂起函数，继承外部作用域的生命周期，其生命周期由其内部的子协程觉得
        coroutineScope {

        }
    }


    //演示coroutineScope的取消受到外部作用域影响
    fun example() = runBlocking {
        // 外部作用域：runBlocking

        val job = launch { // 父协程
            println("Parent starts")
            coroutineScope { // 子作用域：继承自父协程的Context，包括Job
                launch { // 孙子协程 1
                    delay(1000)
                    println("Child 1 finished") // 这行不会打印
                }
                launch { // 孙子协程 2
                    delay(2000)
                    println("Child 2 finished") // 这行也不会打印
                }
            }
            println("Parent ends") // 这行也不会打印，因为coroutineScope没完成
        }

        delay(3000) // 等待500毫秒
        job.cancel() // 取消父协程 -> 传播到coroutineScope -> 传播到两个孙子协程
        println("Main cancels the parent job")
        job.join()
    }


//    suspend fun performParallelTasks(): Results = coroutineScope {
//        // 这个 coroutineScope 会等待内部两个 async 都完成后才返回
//
//        val result1 = async { networkCall1() } // 子协程1
//        val result2 = async { networkCall2() } // 子协程2
//
//        Results(result1.await(), result2.await()) // 挂起等待两个结果
//    }
//
//    // 在 ViewModel 中调用
//    fun loadData() {
//        viewModelScope.launch { // 外部作用域
//            // 挂起在此处，等待 performParallelTasks 内部的 coroutineScope 完成
//            val results = performParallelTasks()
//
//            // 只有当 performParallelTasks 内部的兩個 networkCall 都完成后，
//            // 代码才会执行到这里来更新 UI
//            updateUI(results)
//        }
//    }



    // 模拟一个耗时的网络请求
    suspend fun simulateNetworkRequest(name: String, delayMillis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val startTime = LocalTime.now().format(formatter)

        println("[$startTime] 请求 '$name' 开始执行，需要 ${delayMillis}ms")
        delay(delayMillis) // 模拟网络延迟

        val endTime = LocalTime.now().format(formatter)
        println("[$endTime] 请求 '$name' 完成")

        return "$name 的响应结果"
    }

    // 使用 coroutineScope 并行执行多个请求
    suspend fun fetchAllData(): List<String> = coroutineScope {
        println("coroutineScope 开始 - 将启动三个并行请求")

        // 使用 async 启动三个并行请求
        val request1 = async { simulateNetworkRequest("用户数据", 1000) }
        val request2 = async { simulateNetworkRequest("产品列表", 1500) }
        val request3 = async { simulateNetworkRequest("消息通知", 800) }

        println("所有请求已启动，等待结果...")

        // 等待所有请求完成并收集结果
        val results = listOf(request1.await(), request2.await(), request3.await())

        println("所有请求已完成，coroutineScope 即将返回结果")
        results
    }

    fun main() = runBlocking {
        println("主程序开始")

        val startTime = System.currentTimeMillis()

        // 调用挂起函数，它会等待内部所有协程完成
        val allData = fetchAllData()

        val duration = System.currentTimeMillis() - startTime
        println("\n总共耗时: ${duration}ms")
        println("最终结果: $allData")

        println("主程序结束")
    }

}