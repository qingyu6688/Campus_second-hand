<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast } from 'vant'
import { 
  MapPinIcon, 
  ChevronRightIcon, 
  ShieldCheckIcon,
  DocumentTextIcon,
  CreditCardIcon,
  ChevronLeftIcon,
  ChatBubbleBottomCenterTextIcon
} from '@heroicons/vue/24/outline'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goodsId = route.params.goodsId

const goods = ref({})
const seller = ref({})
const loading = ref(true)
const address = ref('')
const remark = ref('')
const orderNo = ref('')
const createTime = ref('')

onMounted(async () => {
  address.value = userStore.userInfo.address || ''
  generateOrderInfo()
  await fetchGoodsDetail()
})

const generateOrderInfo = () => {
  const now = new Date()
  const timestamp = now.toISOString().replace(/[-:T.]/g, '').slice(0, 14)
  const random = Math.floor(Math.random() * 1000000).toString().padStart(6, '0') // 6 digits random
  orderNo.value = `${timestamp}${random}` // Pure digits
  
  createTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit' // Include seconds
  })
}

const fetchGoodsDetail = async () => {
  try {
    const res = await request.get(`/goods/detail/${goodsId}`)
    goods.value = res.goods
    seller.value = res.seller || {}
  } catch (error) {
    showToast('获取商品信息失败')
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

const handleCreateOrder = async () => {
  if (!address.value) {
    showToast('请填写收货地址')
    return
  }

  try {
    await request.post('/order/create', {
      goodsId: goodsId,
      address: address.value,
      remark: remark.value,
    })
    showSuccessToast('下单成功')
    setTimeout(() => {
      router.replace(`/goods/${goodsId}`)
    }, 1500)
  } catch (error) {
    showToast(error.message || '下单失败')
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#F2F4F7] pb-32 font-sans">
    <!-- Custom Header -->
    <div class="bg-white/80 backdrop-blur-md sticky top-0 z-50 px-4 h-14 flex items-center justify-between shadow-sm">
        <div class="w-10 h-10 flex items-center justify-center rounded-full active:bg-gray-100 cursor-pointer -ml-2 transition-colors" @click="router.back()">
            <ChevronLeftIcon class="w-6 h-6 text-gray-800" />
        </div>
        <div class="font-bold text-lg text-gray-900">确认订单</div>
        <div class="w-10"></div> <!-- Spacer -->
    </div>

    <div v-if="!loading" class="p-4 space-y-5 animate-fade-in">
      
      <!-- Address Section -->
      <div class="bg-white rounded-2xl overflow-hidden shadow-[0_4px_20px_rgba(0,0,0,0.03)] active:scale-[0.99] transition-transform cursor-pointer relative group border border-gray-100" @click="router.push('/address')">
        <div class="p-5 flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-teal-400 to-emerald-500 flex items-center justify-center text-white shadow-lg shadow-teal-200">
              <MapPinIcon class="w-6 h-6" />
            </div>
            <div>
              <div class="flex items-center space-x-2 mb-1">
                 <span class="text-base font-bold text-gray-900">收货地址</span>
                 <span v-if="!address" class="text-xs text-red-500 bg-red-50 px-2 py-0.5 rounded-full">必填</span>
              </div>
              <div class="text-sm text-gray-600 font-medium leading-snug" v-if="address">{{ address }}</div>
              <div class="text-sm text-gray-400" v-else>点击设置收货地址</div>
            </div>
          </div>
          <ChevronRightIcon class="w-5 h-5 text-gray-300 group-hover:text-teal-500 transition-colors" />
        </div>
        <!-- Decorative bottom line -->
        <div class="h-1.5 w-full bg-[repeating-linear-gradient(45deg,#ff6b6b,#ff6b6b_10px,#ffffff_10px,#ffffff_20px,#4ecdc4_20px,#4ecdc4_30px,#ffffff_30px,#ffffff_40px)] opacity-80"></div>
      </div>

      <!-- Goods Card -->
      <div class="bg-white rounded-2xl p-5 shadow-[0_4px_20px_rgba(0,0,0,0.03)] border border-gray-100">
        <!-- Seller Header -->
        <div class="flex items-center space-x-3 mb-4 pb-4 border-b border-gray-50">
            <div class="w-8 h-8 rounded-full bg-gray-100 overflow-hidden ring-2 ring-gray-50">
            <img v-if="seller.avatar" :src="seller.avatar" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs font-bold">
                {{ seller.nickname?.charAt(0).toUpperCase() }}
            </div>
            </div>
            <span class="text-sm font-bold text-gray-800">{{ seller.nickname || '卖家' }}</span>
            <div class="flex-1"></div>
            <span class="text-[10px] text-teal-600 bg-teal-50 px-2 py-1 rounded-full font-medium">信誉极好</span>
        </div>

        <div class="flex space-x-4 mb-5">
          <div class="w-24 h-24 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 shadow-inner relative group">
            <img :src="getFirstImage(goods.images)" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
          </div>
          <div class="flex-1 flex flex-col justify-between py-1">
            <h3 class="text-sm font-bold text-gray-900 line-clamp-2 leading-relaxed">{{ goods.title }}</h3>
            <div class="flex justify-between items-end">
              <div class="flex items-baseline text-red-500">
                  <span class="text-xs font-bold mr-0.5">¥</span>
                  <span class="text-2xl font-bold font-mono">{{ goods.price }}</span>
              </div>
              <span class="text-xs text-gray-400 font-medium bg-gray-50 px-2 py-1 rounded">x1</span>
            </div>
          </div>
        </div>
        
        <!-- Remarks -->
        <div class="bg-gray-50/80 rounded-xl p-3 flex items-center border border-gray-100 focus-within:border-teal-200 focus-within:bg-white transition-all">
          <ChatBubbleBottomCenterTextIcon class="w-5 h-5 text-gray-400 mr-3" />
          <input 
            v-model="remark" 
            type="text" 
            placeholder="订单备注：给卖家留言（选填）" 
            class="bg-transparent text-sm text-gray-900 placeholder-gray-400 flex-1 outline-none"
          />
        </div>
      </div>

      <!-- Order Info -->
      <div class="bg-white rounded-2xl p-5 shadow-[0_4px_20px_rgba(0,0,0,0.03)] border border-gray-100 space-y-4">
        <div class="flex items-center space-x-2 mb-1">
          <DocumentTextIcon class="w-5 h-5 text-teal-600" />
          <span class="text-sm font-bold text-gray-900">订单详情</span>
        </div>
        
        <div class="space-y-3 pl-1">
            <div class="flex justify-between text-xs">
            <span class="text-gray-500">订单编号</span>
            <span class="font-mono text-gray-700 select-all">{{ orderNo }}</span>
            </div>
            <div class="flex justify-between text-xs">
            <span class="text-gray-500">交易时间</span>
            <span class="text-gray-700">{{ createTime }}</span>
            </div>
            <div class="flex justify-between text-xs">
            <span class="text-gray-500">支付方式</span>
            <span class="flex items-center text-gray-700 font-medium">
                <CreditCardIcon class="w-3.5 h-3.5 mr-1.5 text-blue-500" />
                在线支付
            </span>
            </div>
        </div>
      </div>

      <!-- Price Summary -->
      <div class="bg-white rounded-2xl p-5 shadow-[0_4px_20px_rgba(0,0,0,0.03)] border border-gray-100 space-y-3">
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">商品金额</span>
          <span class="text-gray-900 font-medium">¥{{ goods.price }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">运费</span>
          <span class="text-gray-900 font-medium">¥0.00</span>
        </div>
        <div class="border-t border-gray-50 pt-4 mt-2 flex justify-end items-baseline space-x-2">
          <span class="text-sm text-gray-500">实付款:</span>
          <span class="text-2xl font-bold text-red-500 font-mono">¥{{ goods.price }}</span>
        </div>
      </div>

      <!-- Safety Tip -->
      <div class="bg-orange-50/80 border border-orange-100 rounded-2xl p-4 flex items-start space-x-3">
        <ShieldCheckIcon class="w-5 h-5 text-orange-500 flex-shrink-0 mt-0.5" />
        <div class="text-xs text-orange-800 leading-relaxed">
          <span class="font-bold block mb-1 text-orange-900">安全交易提醒</span>
          请在校内公共场所（如食堂、图书馆）进行当面交易。交易前请仔细检查商品。如遇要求私下转账或定金，请务必拒绝并举报。
        </div>
      </div>
    </div>

    <!-- Bottom Bar -->
    <div class="fixed bottom-0 left-0 w-full bg-white/90 backdrop-blur-lg border-t border-gray-100 px-5 py-3 flex items-center justify-between z-50 safe-area-bottom shadow-[0_-4px_20px_rgba(0,0,0,0.05)]">
      <div class="flex flex-col">
        <span class="text-[10px] text-gray-400 mb-0.5">共1件</span>
        <div class="flex items-baseline">
            <span class="text-sm font-bold text-gray-900 mr-1">合计:</span>
            <span class="text-red-500 font-bold text-xl font-mono">¥{{ goods.price }}</span>
        </div>
      </div>
      <button 
        class="bg-gradient-to-r from-teal-500 to-emerald-600 text-white font-bold px-10 py-3 rounded-full shadow-lg shadow-teal-200/50 active:scale-95 transition-all hover:shadow-xl hover:brightness-105"
        @click="handleCreateOrder"
      >
        提交订单
      </button>
    </div>
  </div>
</template>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 20px);
}
.animate-fade-in {
  animation: fadeIn 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
