<script setup>
import { HomeIcon, UserIcon, PlusIcon, ChatBubbleLeftRightIcon } from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const navItems = [
  { name: '首页', icon: HomeIcon, path: '/' },
  { name: '消息', icon: ChatBubbleLeftRightIcon, path: '/message' },
  { name: '发布', icon: PlusIcon, path: '/publish', isMain: true },
  { name: '我的', icon: UserIcon, path: '/profile' },
]

const navigateTo = (path) => {
  if (path === '/profile') {
    if (userStore.isLoggedIn()) {
      router.push('/profile')
    } else {
      router.push('/login')
    }
  } else {
    router.push(path)
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div>
    <!-- Mobile Bottom Nav -->
    <div class="fixed bottom-0 left-0 z-50 w-full h-16 bg-white border-t border-gray-200 shadow-lg md:hidden">
      <div class="grid h-full max-w-lg grid-cols-4 mx-auto font-medium">
        <button 
          v-for="item in navItems" 
          :key="item.name"
          @click="navigateTo(item.path)"
          type="button" 
          class="inline-flex flex-col items-center justify-center px-5 hover:bg-gray-50 group relative"
          :class="{'text-primary': $route.path === item.path, 'text-gray-500': $route.path !== item.path}"
        >
          <!-- Special styling for the main 'Publish' button -->
          <div v-if="item.isMain" class="absolute -top-6 bg-primary rounded-full p-3 shadow-lg border-4 border-slate-50">
             <component :is="item.icon" class="w-8 h-8 text-white" />
          </div>
          <div v-else class="flex flex-col items-center">
              <component :is="item.icon" class="w-6 h-6 mb-1 group-hover:text-primary" />
              <span class="text-xs group-hover:text-primary">{{ item.name }}</span>
          </div>
        </button>
      </div>
    </div>

    <!-- Desktop Top Nav (Simple version) -->
    <div class="hidden md:block fixed top-0 left-0 z-50 w-full bg-white shadow-sm">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between h-16">
              <div class="flex">
                  <div class="flex-shrink-0 flex items-center cursor-pointer" @click="router.push('/')">
                      <span class="font-bold text-xl text-primary">CampusMarket</span>
                  </div>
              </div>
              <div class="flex items-center space-x-4">
                  <template v-if="!userStore.isLoggedIn()">
                    <button @click="router.push('/login')" class="text-gray-500 hover:text-primary px-3 py-2 rounded-md text-sm font-medium">登录 / 注册</button>
                  </template>
                  <template v-else>
                    <div class="flex items-center space-x-2 cursor-pointer" @click="router.push('/profile')">
                      <div class="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center text-teal-600 font-bold overflow-hidden">
                        <img v-if="userStore.userInfo.avatar" :src="userStore.userInfo.avatar" class="w-full h-full object-cover" />
                        <span v-else>{{ userStore.userInfo.username?.charAt(0).toUpperCase() }}</span>
                      </div>
                      <span class="text-sm font-medium text-gray-700">{{ userStore.userInfo.username }}</span>
                    </div>
                    <button @click="handleLogout" class="text-gray-400 hover:text-red-500 text-sm">退出</button>
                  </template>
                  <button @click="router.push('/publish')" class="bg-primary text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-teal-600">发布闲置</button>
              </div>
          </div>
        </div>
    </div>
  </div>
</template>
