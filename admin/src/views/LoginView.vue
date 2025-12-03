<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { User, Lock, Monitor, CircleCheck, Connection } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/admin/login', form.value)
    if (res) {
      userStore.login({
        token: res.token,
        userInfo: res.userInfo
      })
      ElMessage.success('登录成功')
      router.push('/')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-4">
    <div class="flex w-full max-w-4xl bg-white rounded-2xl shadow-2xl overflow-hidden h-[550px]">
      <!-- Left Side -->
      <div
        class="hidden lg:flex lg:w-1/2 bg-[#0f766e] flex-col justify-between p-8 relative overflow-hidden text-white">
        <!-- Background Shapes -->
        <div class="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -mr-16 -mt-16 blur-3xl"></div>
        <div class="absolute bottom-0 left-0 w-48 h-48 bg-teal-500/20 rounded-full -ml-10 -mb-10 blur-3xl"></div>
        <div
          class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full h-full bg-gradient-to-br from-transparent to-black/10 pointer-events-none">
        </div>

        <!-- Header -->
        <div class="relative z-10 flex items-center space-x-2">
          <div
            class="w-8 h-8 bg-white/10 rounded-lg flex items-center justify-center backdrop-blur-sm border border-white/20">
            <Monitor class="w-5 h-5 text-white" />
          </div>
          <span class="text-lg font-bold tracking-wide">CampusAdmin</span>
        </div>

        <!-- Main Content -->
        <div class="relative z-10 space-y-6">
          <div class="space-y-3">
            <h1 class="text-2xl font-bold leading-tight">
              构建绿色校园<br />
              连接每一次信任
            </h1>
            <p class="text-teal-100 text-opacity-90 text-xs leading-relaxed max-w-xs">
              欢迎使用校易后台管理系统。轻松管理商品审核、用户反馈及交易数据分析。
            </p>
          </div>

          <div class="space-y-2">
            <div class="flex items-center space-x-2 text-xs font-medium text-teal-50">
              <CircleCheck class="w-4 h-4 text-teal-300" />
              <span>实时数据监控大屏</span>
            </div>
            <div class="flex items-center space-x-2 text-xs font-medium text-teal-50">
              <CircleCheck class="w-4 h-4 text-teal-300" />
              <span>智能违规内容过滤</span>
            </div>
            <div class="flex items-center space-x-2 text-xs font-medium text-teal-50">
              <CircleCheck class="w-4 h-4 text-teal-300" />
              <span>多级权限角色管理</span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="relative z-10 text-[10px] text-teal-200/60">
          &copy; 2024 CampusTrade Admin System.
        </div>
      </div>

      <!-- Right Side -->
      <div class="flex-1 flex flex-col justify-center items-center bg-white p-8 relative">
        <div class="w-full max-w-xs space-y-6">
          <div class="space-y-1.5 text-center">
            <h2 class="text-xl font-bold text-gray-900">登录后台</h2>
            <p class="text-gray-500 text-xs">请输入您的账号信息进行管理</p>
          </div>

          <el-form :model="form" size="default" class="space-y-4" @submit.prevent>
            <div class="space-y-1">
              <label class="text-xs font-medium text-gray-700 ml-1">账号</label>
              <el-form-item class="mb-0">
                <el-input v-model="form.username" placeholder="admin" :prefix-icon="User" class="custom-input" />
              </el-form-item>
            </div>

            <div class="space-y-1">
              <div class="flex justify-between items-center ml-1">
                <label class="text-xs font-medium text-gray-700">密码</label>
              </div>
              <el-form-item class="mb-0">
                <el-input v-model="form.password" type="password" placeholder="••••••••" show-password
                  :prefix-icon="Lock" class="custom-input" @keyup.enter="handleLogin" />
              </el-form-item>
            </div>

            <el-button type="primary"
              class="w-full !h-9 !text-sm !font-medium !rounded-lg !bg-[#0f766e] !border-[#0f766e] hover:!bg-teal-800 hover:!border-teal-800 transition-all duration-300 shadow-lg shadow-teal-900/10 mt-2"
              :loading="loading" @click="handleLogin">
              安全登录 <el-icon class="ml-2">
                <Connection />
              </el-icon>
            </el-button>
          </el-form>

          <div class="absolute bottom-4 left-0 w-full flex justify-center space-x-3 text-[10px] text-gray-400">
            <a href="#" class="hover:text-gray-600 transition-colors">帮助</a>
            <span class="text-gray-300">|</span>
            <a href="#" class="hover:text-gray-600 transition-colors">隐私</a>
            <span class="text-gray-300">|</span>
            <a href="#" class="hover:text-gray-600 transition-colors">关于</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-input__wrapper) {
  background-color: #f9fafb;
  box-shadow: none !important;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 4px 12px;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #d1d5db;
  background-color: #f3f4f6;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: #0f766e;
  box-shadow: 0 0 0 1px #0f766e !important;
}

:deep(.el-input__inner) {
  color: #1f2937;
  height: 32px;
  font-size: 13px;
}

:deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

:deep(.el-input__icon) {
  color: #9ca3af;
}
</style>
