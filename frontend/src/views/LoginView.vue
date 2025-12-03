<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserIcon, LockClosedIcon, AcademicCapIcon } from '@heroicons/vue/24/outline'
import { login, register } from '@/api/user'
import { showToast, showSuccessToast, showFailToast } from 'vant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(true) // Toggle between Login and Register
const isLoading = ref(false)

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  schoolName: ''
})

const usernameError = ref('')

const validateUsername = () => {
  if (!isLogin.value && form.value.username) {
    if (!/^\d+$/.test(form.value.username)) {
      usernameError.value = '账号必须为纯数字学号'
    } else {
      usernameError.value = ''
    }
  } else {
    usernameError.value = ''
  }
}

const handleSubmit = async () => {
  if (isLoading.value) return
  
  // 再次校验
  validateUsername()
  if (usernameError.value) {
    return
  }

  isLoading.value = true
  
  try {
    if (isLogin.value) {
      console.log('Logging in with', form.value)
      const data = await login({
        username: form.value.username,
        password: form.value.password
      })
      // Save user info/token via store
      userStore.setLoginState(data.token, data.user)
      // Redirect
      router.push('/')
    } else {
      console.log('Registering with', form.value)
      
      if (form.value.password !== form.value.confirmPassword) {
        showFailToast('两次密码输入不一致')
        return
      }
      await register({
        username: form.value.username,
        password: form.value.password,
        schoolName: form.value.schoolName,
        status: 1 // Default active
      })
      showSuccessToast('注册成功，请登录')
      isLogin.value = true
    }
  } catch (error) {
    showFailToast(error.message || '操作失败')
  } finally {
    isLoading.value = false
  }
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  usernameError.value = '' // Clear error on toggle
  // Reset form
  form.value = {
    username: '',
    password: '',
    confirmPassword: '',
    schoolName: ''
  }
}
</script>

<template>
  <div class="flex min-h-[calc(100vh-4rem)] flex-col justify-center px-6 py-12 lg:px-8 relative overflow-hidden">
    <!-- Dynamic Background -->
    <div class="absolute inset-0 -z-10 bg-gradient-to-br from-teal-50 via-white to-orange-50 animate-gradient-xy"></div>
    <div class="absolute -top-20 -left-20 w-72 h-72 bg-teal-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-blob"></div>
    <div class="absolute -bottom-20 -right-20 w-72 h-72 bg-orange-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-blob animation-delay-2000"></div>

    <div class="sm:mx-auto sm:w-full sm:max-w-sm relative z-10">
      <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-teal-100 shadow-inner transform transition-transform hover:scale-110 duration-300">
        <AcademicCapIcon class="h-10 w-10 text-teal-600" />
      </div>
      <h2 class="mt-6 text-center text-2xl font-bold leading-9 tracking-tight text-slate-900 transition-all duration-300">
        {{ isLogin ? '欢迎回到校园集市' : '加入我们' }}
      </h2>
      <p class="mt-2 text-center text-sm text-slate-500 transition-all duration-300">
        {{ isLogin ? '登录你的账号开始交易' : '注册账号，开启校园闲置之旅' }}
      </p>
    </div>

    <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm relative z-10">
      <div class="bg-white/80 backdrop-blur-lg px-6 py-12 shadow-xl ring-1 ring-gray-900/5 rounded-2xl sm:px-10 transition-all duration-500">
        <form class="space-y-6" @submit.prevent="handleSubmit">
          
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-medium leading-6 text-slate-900">账号 / 学号</label>
            <div class="relative mt-2 group">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 transition-colors duration-300 group-focus-within:text-teal-500">
                <UserIcon class="h-5 w-5 text-slate-400 group-focus-within:text-teal-500" aria-hidden="true" />
              </div>
              <input 
                id="username" 
                v-model="form.username" 
                name="username" 
                type="text" 
                required 
                @input="validateUsername"
                class="block w-full rounded-full border-0 py-2.5 pl-10 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-teal-500 sm:text-sm sm:leading-6 transition-all duration-300" 
                :class="{'ring-red-500 focus:ring-red-500': usernameError}"
                placeholder="请输入学号或用户名"
              />
            </div>
            <p v-if="usernameError" class="mt-1 text-xs text-red-500 pl-3">{{ usernameError }}</p>
          </div>

          <!-- School Name (Register only) -->
          <Transition name="slide-fade">
            <div v-if="!isLogin">
              <label for="school" class="block text-sm font-medium leading-6 text-slate-900">学校名称</label>
              <div class="relative mt-2 group">
                <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <AcademicCapIcon class="h-5 w-5 text-slate-400 group-focus-within:text-teal-500" aria-hidden="true" />
                </div>
                <input 
                  id="school" 
                  v-model="form.schoolName" 
                  name="school" 
                  type="text" 
                  required 
                  class="block w-full rounded-full border-0 py-2.5 pl-10 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-teal-500 sm:text-sm sm:leading-6 transition-all duration-300" 
                  placeholder="请输入你的学校"
                />
              </div>
            </div>
          </Transition>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium leading-6 text-slate-900">密码</label>
            <div class="relative mt-2 group">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <LockClosedIcon class="h-5 w-5 text-slate-400 group-focus-within:text-teal-500" aria-hidden="true" />
              </div>
              <input 
                id="password" 
                v-model="form.password" 
                name="password" 
                type="password" 
                required 
                class="block w-full rounded-full border-0 py-2.5 pl-10 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-teal-500 sm:text-sm sm:leading-6 transition-all duration-300" 
                placeholder="请输入密码"
              />
            </div>
          </div>

          <!-- Confirm Password (Register only) -->
          <Transition name="slide-fade">
            <div v-if="!isLogin">
              <label for="confirm-password" class="block text-sm font-medium leading-6 text-slate-900">确认密码</label>
              <div class="relative mt-2 group">
                <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <LockClosedIcon class="h-5 w-5 text-slate-400 group-focus-within:text-teal-500" aria-hidden="true" />
                </div>
                <input 
                  id="confirm-password" 
                  v-model="form.confirmPassword" 
                  name="confirm-password" 
                  type="password" 
                  required 
                  class="block w-full rounded-full border-0 py-2.5 pl-10 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-teal-500 sm:text-sm sm:leading-6 transition-all duration-300" 
                  placeholder="请再次输入密码"
                />
              </div>
            </div>
          </Transition>

          <!-- Submit Button -->
          <div>
            <button 
              type="submit" 
              :disabled="isLoading"
              class="flex w-full justify-center rounded-full bg-teal-500 px-3 py-2.5 text-sm font-semibold leading-6 text-white shadow-lg shadow-teal-500/30 hover:bg-teal-400 hover:shadow-teal-500/50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-teal-500 transition-all duration-300 transform active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isLoading ? '处理中...' : (isLogin ? '登录' : '注册') }}
            </button>
          </div>
        </form>

        <!-- Toggle Mode -->
        <p class="mt-10 text-center text-sm text-slate-500">
          {{ isLogin ? '还没有账号？' : '已有账号？' }}
          <button @click="toggleMode" class="font-semibold leading-6 text-teal-600 hover:text-teal-500 transition-colors duration-200">
            {{ isLogin ? '立即注册' : '立即登录' }}
          </button>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}
</style>
