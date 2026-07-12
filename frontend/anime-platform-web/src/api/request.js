import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  config.headers['X-Request-Id'] =
    globalThis.crypto?.randomUUID?.() ||
    `${Date.now()}-${Math.random()}`

  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data

    if (
      body &&
      typeof body === 'object' &&
      Object.prototype.hasOwnProperty.call(body, 'code')
    ) {
      if (body.code === 200) {
        return body.data
      }

      const message = body.message || '请求失败'

      if (!response.config?.silent) {
        ElMessage.error(message)
      }

      const error = new Error(message)
      error.code = body.code
      error.response = response
      return Promise.reject(error)
    }

    return body
  },
  (error) => {
    const status = error.response?.status
    const message =
      error.response?.data?.message ||
      error.message ||
      '网络请求失败'

    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')

      if (!['/login', '/register'].includes(window.location.pathname)) {
        ElMessage.warning('登录已失效，请重新登录')
        window.location.href = '/login'
      } else if (!error.config?.silent) {
        ElMessage.error(message)
      }
    } else if (!error.config?.silent) {
      ElMessage.error(message)
    }

    return Promise.reject(error)
  }
)

export default request
