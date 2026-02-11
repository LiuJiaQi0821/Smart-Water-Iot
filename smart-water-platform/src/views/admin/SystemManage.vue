<template>
  <div class="admin-page">
    <h2>系统分级管理</h2>
    <el-tabs type="border-card">
      <el-tab-pane label="用户管理">
        <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon style="margin-bottom: 15px;" />
        <el-button type="primary" style="margin-bottom: 15px;">新增管理员</el-button>
        <el-table :data="users" border v-loading="loading">
          <el-table-column prop="name" label="用户名" />
          <el-table-column prop="role" label="角色" />
          <el-table-column label="操作"><el-button link type="primary">编辑</el-button></el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="操作日志">
        <el-timeline>
          <el-timeline-item timestamp="2023-10-27" placement="top">
            <el-card><h4>管理员 执行了全网数据查询</h4></el-card>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import service from '@/api/service'

const users = ref([])
const loading = ref(false)
const errorMsg = ref('')

const fetchUsers = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await service.getUsers()
    // 假设后端返回的数据结构匹配或进行简单适配
    // 如果后端返回 username，这里需要对应修改 el-table 的 prop 或者转换数据
    const rawList = Array.isArray(res) ? res : (res.records || [])
    users.value = rawList.map(item => ({
      ...item,
      name: item.name || item.username // 兼容 name 或 username 字段
    }))
  } catch (error) {
    console.error('获取用户列表失败', error)
    errorMsg.value = '获取数据失败: ' + (error.message || '未知错误')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-page { padding: 20px; }
</style>