import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('admin_token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('admin_user_info') || '{}'))

    const login = (data) => {
        token.value = data.token
        userInfo.value = data.userInfo
        localStorage.setItem('admin_token', data.token)
        localStorage.setItem('admin_user_info', JSON.stringify(data.userInfo))
    }

    const logout = () => {
        token.value = ''
        userInfo.value = {}
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user_info')
    }

    return { token, userInfo, login, logout }
})
