<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showImagePreview } from 'vant'
import { 
  ChevronLeftIcon, 
  PaperAirplaneIcon, 
  PhotoIcon 
} from '@heroicons/vue/24/outline'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const targetUserId = route.params.id

const targetUser = ref({})
const messages = ref([])
const content = ref('')
const loading = ref(true)
const sending = ref(false)
const messageContainer = ref(null)
let pollingInterval = null

const goodsId = route.query.goodsId
const goodsInfo = ref(null)

// Fetch target user info
const fetchTargetUser = async () => {
  try {
    const res = await request.get(`/user/info/${targetUserId}`)
    targetUser.value = res.user
  } catch (error) {
    showToast('获取用户信息失败')
  }
}

const fetchGoodsInfo = async () => {
  if (!goodsId) return
  try {
    const res = await request.get(`/goods/detail/${goodsId}`)
    const goods = res.goods
    let image = ''
    if (goods.images) {
      try {
        const images = JSON.parse(goods.images)
        if (images.length > 0) image = images[0]
      } catch (e) {}
    }
    goodsInfo.value = {
      id: goods.id,
      title: goods.title,
      price: goods.price,
      image: image
    }
  } catch (error) {
    console.error('Fetch goods info failed', error)
  }
}

// Fetch chat history
const fetchHistory = async () => {
  try {
    const res = await request.get(`/chat/history/${targetUserId}`)
    const isAtBottom = isScrollAtBottom()
    messages.value = res
    
    // If was at bottom or first load, scroll to bottom
    if (loading.value || isAtBottom) {
      scrollToBottom()
    }
  } catch (error) {
    console.error('Fetch history failed', error)
  } finally {
    loading.value = false
  }
}

// Mark messages as read
const markAsRead = async () => {
  try {
    await request.post(`/chat/read/${targetUserId}`)
  } catch (error) {
    console.error('Mark read failed', error)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

const isScrollAtBottom = () => {
  if (!messageContainer.value) return true
  const { scrollTop, scrollHeight, clientHeight } = messageContainer.value
  return scrollHeight - scrollTop - clientHeight < 50
}

const sendMessage = async (type = 0, msgContent = content.value) => {
  if (!msgContent.trim() && type === 0) return
  
  sending.value = true
  try {
    await request.post('/chat/send', {
      receiverId: targetUserId,
      content: msgContent,
      msgType: type
    })
    content.value = ''
    await fetchHistory()
    scrollToBottom()
  } catch (error) {
    showToast('发送失败')
  } finally {
    sending.value = false
  }
}

const afterRead = async (file) => {
  file.status = 'uploading'
  file.message = '上传中...'

  try {
    const formData = new FormData()
    formData.append('file', file.file)

    const res = await request.post('/common/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    // Send image message
    await sendMessage(1, res)
  } catch (error) {
    showToast('图片上传失败')
  }
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

const previewImage = (url) => {
  showImagePreview([url])
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  
  await fetchTargetUser()
  await fetchGoodsInfo()
  await fetchHistory()
  await markAsRead()
  
  // Polling every 3 seconds
  pollingInterval = setInterval(fetchHistory, 3000)
})

onUnmounted(() => {
  if (pollingInterval) clearInterval(pollingInterval)
})

const handleBack = () => {
  // If entered from product detail (has goodsId), go to message list
  if (route.query.goodsId) {
    router.push('/message')
  } else {
    router.back()
  }
}
</script>

<template>
  <div class="flex flex-col h-screen bg-[#f8fafc]">
    <!-- Header -->
    <div class="bg-white/80 backdrop-blur-xl px-4 py-3 flex items-center justify-between shadow-sm border-b border-gray-100 sticky top-0 z-20">
      <div class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100/50 active:bg-gray-100 transition-colors cursor-pointer -ml-2" @click="handleBack">
        <ChevronLeftIcon class="w-6 h-6 text-gray-700" />
      </div>
      <div class="flex flex-col items-center">
        <div class="font-bold text-lg text-gray-900 tracking-tight">
          {{ targetUser.nickname || targetUser.username || '聊天' }}
        </div>
        <div class="flex items-center space-x-1">
          <div class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></div>
          <span class="text-[10px] text-gray-400 font-medium">在线</span>
        </div>
      </div>
      <div class="w-8"></div> <!-- Placeholder for balance -->
    </div>

    <!-- Goods Info Card (Floating) -->
    <div v-if="goodsInfo" class="px-4 py-2 z-10">
      <div class="bg-white/90 backdrop-blur-md p-3 rounded-2xl shadow-sm border border-white/50 flex items-center space-x-3">
        <div class="w-12 h-12 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 border border-gray-100">
          <img :src="goodsInfo.image" class="w-full h-full object-cover" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex justify-between items-start">
            <h3 class="text-sm font-bold text-gray-900 truncate pr-2">{{ goodsInfo.title }}</h3>
            <span class="text-orange-500 font-bold text-sm flex-shrink-0 font-mono">¥{{ goodsInfo.price }}</span>
          </div>
          <div class="text-xs text-gray-400 mt-0.5 truncate flex items-center">
            <span class="bg-gray-100 text-gray-500 px-1.5 py-0.5 rounded text-[10px] mr-1">交易前必读</span>
            请仔细核实商品信息
          </div>
        </div>
        <button 
          class="px-3 py-1.5 bg-teal-50 text-teal-600 text-xs font-bold rounded-full active:scale-95 transition-transform border border-teal-100" 
          @click="router.push(`/goods/${goodsInfo.id}`)">
          查看
        </button>
      </div>
    </div>

    <!-- Message List -->
    <div class="flex-1 overflow-y-auto p-4 space-y-6" ref="messageContainer">
      <div v-for="(msg, index) in messages" :key="msg.id">
        <!-- Time Divider (Optional logic could be added to show only when time gap is large) -->
        <div v-if="index === 0 || new Date(msg.createTime) - new Date(messages[index-1].createTime) > 300000" class="flex justify-center mb-4">
          <span class="text-[10px] text-gray-400 bg-gray-100/80 px-2 py-0.5 rounded-full">{{ formatTime(msg.createTime) }}</span>
        </div>

        <div class="flex w-full" :class="msg.senderId === userStore.userInfo.id ? 'justify-end' : 'justify-start'">
          <!-- Avatar (Left) -->
          <div v-if="msg.senderId !== userStore.userInfo.id" class="w-9 h-9 rounded-full bg-white border border-gray-100 overflow-hidden mr-2 flex-shrink-0 shadow-sm self-end mb-1">
            <img v-if="targetUser.avatar" :src="targetUser.avatar" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold bg-gray-50">
              {{ targetUser.nickname?.charAt(0).toUpperCase() }}
            </div>
          </div>

          <!-- Message Bubble -->
          <div class="max-w-[75%] flex flex-col" :class="msg.senderId === userStore.userInfo.id ? 'items-end' : 'items-start'">
            <div 
              class="px-4 py-2.5 text-[15px] shadow-sm break-words relative group transition-all duration-200"
              :class="[
                msg.senderId === userStore.userInfo.id 
                  ? 'bg-gradient-to-br from-teal-500 to-emerald-600 text-white rounded-2xl rounded-tr-sm shadow-teal-500/10' 
                  : 'bg-white text-gray-800 rounded-2xl rounded-tl-sm border border-gray-100'
              ]"
            >
              <template v-if="msg.msgType === 1">
                <img :src="msg.content" class="max-w-full rounded-lg cursor-pointer hover:opacity-95 transition-opacity" @click="previewImage(msg.content)" />
              </template>
              <template v-else>
                {{ msg.content }}
              </template>
            </div>
            <!-- Read Status / Time (Small) -->
            <!-- <div class="text-[10px] text-gray-300 mt-1 px-1 opacity-0 group-hover:opacity-100 transition-opacity">
              {{ formatTime(msg.createTime) }}
            </div> -->
          </div>

          <!-- Avatar (Right) -->
          <div v-if="msg.senderId === userStore.userInfo.id" class="w-9 h-9 rounded-full bg-gray-100 overflow-hidden ml-2 flex-shrink-0 border border-white shadow-sm self-end mb-1">
            <img v-if="userStore.userInfo.avatar" :src="userStore.userInfo.avatar" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold">
              {{ userStore.userInfo.nickname?.charAt(0).toUpperCase() }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Input Area -->
    <div class="bg-white/80 backdrop-blur-xl px-4 py-3 border-t border-gray-100 flex items-end space-x-3 safe-area-bottom z-20">
      <van-uploader :after-read="afterRead">
        <div class="p-2.5 rounded-full bg-gray-100 text-gray-500 hover:bg-teal-50 hover:text-teal-600 transition-colors cursor-pointer active:scale-95">
          <PhotoIcon class="w-6 h-6" />
        </div>
      </van-uploader>
      
      <div class="flex-1 bg-gray-100/80 rounded-[1.25rem] px-4 py-2.5 focus-within:bg-white focus-within:ring-2 focus-within:ring-teal-100 focus-within:border-teal-200 border border-transparent transition-all">
        <textarea 
          v-model="content"
          rows="1"
          placeholder="发消息..." 
          class="w-full bg-transparent border-none outline-none text-[15px] text-gray-900 placeholder-gray-400 resize-none max-h-24 leading-relaxed"
          @keydown.enter.prevent="sendMessage(0)"
        ></textarea>
      </div>
      
      <button 
        class="p-2.5 rounded-full transition-all shadow-md mb-0.5 active:scale-90"
        :class="content.trim() ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-white shadow-teal-200' : 'bg-gray-200 text-gray-400 shadow-none'"
        :disabled="!content.trim() || sending"
        @click="sendMessage(0)"
      >
        <PaperAirplaneIcon class="w-5 h-5 transform -rotate-45 translate-x-0.5 translate-y-0.5" />
      </button>
    </div>
  </div>
</template>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 20px);
}

/* Custom Scrollbar for Webkit */
::-webkit-scrollbar {
  width: 4px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
