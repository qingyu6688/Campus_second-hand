<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { 
  ChevronRightIcon, 
  ChatBubbleOvalLeftIcon
} from '@heroicons/vue/24/outline'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const orderList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)

const onLoad = async () => {
  if (refreshing.value) {
    orderList.value = []
    refreshing.value = false
  }

  try {
    const res = await request.get('/order/sold', {
      params: {
        page: page.value,
        size: 10
      }
    })

    const records = res.records || []
    if (page.value === 1) {
      orderList.value = records
    } else {
      orderList.value.push(...records)
    }

    page.value++

    if (records.length < 10) {
      finished.value = true
    }
  } catch (error) {
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onRefresh = () => {
  finished.value = false
  loading.value = true
  page.value = 1
  onLoad()
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
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待交易'
    case 1: return '已完成'
    case 2: return '已取消'
    default: return '未知状态'
  }
}

const handleContact = (buyerId, goodsId) => {
  router.push(`/chat/${buyerId}?goodsId=${goodsId}`)
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <van-nav-bar
      title="我卖出的"
      left-arrow
      fixed
      placeholder
      @click-left="router.back()"
      class="z-50"
    />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        :finished-text="orderList.length > 0 ? '没有更多了' : ''"
        @load="onLoad"
        class="p-4 space-y-4 min-h-[calc(100vh-46px)]"
      >
        <div v-for="item in orderList" :key="item.id" 
          class="bg-white rounded-2xl p-4 shadow-sm active:scale-[0.99] transition-transform duration-200"
          @click="router.push(`/order/detail/${item.id}`)"
        >
          <!-- Header -->
          <div class="flex justify-between items-center mb-3 pb-3 border-b border-gray-50">
             <div class="flex items-center space-x-2">
                <div class="w-6 h-6 rounded-full bg-gray-100 overflow-hidden">
                    <img v-if="item.buyer?.avatar" :src="item.buyer.avatar" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold">
                        {{ item.buyer?.nickname?.charAt(0).toUpperCase() || 'U' }}
                    </div>
                </div>
                <span class="text-xs font-bold text-gray-700">{{ item.buyer?.nickname || '买家' }}</span>
                <ChevronRightIcon class="w-3 h-3 text-gray-300" />
             </div>
             <span class="text-xs font-medium" :class="item.status === 1 ? 'text-teal-500' : 'text-gray-500'">
                {{ getStatusText(item.status) }}
             </span>
          </div>

          <!-- Content -->
          <div class="flex space-x-3 mb-3">
            <div class="w-20 h-20 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
              <img v-if="item.goods" :src="getFirstImage(item.goods.images)" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center bg-gray-200 text-gray-400 text-xs">
                已失效
              </div>
            </div>
            <div class="flex-1 flex flex-col justify-between py-0.5">
              <h3 class="text-sm font-bold text-gray-900 line-clamp-2">{{ item.goods?.title || '商品已删除' }}</h3>
              <div class="flex justify-between items-end">
                 <span class="text-xs text-gray-400">{{ formatTime(item.createTime) }}</span>
                 <span class="text-base font-bold text-gray-900">¥{{ item.amount }}</span>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="flex justify-end space-x-2">
             <button class="px-3 py-1.5 rounded-full border border-gray-200 text-xs text-gray-600 font-medium" @click.stop="handleContact(item.buyerId, item.goodsId)">
                联系买家
             </button>
             <button class="px-3 py-1.5 rounded-full border border-gray-200 text-xs text-gray-600 font-medium" @click.stop="router.push(`/order/detail/${item.id}`)">
                查看详情
             </button>
          </div>
        </div>

        <van-empty v-if="finished && orderList.length === 0" description="您还没有卖出商品哦" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>
