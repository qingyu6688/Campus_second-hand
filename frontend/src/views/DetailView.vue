<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showImagePreview, showDialog } from 'vant'
import {
  MapPinIcon,
  EyeIcon,
  ClockIcon,
  ShareIcon,
  ChevronLeftIcon,
  HeartIcon,
  EllipsisHorizontalIcon,
  CheckBadgeIcon,
  ChatBubbleOvalLeftIcon,
  PaperAirplaneIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import { HeartIcon as HeartIconSolid } from '@heroicons/vue/24/solid'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goodsId = route.params.id

const goods = ref({})
const seller = ref({})
const images = ref([])
const tags = ref([])
const loading = ref(true)
const isFavorite = ref(false)

// Comment related refs
const comments = ref([])
const commentContent = ref('')
const submittingComment = ref(false)
const commentSectionRef = ref(null)
const replyToUser = ref(null) // { id, nickname, parentId }

const isOwnGoods = computed(() => {
  return goods.value.sellerId === userStore.userInfo.id
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours().toString().padStart(2, '0')
  const minute = date.getMinutes().toString().padStart(2, '0')
  return `${month}月${day}日 ${hour}:${minute}`
}

const fetchDetail = async () => {
  try {
    const res = await request.get(`/goods/detail/${goodsId}`)
    goods.value = res.goods
    seller.value = res.seller || {}

    // Parse images
    if (res.goods.images) {
      try {
        images.value = JSON.parse(res.goods.images)
      } catch (e) {
        images.value = []
      }
    }

    // Parse tags
    if (res.goods.tags) {
      try {
        tags.value = JSON.parse(res.goods.tags)
      } catch (e) {
        tags.value = []
      }
    }
  } catch (error) {
    showToast('获取详情失败')
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await request.get(`/comment/list/${goodsId}`)
    comments.value = res
  } catch (error) {
    console.error('Fetch comments failed', error)
  }
}

const handleReply = (comment, rootId = null) => {
  if (!userStore.isLoggedIn) {
     showToast('请先登录')
     router.push('/login')
     return
  }
  
  // Determine parentId
  // If rootId is provided, use it (replying to a child)
  // If rootId is null, then comment is the root (replying to a root)
  const parentId = rootId || comment.id
  
  replyToUser.value = {
    id: comment.userId,
    nickname: comment.userNickname,
    parentId: parentId
  }
  // Focus input (optional: scroll to input)
}

const cancelReply = () => {
  replyToUser.value = null
  commentContent.value = ''
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  if (!userStore.isLoggedIn) {
     showToast('请先登录')
     router.push('/login')
     return
  }
  
  submittingComment.value = true
  try {
    const payload = {
      goodsId: goodsId,
      content: commentContent.value
    }
    
    if (replyToUser.value) {
      payload.replyUserId = replyToUser.value.id
      payload.parentId = replyToUser.value.parentId
    }

    await request.post('/comment/add', payload)
    showToast('留言成功')
    commentContent.value = ''
    replyToUser.value = null
    fetchComments() // Refresh list
  } catch (error) {
    showToast('留言失败')
  } finally {
    submittingComment.value = false
  }
}

const handlePreview = (index) => {
  showImagePreview({
    images: images.value,
    startPosition: index,
  })
}

const handleContact = () => {
  // Scroll to comment section
  if (commentSectionRef.value) {
    commentSectionRef.value.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleBuy = () => {
  if (isOwnGoods.value) {
    showToast('不能购买自己的商品')
    return
  }
  if (goods.value.status !== 0) {
    showToast('商品已售出或下架')
    return
  }
  router.push(`/order/create/${goodsId}`)
}

const checkFavorite = async () => {
  try {
    const res = await request.get(`/favorite/check/${goodsId}`)
    isFavorite.value = res
  } catch (error) {
    console.error('Check favorite failed', error)
  }
}

const toggleFavorite = async () => {
  try {
    const res = await request.post('/favorite/toggle', {
      goodsId: goodsId
    })
    isFavorite.value = res
    showToast(isFavorite.value ? '已收藏' : '已取消收藏')
  } catch (error) {
    showToast('操作失败，请先登录')
  }
}

const goToChat = () => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  if (isOwnGoods.value) {
    showToast('不能和自己私聊')
    return
  }
  router.push({
    path: `/chat/${seller.value.id}`,
    query: { goodsId: goodsId }
  })
}

onMounted(() => {
  fetchDetail()
  checkFavorite()
  fetchComments()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-24 relative">
    <!-- Floating Header -->
    <div class="fixed top-0 left-0 w-full z-50 px-4 py-3 flex justify-between items-center pointer-events-none">
      <div
        class="w-8 h-8 bg-black/30 backdrop-blur-md rounded-full flex items-center justify-center cursor-pointer pointer-events-auto"
        @click="router.back()">
        <ChevronLeftIcon class="w-5 h-5 text-white" />
      </div>
      <div class="flex space-x-3 pointer-events-auto">
        <div class="w-8 h-8 bg-black/30 backdrop-blur-md rounded-full flex items-center justify-center cursor-pointer"
          @click="toggleFavorite">
          <component :is="isFavorite ? HeartIconSolid : HeartIcon" class="w-5 h-5"
            :class="isFavorite ? 'text-red-500' : 'text-white'" />
        </div>
        <div class="w-8 h-8 bg-black/30 backdrop-blur-md rounded-full flex items-center justify-center cursor-pointer">
          <EllipsisHorizontalIcon class="w-5 h-5 text-white" />
        </div>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <van-loading type="spinner" color="#14b8a6" />
    </div>

    <template v-else>
      <!-- Image Swipe -->
      <div class="relative">
        <van-swipe :autoplay="0" indicator-color="white" class="h-[50vh] bg-gray-200">
          <van-swipe-item v-for="(img, index) in images" :key="index" @click="handlePreview(index)">
            <img :src="img" class="w-full h-full object-cover" />
          </van-swipe-item>
          <template #indicator="{ active, total }">
            <div
              class="absolute bottom-6 right-4 bg-black/30 backdrop-blur-sm text-white px-3 py-1 rounded-full text-xs font-medium">
              {{ active + 1 }} / {{ total }}
            </div>
          </template>
        </van-swipe>
      </div>

      <!-- Main Content Container -->
      <div
        class="relative -mt-6 bg-gray-50 rounded-t-3xl overflow-hidden min-h-[50vh] shadow-[0_-4px_20px_rgba(0,0,0,0.05)]">

        <!-- Price & Title Section -->
        <div class="bg-white p-5 mb-3">
          <div class="flex items-baseline mb-3">
            <span class="text-orange-500 text-sm font-bold mr-0.5">¥</span>
            <span class="text-orange-500 text-3xl font-bold mr-3">{{ goods.price }}</span>
            <span class="text-gray-400 text-sm line-through" v-if="goods.originalPrice">原价¥{{ goods.originalPrice
            }}</span>
          </div>

          <h1 class="text-xl font-bold text-gray-900 leading-snug mb-3">{{ goods.title }}</h1>

          <div class="flex flex-wrap gap-2">
            <span v-for="tag in tags" :key="tag"
              class="bg-teal-50 text-teal-600 text-xs px-2 py-1 rounded-md font-medium">
              #{{ tag }}
            </span>
          </div>
        </div>

        <!-- Seller Card -->
        <div class="bg-white p-4 mb-3 mx-3 rounded-xl shadow-sm flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 rounded-full bg-gray-100 overflow-hidden border border-gray-100 cursor-pointer" @click="goToChat">
              <img v-if="seller.avatar" :src="seller.avatar" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400 font-bold text-lg">
                {{ seller.nickname?.charAt(0).toUpperCase() || 'U' }}
              </div>
            </div>
            <div>
              <div class="flex items-center">
                <div class="font-bold text-gray-900 text-sm mr-1">{{ seller.nickname || seller.username }}</div>
                <!-- Verification Badge (Mock) -->
                <CheckBadgeIcon class="w-4 h-4 text-blue-500" />
              </div>
              <div class="text-xs text-gray-400 mt-0.5">
                {{ seller.schoolName || '未认证学校' }} · 信誉极好
              </div>
            </div>
          </div>
          <button
            class="text-teal-600 text-xs font-medium border border-teal-600 px-4 py-1.5 rounded-full hover:bg-teal-50 transition-colors"
            @click="router.push(`/user/${seller.id}`)">
            主页
          </button>
        </div>

        <!-- Description -->
        <div class="bg-white p-5 mb-3">
          <h3 class="font-bold text-gray-900 mb-3 text-base">宝贝描述</h3>
          <p class="text-gray-700 text-sm leading-relaxed whitespace-pre-wrap mb-6">{{ goods.description }}</p>

          <div class="flex items-center text-xs text-gray-400 bg-gray-50 w-fit px-3 py-1.5 rounded-lg">
            <MapPinIcon class="w-3.5 h-3.5 mr-1.5" />
            发布于：{{ goods.location || '校内' }}
          </div>
        </div>

        <!-- Comments Section -->
        <div class="bg-white p-5 mb-24" ref="commentSectionRef">
          <h3 class="font-bold text-gray-900 mb-4 text-base">全部留言 ({{ comments.length }})</h3>
          
          <!-- Comment List -->
          <div class="space-y-6 mb-6">
            <div v-if="comments.length === 0" class="text-center text-gray-400 py-8">
              <div class="bg-gray-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-3">
                <ChatBubbleOvalLeftIcon class="w-8 h-8 text-gray-300" />
              </div>
              <p class="text-sm">暂无留言，快来抢沙发吧~</p>
            </div>
            
            <div v-for="comment in comments" :key="comment.id" class="flex flex-col gap-3">
              <!-- Root Comment Item -->
              <div class="flex gap-3">
                 <!-- Avatar -->
                 <div class="w-9 h-9 rounded-full bg-gray-100 flex-shrink-0 overflow-hidden border border-white shadow-sm">
                    <img v-if="comment.userAvatar" :src="comment.userAvatar" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold">
                      {{ comment.userNickname?.charAt(0).toUpperCase() }}
                    </div>
                 </div>
                 <div class="flex-1">
                    <!-- Name & Time Row -->
                    <div class="flex items-center justify-between">
                       <span class="text-sm font-bold text-gray-800">{{ comment.userNickname }}</span>
                       <span class="text-xs text-gray-400">{{ formatDate(comment.createTime) }}</span>
                    </div>
                    <!-- Content -->
                    <p class="text-sm text-gray-700 mt-1 leading-relaxed cursor-pointer" @click="handleReply(comment)">
                       {{ comment.content }}
                    </p>
                    <!-- Actions Row -->
                    <div class="flex items-center gap-4 mt-2">
                       <button class="text-xs text-gray-400 font-medium hover:text-teal-600 transition-colors flex items-center gap-1" @click.stop="handleReply(comment)">
                          <ChatBubbleOvalLeftIcon class="w-3.5 h-3.5" />
                          回复
                       </button>
                    </div>

                    <!-- Child Comments Block -->
                    <div v-if="comment.children && comment.children.length > 0" class="mt-3 bg-gray-50 rounded-xl p-3 space-y-3">
                       <div v-for="child in comment.children" :key="child.id" class="flex gap-2.5">
                          <!-- Child Avatar -->
                          <div class="w-6 h-6 rounded-full bg-gray-200 flex-shrink-0 overflow-hidden mt-0.5">
                             <img v-if="child.userAvatar" :src="child.userAvatar" class="w-full h-full object-cover" />
                             <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-[10px] font-bold">
                               {{ child.userNickname?.charAt(0).toUpperCase() }}
                             </div>
                          </div>
                          <div class="flex-1">
                             <div class="flex items-baseline justify-between">
                                <span class="text-xs font-bold text-gray-700">{{ child.userNickname }}</span>
                                <span class="text-[10px] text-gray-400">{{ formatDate(child.createTime) }}</span>
                             </div>
                             <p class="text-xs text-gray-600 mt-0.5 leading-relaxed cursor-pointer" @click="handleReply(child, comment.id)">
                                <span v-if="child.replyUserNickname && child.replyUserId !== comment.userId" class="text-teal-600 font-medium">回复 {{ child.replyUserNickname }} : </span>
                                {{ child.content }}
                             </p>
                             <!-- Child Actions -->
                             <div class="mt-1.5">
                                <button class="text-[10px] text-gray-400 font-medium hover:text-teal-600 transition-colors" @click.stop="handleReply(child, comment.id)">
                                   回复
                                </button>
                             </div>
                          </div>
                       </div>
                    </div>
                 </div>
              </div>
            </div>
          </div>

          <!-- Comment Input -->
          <div class="mt-6 flex items-end gap-2 bg-gray-50 p-2 rounded-2xl border border-gray-100 focus-within:border-teal-200 focus-within:ring-2 focus-within:ring-teal-50 transition-all">
             <div class="flex-1">
                <div v-if="replyToUser" class="flex items-center justify-between px-2 py-1 mb-1 bg-white rounded-lg border border-gray-100 shadow-sm">
                   <span class="text-xs text-teal-600 font-medium">回复 {{ replyToUser.nickname }}</span>
                   <button class="text-xs text-gray-400 hover:text-gray-600" @click="cancelReply">
                      <XMarkIcon class="w-3.5 h-3.5" />
                   </button>
                </div>
                <input 
                   v-model="commentContent"
                   type="text"
                   class="w-full bg-transparent border-none outline-none text-sm text-gray-700 placeholder-gray-400 px-2 py-1.5"
                   :placeholder="replyToUser ? '写下你的回复...' : '看对眼了？问问细节...'"
                   @keyup.enter="submitComment"
                />
             </div>
             <button 
                class="p-2.5 rounded-xl transition-all shadow-sm flex-shrink-0 mb-0.5"
                :class="commentContent.trim() ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-white shadow-teal-200' : 'bg-gray-200 text-gray-400'"
                :disabled="!commentContent.trim() || submittingComment"
                @click="submitComment"
             >
                <PaperAirplaneIcon class="w-4 h-4" />
             </button>
          </div>
        </div>
      </div>

      <!-- Bottom Action Bar -->
      <div
        class="fixed bottom-0 left-0 w-full bg-white border-t border-gray-100 px-4 py-3 flex items-center justify-between z-50 safe-area-bottom">
        <div class="flex items-center space-x-6 px-2">
          <div class="flex flex-col items-center justify-center text-gray-600 cursor-pointer" @click="handleContact">
            <ChatBubbleOvalLeftIcon class="w-6 h-6 mb-0.5" />
            <span class="text-[10px]">留言</span>
          </div>
          <div class="flex flex-col items-center justify-center text-gray-600 cursor-pointer" @click="toggleFavorite">
            <component :is="isFavorite ? HeartIconSolid : HeartIcon" class="w-6 h-6 mb-0.5"
              :class="isFavorite ? 'text-red-500' : 'text-gray-600'" />
            <span class="text-[10px]">收藏</span>
          </div>
        </div>
        <button
          class="flex-1 ml-6 font-bold text-base py-2.5 rounded-full shadow-lg transition-transform"
          :class="(goods.status === 0 && !isOwnGoods) ? 'bg-teal-500 text-white shadow-teal-200 active:scale-95' : 'bg-gray-300 text-gray-500 cursor-not-allowed shadow-none'"
          :disabled="goods.status !== 0 || isOwnGoods"
          @click="handleBuy">
          {{ isOwnGoods ? '我发布的' : (goods.status === 0 ? '我想要' : (goods.status === 2 ? '已卖出' : '已下架')) }}
        </button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 20px);
}
</style>
