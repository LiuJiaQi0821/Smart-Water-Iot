<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import * as echarts from 'echarts'
import { useDark } from '@vueuse/core'
import api from '@/api/service'

const chartRef = ref(null)
const isDark = useDark()
let myChart = null

// 核心响应式数据
const monitor = ref({
  records: [],       // 原始记录列表
  stats: {           // 统计数据
    totalDevices: 0,
    avgPressure: 0,
    onlineRate: '100%',
    todayAlarms: 0
  },
  currentDevice: {   // 当前选中/最新的设备状态
    pressure: '0.00',
    flowRate: 0,
    voltage: '3.7V', // 后端 Entity 暂无此字段，建议后期添加
    signal: '-75dBm' // 后端 Entity 暂无此字段
  },
  times: [],
  flowRates: [],
  totalUsage: []
})

const userRole = localStorage.getItem('userRole') || 'user'
const isAdmin = computed(() => userRole === 'admin')

// 获取并处理数据
const fetchData = async () => {
  try {
    const res = await api.getMonitorData({ current: 1, size: 50 }); // 获取较多样本用于统计
    if (res && res.records) {
      const data = res.records;
      monitor.value.records = data;

      // 1. 计算统计信息 (Admin 视图)
      const deviceIds = new Set(data.map(r => r.deviceId));
      const totalPressure = data.reduce((sum, r) => sum + (r.pressure || 0), 0);
      
      monitor.value.stats = {
        totalDevices: deviceIds.size,
        avgPressure: data.length > 0 ? (totalPressure / data.length).toFixed(2) : 0,
        onlineRate: '98.5%', // 暂无后端心跳逻辑，保留逻辑展示
        todayAlarms: 0       // 需配合 DecisionLog 接口计算
      };

      // 2. 准备时序图表数据
      const sortedData = [...data].reverse();
      monitor.value.times = sortedData.map(r => new Date(r.recordTime).toLocaleTimeString());
      monitor.value.flowRates = sortedData.map(r => r.flowRate);
      monitor.value.totalUsage = sortedData.map(r => r.totalUsage);

      // 3. 设置当前实时状态
      const latest = data[0] || {};
      monitor.value.currentDevice.pressure = latest.pressure || '0.00';
      monitor.value.currentDevice.flowRate = latest.flowRate || 0;

      initChart();
    }
  } catch (e) {
    console.error("获取监测数据失败", e);
  }
}

const initChart = () => {
  if (myChart) myChart.dispose();
  myChart = echarts.init(chartRef.value, isDark.value ? 'dark' : undefined);
  myChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: monitor.value.times },
    yAxis: [{ type: 'value', name: '流速 (L/h)' }],
    series: [{ 
      name: '瞬时流速', 
      type: 'line', 
      smooth: true, 
      data: monitor.value.flowRates,
      areaStyle: { opacity: 0.2 } 
    }]
  });
}

onMounted(fetchData);
watch(isDark, initChart);
</script>

<template>
  <div class="p-20">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="8">
        <el-card shadow="hover" header="实时风险评估">
          <div class="center-content">
            <el-progress 
              type="dashboard" 
              :percentage="monitor.stats.avgPressure > 0.6 ? 85 : 15" 
              :color="[{ color: '#67c23a', percentage: 40 }, { color: '#f56c6c', percentage: 80 }]" 
            />
            <p>{{ monitor.stats.avgPressure > 0.6 ? '检测到管网压力异常' : '运行状态良好' }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover" :header="isAdmin ? '全网运行概览' : '终端实时状态'">
          <el-descriptions v-if="isAdmin" border :column="2">
            <el-descriptions-item label="活跃设备数">{{ monitor.stats.totalDevices }} 台</el-descriptions-item>
            <el-descriptions-item label="系统在线率">{{ monitor.stats.onlineRate }}</el-descriptions-item>
            <el-descriptions-item label="平均管网压力">{{ monitor.stats.avgPressure }} MPa</el-descriptions-item>
            <el-descriptions-item label="当前待处理告警">{{ monitor.stats.todayAlarms }} 条</el-descriptions-item>
          </el-descriptions>

          <el-descriptions v-else border :column="2">
            <el-descriptions-item label="实时压力">{{ monitor.currentDevice.pressure }} MPa</el-descriptions-item>
            <el-descriptions-item label="瞬时流速">{{ monitor.currentDevice.flowRate }} L/h</el-descriptions-item>
            <el-descriptions-item label="电池电压">{{ monitor.currentDevice.voltage }}</el-descriptions-item>
            <el-descriptions-item label="信号强度">{{ monitor.currentDevice.signal }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-card header="用水流量时序分析 (15min)">
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>
  </div>
</template>