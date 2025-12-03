import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
    baseURL: '/api', // Proxy will handle this
    timeout: 5000
})

service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('admin_token')
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code === 200) {
            return res.data
        } else {
            ElMessage.error(res.msg || 'Error')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
    },
    error => {
        ElMessage.error(error.message || 'Request Failed')
        return Promise.reject(error)
    }
)

export default service
