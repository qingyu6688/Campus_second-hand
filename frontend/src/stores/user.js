import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))

    const isLoggedIn = () => {
        return !!token.value
    }

    const setLoginState = (newToken, newUser) => {
        token.value = newToken
        userInfo.value = newUser
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(newUser))
    }

    const logout = () => {
        token.value = ''
        userInfo.value = {}
        localStorage.removeItem('token')
        localStorage.removeItem('user')
    }

    return {
        token,
        userInfo,
        isLoggedIn,
        setLoginState,
        logout
    }
})
