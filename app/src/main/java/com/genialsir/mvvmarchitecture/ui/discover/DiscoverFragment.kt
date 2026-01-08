package com.genialsir.mvvmarchitecture.ui.discover

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.genialsir.mvvmarchitecture.databinding.FragmentDiscoverBinding
import com.genialsir.mvvmarchitecture.mqtt.TopicManager
import com.genialsir.mvvmcommon.base.BaseFragment
import com.genialsir.mvvmcommon.listener.setOnIntervalClickListener
import com.genialsir.iothelper.mqtt.MqttController
import com.genialsir.mvvmcommon.util.LogUtil
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
@AndroidEntryPoint
class DiscoverFragment : BaseFragment<FragmentDiscoverBinding, DiscoverViewModel>() {

    override val viewModel: DiscoverViewModel by viewModels()

    private var tfLight: Typeface? = null

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDiscoverBinding {
        return FragmentDiscoverBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        tfLight = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")
        setupLineChart()
        initMqtt()
        setData(20, 30f)
    }

    override fun observeViewModel() {}

    override fun initListener() {
        binding.tvSendMqttMsg.setOnIntervalClickListener {
            MqttController.publish(TopicManager.TEST_TOPIC, "This is MVVM Android send msg!")
            LogUtil.d("发送 MQTT 消息完成")
        }
    }


    private fun initMqtt() {
        MqttController.start(requireContext()) {
            MqttController.subscribe(TopicManager.TEST_TOPIC)
            LogUtil.d("订阅主题")
        }

        MqttController.observe(TopicManager.TEST_TOPIC).observeForever {
            LogUtil.d("MqttEventBus observe data: $it")
        }

    }

    private fun setupLineChart() {
        binding.lineChart.apply {
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            setHighlightPerDragEnabled(true)
        }

        // 图例配置
        val l: Legend = binding.lineChart.legend
        l.form = LegendForm.LINE
        l.typeface = tfLight
        l.textSize = 11f
        l.textColor = Color.BLUE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        // X 轴配置
        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.typeface = tfLight
        xAxis.textSize = 11f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        // 左 Y 轴
        val leftAxis: YAxis = binding.lineChart.axisLeft
        leftAxis.typeface = tfLight
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.axisMaximum = 200f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true

        // 右 Y 轴
        val rightAxis: YAxis = binding.lineChart.axisRight
        rightAxis.typeface = tfLight
        rightAxis.textColor = Color.RED
        rightAxis.axisMaximum = 900f
        rightAxis.axisMinimum = -200f
        rightAxis.setDrawGridLines(false)
        rightAxis.isGranularityEnabled = false

        // 点击选中值监听
        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let { entry ->
                    h?.let { highlight ->
                        binding.lineChart.centerViewToAnimated(
                            entry.x, entry.y,
                            binding.lineChart.data.getDataSetByIndex(highlight.dataSetIndex).axisDependency,
                            500
                        )
                    }
                }
            }

            override fun onNothingSelected() {}
        })
    }

    private fun setData(count: Int, range: Float) {
        val values1 = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        for (i in 0 until count) {
            values1.add(Entry(i.toFloat(), (Math.random() * (range / 2f)).toFloat() + 50))
            values2.add(Entry(i.toFloat(), (Math.random() * range).toFloat() + 450))
            values3.add(Entry(i.toFloat(), (Math.random() * range).toFloat() + 500))
        }

        val dataSet1 = LineDataSet(values1, "DataSet 1").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = ColorTemplate.getHoloBlue()
            setCircleColor(Color.WHITE)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65
            fillColor = ColorTemplate.getHoloBlue()
            highLightColor = Color.rgb(244, 117, 117)
            setDrawCircleHole(false)
            valueTextColor = ColorTemplate.getHoloBlue()
        }

        val dataSet2 = LineDataSet(values2, "DataSet 2").apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            color = Color.RED
            setCircleColor(Color.WHITE)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65
            fillColor = Color.RED
            highLightColor = Color.rgb(244, 117, 117)
            setDrawCircleHole(false)
            valueTextColor = Color.RED
        }

        val dataSet3 = LineDataSet(values3, "DataSet 3").apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            color = Color.YELLOW
            setCircleColor(Color.WHITE)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65
            fillColor = ColorTemplate.colorWithAlpha(Color.YELLOW, 200)
            highLightColor = Color.rgb(244, 117, 117)
            setDrawCircleHole(false)
            valueTextColor = Color.YELLOW
        }

        val lineData = LineData(dataSet1, dataSet2, dataSet3).apply {
            setValueTextSize(9f)
        }

        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }


}
