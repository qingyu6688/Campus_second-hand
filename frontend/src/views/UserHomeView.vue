<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { 
  AcademicCapIcon, 
  ChevronLeftIcon,
  ClockIcon,
  ChatBubbleLeftRightIcon
} from '@heroicons/vue/24/outline'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userId = route.params.id

const userInfo = ref({})
const stats = ref({
  publishedCount: 0,
  soldCount: 0
})
const goodsList = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

const isOwnProfile = computed(() => {
  return userStore.userInfo && String(userStore.userInfo.id) === String(userId)
})

const handleChat = () => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  router.push(`/chat/${userId}`)
}

onMounted(async () => {
  await fetchUserInfo()
})

const fetchUserInfo = async () => {
  try {
    const res = await request.get(`/user/info/${userId}`)
    userInfo.value = res.user
    stats.value = res.stats
  } catch (error) {
    showToast('获取用户信息失败')
  }
}

const onLoad = async () => {
  try {
    const res = await request.get('/goods/list', {
      params: {
        page: page.value,
        size: 10,
        sellerId: userId
      }
    })

    const records = res.records || []
    if (page.value === 1) {
      goodsList.value = records
    } else {
      goodsList.value.push(...records)
    }

    page.value++

    if (records.length < 10) {
      finished.value = true
    }
  } catch (error) {
    showToast('加载商品失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const getFirstImage = (imagesStr) => {
  try {
    const images = JSON.parse(imagesStr)
    return images && images.length > 0 ? images[0] : ''
  } catch (e) {
    return ''
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- Header -->
    <div class="bg-gradient-to-br from-teal-500 to-emerald-600 pt-12 pb-24 px-6 rounded-b-[2.5rem] shadow-lg relative overflow-hidden">
       <!-- Decorative circles -->
       <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
       <div class="absolute bottom-0 left-0 w-48 h-48 bg-black/5 rounded-full -ml-10 -mb-10 blur-2xl"></div>

       <div class="relative z-10 mb-6 flex justify-between items-center">
         <div class="w-10 h-10 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md cursor-pointer active:scale-95 transition-transform hover:bg-white/30" @click="router.back()">
           <ChevronLeftIcon class="w-6 h-6 text-white" />
         </div>
         
         <div v-if="!isOwnProfile" 
              class="px-4 py-2 bg-white/20 backdrop-blur-md rounded-full flex items-center space-x-2 cursor-pointer active:scale-95 transition-transform hover:bg-white/30 text-white font-medium text-sm border border-white/20"
              @click="handleChat">
           <ChatBubbleLeftRightIcon class="w-4 h-4" />
           <span>私聊</span>
         </div>
       </div>

       <div class="flex items-center space-x-5 text-white relative z-10">
         <div class="w-20 h-20 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner">
           <div class="w-full h-full rounded-full bg-white overflow-hidden">
             <img v-if="userInfo.avatar" :src="userInfo.avatar" class="w-full h-full object-cover" />
             <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400 text-2xl font-bold">
               {{ userInfo.nickname?.charAt(0).toUpperCase() || 'U' }}
             </div>
           </div>
         </div>
         
         <div class="flex-1">
           <h2 class="text-2xl font-bold tracking-tight">{{ userInfo.nickname || '用户' }}</h2>
           <div class="flex items-center text-teal-50 text-xs mt-2 bg-white/10 w-fit px-3 py-1 rounded-full backdrop-blur-md border border-white/10">
             <AcademicCapIcon class="w-3.5 h-3.5 mr-1.5" />
             <span>{{ userInfo.schoolName || '未认证学校' }}</span>
           </div>
         </div>
       </div>
    </div>

    <!-- Stats Card -->
    <div class="mx-5 -mt-12 bg-white rounded-2xl shadow-lg shadow-gray-200/50 p-5 flex justify-around items-center relative z-10 mb-6">
      <div class="flex flex-col items-center">
        <span class="font-bold text-xl text-gray-800">{{ stats.publishedCount }}</span>
        <span class="text-xs text-gray-400 mt-1">在售商品</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center">
        <span class="font-bold text-xl text-gray-800">{{ stats.soldCount }}</span>
        <span class="text-xs text-gray-400 mt-1">已卖出</span>
      </div>
    </div>

    <!-- Goods List -->
    <div class="px-4">
      <h3 class="font-bold text-lg text-gray-800 mb-4 ml-1">发布的商品</h3>
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        class="space-y-4"
      >
        <template #empty>
            <van-empty description="该用户暂时没有发布商品" />
        </template>

        <div v-for="item in goodsList" :key="item.id" 
          class="bg-white rounded-2xl p-3 shadow-sm flex space-x-3 active:scale-[0.99] transition-transform duration-200"
          @click="router.push(`/goods/${item.id}`)"
        >
          <!-- Image -->
          <div class="w-28 h-28 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 relative">
            <img :src="getFirstImage(item.images)" class="w-full h-full object-cover" />
          </div>

          <!-- Content -->
          <div class="flex-1 flex flex-col justify-between py-1">
            <div>
              <h3 class="text-sm font-bold text-gray-900 line-clamp-2 leading-snug">{{ item.title }}</h3>
              <div class="mt-2 flex items-baseline space-x-2">
                <span class="text-red-500 font-bold text-lg">¥{{ item.price }}</span>
                <span class="text-gray-400 text-xs line-through" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
              </div>
            </div>
            
            <div class="flex items-center text-xs text-gray-400">
              <ClockIcon class="w-3 h-3 mr-1" />
              {{ formatTime(item.createTime) }} · {{ item.viewCount || 0 }}浏览
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>
