<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { 
  ChatBubbleOvalLeftEllipsisIcon 
} from '@heroicons/vue/24/outline'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const chatList = ref([])
const loading = ref(true)

const fetchChatList = async () => {
  try {
    const res = await request.get('/chat/list')
    chatList.value = res
  } catch (error) {
    showToast('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  
  // If today
  if (date.toDateString() === now.toDateString()) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  }
  
  // If this year
  if (date.getFullYear() === now.getFullYear()) {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
  
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
}

const handleChat = (targetUserId) => {
  router.push(`/chat/${targetUserId}`)
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  fetchChatList()
})
</script>

<template>
  <div class="min-h-screen bg-[#f8fafc] pb-20">
    <!-- Header -->
    <div class="bg-white/80 backdrop-blur-xl px-4 py-4 flex items-center justify-center border-b border-gray-100 sticky top-0 z-10">
      <div class="font-bold text-lg text-gray-900 tracking-tight">消息中心</div>
    </div>

    <!-- List -->
    <div v-if="loading" class="flex justify-center py-20">
      <van-loading type="spinner" color="#14b8a6" />
    </div>

    <div v-else class="px-4 py-4 space-y-3">
      <div v-if="chatList.length === 0" class="flex flex-col items-center justify-center py-32 text-gray-400">
        <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <ChatBubbleOvalLeftEllipsisIcon class="w-10 h-10 text-gray-300" />
        </div>
        <p class="text-sm font-medium text-gray-500">暂无消息</p>
        <p class="text-xs text-gray-400 mt-1">快去和卖家聊聊吧</p>
      </div>

      <div 
        v-for="chat in chatList" 
        :key="chat.targetUserId"
        class="bg-white rounded-2xl p-4 flex items-center space-x-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 hover:shadow-md cursor-pointer group"
        @click="handleChat(chat.targetUserId)"
      >
        <!-- Avatar -->
        <div class="relative">
          <div class="w-14 h-14 rounded-full bg-gray-50 overflow-hidden border border-gray-100 group-hover:border-teal-100 transition-colors">
            <img v-if="chat.targetUserAvatar" :src="chat.targetUserAvatar" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-300 text-xl font-bold bg-gray-50">
              {{ chat.targetUserNickname?.charAt(0).toUpperCase() }}
            </div>
          </div>
          <!-- Badge -->
          <div v-if="chat.unreadCount > 0" class="absolute -top-0.5 -right-0.5 bg-gradient-to-r from-red-500 to-pink-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[18px] text-center border-2 border-white shadow-sm">
            {{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 min-w-0 py-1">
          <div class="flex justify-between items-baseline mb-1.5">
            <h3 class="font-bold text-gray-900 text-[15px] truncate group-hover:text-teal-600 transition-colors">{{ chat.targetUserNickname }}</h3>
            <span class="text-xs text-gray-400 font-medium flex-shrink-0">{{ formatTime(chat.lastTime) }}</span>
          </div>
          <p class="text-sm text-gray-500 truncate leading-relaxed">
            <span v-if="chat.msgType === 1" class="text-teal-600 font-medium flex items-center">
              <span class="mr-1">📷</span> [图片]
            </span>
            <span v-else class="group-hover:text-gray-600 transition-colors">{{ chat.lastContent }}</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
