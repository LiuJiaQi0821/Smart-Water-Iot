<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import api from '@/api/service'

const chartRef = ref(null)

const analyze = async () => {
  const res = await api.getMonitorData({ size: 50 }); // 获取较多数据点进行建模
  if (res && res.records) {
    const records = res.records.reverse();
    const chart = echarts.init(chartRef.value);
    chart.setOption({
      title: { text: '真实时序数据建模趋势' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['瞬时流速', '累计用水'] },
      xAxis: { type: 'category', data: records.map(r => new Date(r.recordTime).toLocaleTimeString()) },
      yAxis: { type: 'value' },
      series: [
        { name: '瞬时流速', type: 'line', smooth: true, data: records.map(r => r.flowRate) },
        { name: '累计用水', type: 'bar', data: records.map(r => r.totalUsage) }
      ]
    });
  }
}

onMounted(analyze);
</script>

<template>
  <div class="habit-analysis p-20">
    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <span>用水习惯多维分析建模</span>
          <el-button type="primary" size="small" @click="analyze">刷新分析结果</el-button>
        </div>
      </template>
      <div ref="chartRef" style="height: 500px;"></div>
    </el-card>
  </div>
</template>