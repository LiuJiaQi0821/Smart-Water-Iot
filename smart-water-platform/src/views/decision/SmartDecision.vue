<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/api/service'

const rawLogs = ref([])
const loading = ref(false)

const fetchDecisions = async () => {
  loading.value = true
  try {
    const res = await api.getDecisions() // 获取 List<DecisionLog>
    rawLogs.value = res || []
  } finally {
    loading.value = false
  }
}

// 动态计算统计卡片
const stats = computed(() => {
  const total = rawLogs.value.length
  const success = rawLogs.value.filter(l => l.result === '成功').length
  return {
    total,
    successRate: total > 0 ? ((success / total) * 100).toFixed(1) + '%' : '100%',
    highRisk: rawLogs.value.filter(l => l.content.includes('异常') || l.content.includes('高压')).length
  }
})

onMounted(fetchDecisions)
</script>

<template>
  <div class="p-20">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="8">
        <el-card shadow="always" class="text-center">
          <div class="stat-value text-danger">{{ stats.highRisk }}</div>
          <div class="stat-label">待核实异常日志</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always" class="text-center">
          <div class="stat-value text-success">{{ stats.successRate }}</div>
          <div class="stat-label">决策执行成功率</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always" class="text-center">
          <div class="stat-value text-primary">{{ stats.total }}</div>
          <div class="stat-label">累计自动化决策次数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card header="AI 辅助决策执行日志">
      <el-table :data="rawLogs" stripe v-loading="loading">
        <el-table-column prop="decisionTime" label="决策时间" width="180" />
        <el-table-column prop="deviceId" label="设备 ID" width="100" />
        <el-table-column prop="content" label="分析结论及建议" />
        <el-table-column prop="result" label="执行状态">
          <template #default="scope">
            <el-tag :type="scope.row.result === '成功' ? 'success' : 'warning'">
              {{ scope.row.result }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.text-center { text-align: center; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { color: #909399; margin-top: 5px; }
.text-danger { color: #F56C6C; }
.text-success { color: #67C23A; }
.text-primary { color: #409EFF; }
</style>