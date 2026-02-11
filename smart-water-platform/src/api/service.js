import axios from 'axios';

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 对应后端控制器的 RequestMapping 公共前缀
  timeout: 10000
});

// 响应拦截器：统一处理后端返回的格式
request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API 请求错误:', error);
    return Promise.reject(error);
  }
);

export default {
  // ① 登录 - 对应 AuthController.login
  login(data) {
    return request.post('/auth/login', data);
  },
  
  // ② 注册 - 对应 AuthController.register
  register(data) {
    return request.post('/auth/register', data);
  },

  // ③ 获取监测数据 - 对应 MonitorController.search
  getMonitorData(params) {
    // 后端返回 IPage 结构，包含 records 列表
    return request.get('/monitor/search', { params });
  },

  // ④ 获取决策建议/日志 - 对应 AdminController.list
  getDecisions() {
    // 后端返回 List<DecisionLog>
    return request.get('/admin/decisions');
  },

  // ⑤ 上传设备数据 - 对应 MonitorController.upload
  uploadData(data) {
    return request.post('/monitor/upload', data);
  },

  // ⑥ 获取用户列表 - 管理员功能
  getUsers() {
    return request.get('/admin/users');
  }
}