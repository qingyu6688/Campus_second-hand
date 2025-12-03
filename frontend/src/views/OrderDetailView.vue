<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { 
  ChevronLeftIcon, 
  MapPinIcon,
  ChatBubbleOvalLeftIcon,
  ClipboardDocumentIcon,
  TruckIcon,
  ShieldCheckIcon,
  CurrencyYenIcon,
  CheckCircleIcon,
  ClockIcon
} from '@heroicons/vue/24/outline'
import request from '@/api/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const orderId = route.params.id

const order = ref({})
const loading = ref(true)

const fetchOrderDetail = async () => {
  try {
    const res = await request.get(`/order/detail/${orderId}`)
    order.value = res
  } catch (error) {
    showToast('获取订单详情失败')
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

const getDeliveryText = (type) => {
  switch (type) {
    case 1: return '自提'
    case 2: return '送货上门'
    case 3: return '邮寄'
    default: return '自提'
  }
}

const activeStep = computed(() => {
  if (order.value.status === 2) return 1 // Cancelled
  if (order.value.status === 1) return 3 // Completed
  return 1 // Pending
})

const copyOrderNo = () => {
  navigator.clipboard.writeText(order.value.orderNo).then(() => {
    showToast('复制成功')
  })
}

const handleContact = () => {
  if (order.value.seller) {
    router.push(`/chat/${order.value.seller.id}?goodsId=${order.value.goodsId}`)
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- Header -->
    <div class="bg-white px-4 py-3 flex items-center justify-between shadow-sm sticky top-0 z-10">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-gray-100 cursor-pointer" @click="router.back()">
        <ChevronLeftIcon class="w-6 h-6 text-gray-700" />
      </div>
      <div class="font-bold text-lg text-gray-900">订单详情</div>
      <div class="w-8"></div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <van-loading type="spinner" color="#14b8a6" />
    </div>

    <div v-else class="p-4 space-y-4">
      <!-- Status Card -->
      <div class="bg-gradient-to-r from-teal-500 to-emerald-500 rounded-2xl p-6 text-white shadow-lg shadow-teal-200">
        <div class="flex items-center justify-between mb-4">
          <div>
            <div class="text-2xl font-bold mb-1">{{ getStatusText(order.status) }}</div>
            <div class="text-teal-50 text-sm opacity-90">感谢您使用校园二手交易平台</div>
          </div>
          <div class="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center backdrop-blur-sm">
            <CheckCircleIcon v-if="order.status === 1" class="w-8 h-8 text-white" />
            <ClockIcon v-else class="w-8 h-8 text-white" />
          </div>
        </div>
        
        <!-- Steps (Simplified) -->
        <div class="flex items-center text-xs text-teal-100 mt-4">
          <div class="flex flex-col items-center relative z-10">
            <div class="w-2 h-2 rounded-full bg-white mb-1"></div>
            <span>已下单</span>
          </div>
          <div class="flex-1 h-0.5 bg-white/30 -mt-4 mx-1"></div>
          <div class="flex flex-col items-center relative z-10">
            <div class="w-2 h-2 rounded-full mb-1" :class="activeStep >= 2 ? 'bg-white' : 'bg-white/30'"></div>
            <span :class="activeStep >= 2 ? 'text-white' : 'text-teal-100/50'">交易中</span>
          </div>
          <div class="flex-1 h-0.5 bg-white/30 -mt-4 mx-1"></div>
          <div class="flex flex-col items-center relative z-10">
            <div class="w-2 h-2 rounded-full mb-1" :class="activeStep >= 3 ? 'bg-white' : 'bg-white/30'"></div>
            <span :class="activeStep >= 3 ? 'text-white' : 'text-teal-100/50'">已完成</span>
          </div>
        </div>
      </div>

      <!-- Address Card -->
      <div class="bg-white rounded-2xl p-4 shadow-sm space-y-3">
        <div class="flex items-start space-x-3">
          <div class="w-8 h-8 rounded-full bg-orange-50 flex items-center justify-center flex-shrink-0">
            <MapPinIcon class="w-5 h-5 text-orange-500" />
          </div>
          <div class="flex-1">
            <div class="font-bold text-gray-900 text-sm mb-1">交易地点</div>
            <div class="text-sm text-gray-600">{{ order.goods?.location || '校内' }}</div>
          </div>
        </div>
        <div class="flex items-start space-x-3 pt-3 border-t border-gray-50">
          <div class="w-8 h-8 rounded-full bg-blue-50 flex items-center justify-center flex-shrink-0">
            <TruckIcon class="w-5 h-5 text-blue-500" />
          </div>
          <div class="flex-1">
            <div class="font-bold text-gray-900 text-sm mb-1">交易方式</div>
            <div class="text-sm text-gray-600">{{ getDeliveryText(order.goods?.deliveryType) }}</div>
          </div>
        </div>
      </div>

      <!-- Goods Card -->
      <div class="bg-white rounded-2xl p-4 shadow-sm">
        <div class="flex items-center justify-between mb-4 pb-3 border-b border-gray-50">
          <div class="flex items-center space-x-2">
            <div class="w-8 h-8 rounded-full bg-gray-100 overflow-hidden border border-gray-100">
                <img v-if="order.seller?.avatar" :src="order.seller.avatar" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold">
                    {{ order.seller?.nickname?.charAt(0).toUpperCase() || 'U' }}
                </div>
            </div>
            <div>
              <div class="text-sm font-bold text-gray-700">{{ order.seller?.nickname || '卖家' }}</div>
              <div class="text-[10px] text-gray-400">{{ order.seller?.schoolName || '本校同学' }}</div>
            </div>
          </div>
          <button class="px-3 py-1.5 bg-teal-50 text-teal-600 text-xs font-bold rounded-full flex items-center active:scale-95 transition-transform" @click="handleContact">
            <ChatBubbleOvalLeftIcon class="w-4 h-4 mr-1" />
            联系卖家
          </button>
        </div>

        <div class="flex space-x-3 mb-4" @click="router.push(`/goods/${order.goodsId}`)">
          <div class="w-24 h-24 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0 relative">
            <img v-if="order.goods" :src="getFirstImage(order.goods.images)" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center bg-gray-200 text-gray-400 text-xs">
              已失效
            </div>
            <div class="absolute bottom-0 left-0 w-full bg-black/40 text-white text-[10px] px-1 py-0.5 text-center backdrop-blur-sm">
              共1件
            </div>
          </div>
          <div class="flex-1 flex flex-col justify-between py-0.5">
            <div>
              <h3 class="text-sm font-bold text-gray-900 line-clamp-2 mb-1">{{ order.goods?.title || '商品已删除' }}</h3>
              <div class="flex flex-wrap gap-1">
                <span class="bg-gray-100 text-gray-500 text-[10px] px-1.5 py-0.5 rounded">
                  {{ getDeliveryText(order.goods?.deliveryType) }}
                </span>
              </div>
            </div>
            <div class="flex justify-between items-end">
                <span class="text-xs text-gray-400 line-through" v-if="order.goods?.originalPrice">¥{{ order.goods.originalPrice }}</span>
                <span class="text-lg font-bold text-gray-900">¥{{ order.amount }}</span>
            </div>
          </div>
        </div>

        <!-- Price Breakdown -->
        <div class="space-y-2 pt-3 border-t border-gray-50 text-sm">
          <div class="flex justify-between text-gray-500">
            <span>商品总价</span>
            <span>¥{{ order.amount }}</span>
          </div>
          <div class="flex justify-between text-gray-500">
            <span>运费</span>
            <span>¥0.00</span>
          </div>
          <div class="flex justify-between items-center pt-2">
            <span class="font-bold text-gray-900">实付款</span>
            <span class="text-xl font-bold text-orange-500">¥{{ order.amount }}</span>
          </div>
        </div>
      </div>

      <!-- Order Info -->
      <div class="bg-white rounded-2xl p-4 shadow-sm space-y-3">
        <div class="flex items-center space-x-2 text-sm font-bold text-gray-900 mb-2">
          <div class="w-1 h-4 bg-teal-500 rounded-full"></div>
          <span>订单信息</span>
        </div>
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500">订单编号</span>
          <div class="flex items-center text-gray-900 font-mono text-xs">
            {{ order.orderNo }}
            <button class="ml-2 text-teal-600 text-[10px] border border-teal-200 px-1.5 rounded" @click="copyOrderNo">复制</button>
          </div>
        </div>
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500">创建时间</span>
          <span class="text-gray-900 text-xs">{{ formatTime(order.createTime) }}</span>
        </div>
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500">支付方式</span>
          <span class="text-gray-900 text-xs">在线支付</span>
        </div>
        <div class="flex justify-between items-center text-sm" v-if="order.remark">
          <span class="text-gray-500">备注</span>
          <span class="text-gray-900 text-xs max-w-[60%] truncate">{{ order.remark }}</span>
        </div>
      </div>

      <!-- Safety Tips -->
      <div class="bg-orange-50 rounded-xl p-3 flex items-start space-x-3">
        <ShieldCheckIcon class="w-5 h-5 text-orange-500 flex-shrink-0 mt-0.5" />
        <div class="text-xs text-orange-700 leading-relaxed">
          <span class="font-bold block mb-0.5">安全提醒</span>
          请务必在确认收到商品且无误后再进行确认收货。如遇诈骗等可疑情况，请立即联系平台客服或报警。
        </div>
      </div>
    </div>
  </div>
</template>
