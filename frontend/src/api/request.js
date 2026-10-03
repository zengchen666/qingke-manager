import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearLogin } from '@/utils/auth'
import router from '@/router'

/**
 * axios 实例
 *
 * <p>后端统一返回 {code, msg, data}：code=1 成功、code=0 失败。
 * 这里在拦截器里统一处理，业务代码只关心 data，不用每次都判断 code。
 */
const request = axios.create({
  // 统一走 /api 前缀：开发环境由 Vite 代理到 8080，生产环境由 nginx 代理
  baseURL: '/api',
  timeout: 10000
})

// ---------- 请求拦截：统一携带 token ----------
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 与后端 TokenFilter 约定一致：token 放在请求头
      config.headers.token = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ---------- 响应拦截：统一处理业务码与 HTTP 错误 ----------
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code === 1) {
      return res
    }
    // code=0：业务失败（用户名密码错误、字段重复等），统一弹提示
    const msg = (res && res.msg) || '操作失败'
    ElMessage.error(msg)
    return Promise.reject(new Error(msg))
  },
  (error) => {
    const status = error.response && error.response.status
    if (status === 401) {
      // 后端过滤器拦截：token 缺失或失效
      clearLogin()
      ElMessage.error('登录已失效，请重新登录')
      if (router.currentRoute.value.path !== '/login') {
        router.replace('/login')
      }
    } else {
      ElMessage.error(error.message || '网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
