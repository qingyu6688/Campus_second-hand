import axios from 'axios'

const service = axios.create({
    baseURL: '/api', // Use proxy
    timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
    config => {
        // TODO: Add token from store/localStorage
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    },
    error => {
        console.log(error)
        return Promise.reject(error)
    }
)

// Response interceptor
service.interceptors.response.use(
    response => {
        const res = response.data
        // Assuming backend returns { code: 200, data: ..., message: ... }
        if (res.code !== 200) {
            // Handle errors (e.g., show toast)
            console.error('API Error:', res.message)
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res.data
        }
    },
    error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

export default service
