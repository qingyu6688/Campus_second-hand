<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import {
  UserCircleIcon,
  MapPinIcon,
  AcademicCapIcon,
  ArrowRightOnRectangleIcon,
  ShoppingBagIcon,
  PencilSquareIcon,
  ChevronRightIcon,
  CurrencyYenIcon
} from '@heroicons/vue/24/outline'

const userStore = useUserStore()
const router = useRouter()

const stats = ref({
  publishedCount: 0,
  soldCount: 0,
  favoriteCount: 0
})

onMounted(async () => {
  try {
    const res = await request.get('/user/stats')
    stats.value = res
  } catch (error) {
    console.error('Failed to fetch user stats', error)
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <!-- Header Section with Gradient -->
    <div
      class="bg-gradient-to-br from-teal-500 to-emerald-600 pt-12 pb-24 px-6 rounded-b-[2.5rem] shadow-lg relative overflow-hidden">
      <!-- Decorative circles -->
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-black/5 rounded-full -ml-10 -mb-10 blur-2xl"></div>

      <div class="flex items-center space-x-5 text-white relative z-10">
        <div class="relative group">
          <div class="w-20 h-20 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner">
            <div class="w-full h-full rounded-full bg-white overflow-hidden">
              <img v-if="userStore.userInfo.avatar" :src="userStore.userInfo.avatar"
                class="w-full h-full object-cover" />
              <div v-else
                class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400 text-2xl font-bold">
                {{ userStore.userInfo.username?.charAt(0).toUpperCase() }}
              </div>
            </div>
          </div>
          <!-- Edit Icon Badge -->
          <div
            class="absolute bottom-0 right-0 bg-white text-teal-600 p-1.5 rounded-full shadow-lg cursor-pointer hover:scale-110 transition-transform"
            @click="router.push('/profile/edit')">
            <PencilSquareIcon class="w-3.5 h-3.5" />
          </div>
        </div>

        <div class="flex-1">
          <h2 class="text-2xl font-bold tracking-tight">{{ userStore.userInfo.nickname || userStore.userInfo.username }}
          </h2>
          <div
            class="flex items-center text-teal-50 text-xs mt-2 bg-white/10 w-fit px-3 py-1 rounded-full backdrop-blur-md border border-white/10">
            <AcademicCapIcon class="w-3.5 h-3.5 mr-1.5" />
            <span>{{ userStore.userInfo.schoolName || '未认证学校' }}</span>
          </div>
          <p class="text-teal-100 text-xs mt-2 opacity-80 font-light tracking-wide">学号: {{ userStore.userInfo.username
            }}</p>
        </div>
      </div>
    </div>

    <!-- Stats Card (Floating) -->
    <div
      class="mx-5 -mt-12 bg-white rounded-2xl shadow-lg shadow-gray-200/50 p-5 flex justify-around items-center relative z-10 mb-6">
      <div class="flex flex-col items-center cursor-pointer group" @click="router.push('/my-goods')">
        <span class="font-bold text-xl text-gray-800 group-hover:text-teal-600 transition-colors">{{
          stats.publishedCount }}</span>
        <span class="text-xs text-gray-400 mt-1">我发布的</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center cursor-pointer group" @click="router.push('/my-sold')">
        <span class="font-bold text-xl text-gray-800 group-hover:text-teal-600 transition-colors">{{ stats.soldCount
          }}</span>
        <span class="text-xs text-gray-400 mt-1">我卖出的</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center cursor-pointer group" @click="router.push('/my-favorites')">
        <span class="font-bold text-xl text-gray-800 group-hover:text-teal-600 transition-colors">{{ stats.favoriteCount
          }}</span>
        <span class="text-xs text-gray-400 mt-1">我的收藏</span>
      </div>
    </div>

    <!-- Menu List -->
    <div class="mx-5 space-y-4">
      <!-- Group 1 -->
      <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div
          class="p-4 flex items-center justify-between hover:bg-gray-50 cursor-pointer border-b border-gray-50 last:border-0 transition-colors"
          @click="router.push('/profile/edit')">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-blue-50 flex items-center justify-center text-blue-500">
              <UserCircleIcon class="w-5 h-5" />
            </div>
            <span class="text-gray-700 font-medium text-sm">个人资料</span>
          </div>
          <ChevronRightIcon class="w-4 h-4 text-gray-300" />
        </div>

        <div
          class="p-4 flex items-center justify-between hover:bg-gray-50 cursor-pointer border-b border-gray-50 last:border-0 transition-colors"
          @click="router.push('/address')">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-orange-50 flex items-center justify-center text-orange-500">
              <MapPinIcon class="w-5 h-5" />
            </div>
            <span class="text-gray-700 font-medium text-sm">我的地址</span>
          </div>
          <ChevronRightIcon class="w-4 h-4 text-gray-300" />
        </div>

        <div class="p-4 flex items-center justify-between hover:bg-gray-50 cursor-pointer transition-colors"
          @click="router.push('/my-goods')">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-purple-50 flex items-center justify-center text-purple-500">
              <ShoppingBagIcon class="w-5 h-5" />
            </div>
            <span class="text-gray-700 font-medium text-sm">我的闲置</span>
          </div>
          <ChevronRightIcon class="w-4 h-4 text-gray-300" />
        </div>

        <div class="p-4 flex items-center justify-between hover:bg-gray-50 cursor-pointer transition-colors"
          @click="router.push('/my-orders')">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-teal-50 flex items-center justify-center text-teal-500">
              <ShoppingBagIcon class="w-5 h-5" />
            </div>
            <span class="text-gray-700 font-medium text-sm">我买到的</span>
          </div>
          <ChevronRightIcon class="w-4 h-4 text-gray-300" />
        </div>

        <div class="p-4 flex items-center justify-between hover:bg-gray-50 cursor-pointer transition-colors"
          @click="router.push('/my-sold')">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-500">
              <CurrencyYenIcon class="w-5 h-5" />
            </div>
            <span class="text-gray-700 font-medium text-sm">我卖出的</span>
          </div>
          <ChevronRightIcon class="w-4 h-4 text-gray-300" />
        </div>
      </div>

      <!-- Logout -->
      <div
        class="bg-white rounded-2xl shadow-sm p-4 flex items-center justify-center cursor-pointer active:scale-95 transition-transform hover:bg-red-50 group"
        @click="handleLogout">
        <ArrowRightOnRectangleIcon class="w-5 h-5 text-red-500 mr-2 group-hover:text-red-600" />
        <span class="text-red-500 font-medium text-sm group-hover:text-red-600">退出登录</span>
      </div>
    </div>
  </div>
</template>
