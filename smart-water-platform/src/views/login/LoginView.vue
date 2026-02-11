<script setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Check, Monitor } from '@element-plus/icons-vue'
import api from '@/api/service' // 引入真实 API

const router = useRouter()
const loading = ref(false)
const isRegister = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await api.login({ username: form.username, password: form.password });
    if (res.code === 200) {
      // 存储后端返回的用户角色和名称
      localStorage.setItem('userRole', res.data.role);
      localStorage.setItem('username', res.data.username);
      ElMessage.success(`欢迎回来，${res.data.username}`);
      router.push('/dashboard');
    } else {
      ElMessage.error(res.msg || '登录失败');
    }
  } catch (error) {
    ElMessage.error('网络请求失败');
  } finally {
    loading.value = false;
  }
}

const handleRegister = async () => {
  loading.value = true
  try {
    const res = await api.register({ username: form.username, password: form.password });
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录');
      isRegister.value = false;
    } else {
      ElMessage.error(res.msg || '该账号已存在');
    }
  } catch (error) {
    ElMessage.error('注册请求失败');
  } finally {
    loading.value = false;
  }
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      isRegister.value ? handleRegister() : handleLogin();
    }
  })
}

const toggleMode = async () => {
  isRegister.value = !isRegister.value
  await nextTick()
  if (formRef.value) {
    formRef.value.resetFields()
    formRef.value.clearValidate()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (isRegister.value && value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = reactive({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
})
</script>

<template>
  <div class="login-container">
    <div class="login-box animate__animated animate__fadeInUp">
      <div class="header">
        <div class="logo-circle"><el-icon :size="30" color="#fff"><Monitor /></el-icon></div>
        <h2>智慧水务物联网平台</h2>
        <p class="subtitle">{{ isRegister ? '注册新账户' : 'Smart Water IoT System' }}</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" size="large">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="账号" :prefix-icon="User" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password /></el-form-item>
        <el-form-item prop="confirmPassword" v-if="isRegister"><el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" :prefix-icon="Check" show-password /></el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="handleSubmit" round>
          {{ isRegister ? '立即注册' : '登 录' }}
        </el-button>
        <div class="toggle-box">
          <span v-if="!isRegister">没有账号？ <a @click="toggleMode">立即注册</a></span>
          <span v-else>已有账号？ <a @click="toggleMode">返回登录</a></span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container { height: 100vh; width: 100vw; background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%); display: flex; justify-content: center; align-items: center; }
.login-box { width: 420px; padding: 40px; background: rgba(255, 255, 255, 0.95); border-radius: 16px; text-align: center; }
.logo-circle { width: 60px; height: 60px; background: linear-gradient(135deg, #409EFF, #00C6FB); border-radius: 50%; margin: 0 auto 15px; display: flex; align-items: center; justify-content: center; }
.subtitle { color: #909399; font-size: 14px; margin-bottom: 30px; }
.submit-btn { width: 100%; height: 45px; margin-top: 10px; }
.toggle-box { margin-top: 20px; font-size: 14px; }
.toggle-box a { color: #409EFF; cursor: pointer; font-weight: 600; }
</style>