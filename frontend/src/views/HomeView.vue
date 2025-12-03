<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { MapPinIcon, FunnelIcon, BookOpenIcon, ComputerDesktopIcon, TruckIcon, HomeModernIcon, SparklesIcon, TrophyIcon } from '@heroicons/vue/24/outline'
import request from '@/api/request'

const router = useRouter()

const searchValue = ref('')
const activeCategory = ref(0)
const goodsList = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const categories = ref([])

// Icon mapping
const iconMap = {
  'book': BookOpenIcon,
  'device-tablet': ComputerDesktopIcon,
  'bicycle': TruckIcon,
  'coffee': HomeModernIcon,
  'sparkles': SparklesIcon,
  'trophy': TrophyIcon
}

// Color mapping (cycling)
const colors = [
  'bg-blue-100 text-blue-600',
  'bg-green-100 text-green-600',
  'bg-orange-100 text-orange-600',
  'bg-pink-100 text-pink-600',
  'bg-purple-100 text-purple-600',
  'bg-yellow-100 text-yellow-600'
]

onMounted(async () => {
  // Fetch categories
  try {
    const res = await request.get('/category/list')
    categories.value = res.map((cat, index) => ({
      ...cat,
      icon: iconMap[cat.icon] || BookOpenIcon,
      color: colors[index % colors.length]
    }))
  } catch (error) {
    console.error('Fetch categories failed', error)
  }
})

const onLoad = async () => {
  try {
    const res = await request.get('/goods/list', {
      params: {
        page: page.value,
        size: 10,
        keyword: searchValue.value,
        categoryId: activeCategory.value || undefined
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
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  page.value = 1
  finished.value = false
  loading.value = true
  onLoad()
}

const selectCategory = (id) => {
  if (activeCategory.value === id) {
    activeCategory.value = 0
  } else {
    activeCategory.value = id
  }
  onSearch()
}

const getFirstImage = (imagesStr) => {
  try {
    const images = JSON.parse(imagesStr)
    return images && images.length > 0 ? images[0] : ''
  } catch (e) {
    return ''
  }
}

const getTags = (tagsStr) => {
  try {
    return JSON.parse(tagsStr) || []
  } catch (e) {
    return []
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- Header -->
    <div class="sticky top-0 z-40 bg-white px-4 py-2 shadow-sm flex items-center space-x-3">
      <div class="flex items-center space-x-1">
        <img src="/logo.png" alt="Logo" class="w-8 h-8 object-contain" />
        <span class="text-lg font-bold text-teal-600">校易</span>
      </div>
      <div class="flex-1">
        <van-search v-model="searchValue" shape="round" background="transparent" placeholder="搜索教材、宿舍神器..."
          @search="onSearch" class="p-0" />
      </div>
      <MapPinIcon class="w-6 h-6 text-gray-600" />
    </div>

    <!-- Banner -->
    <div class="p-4">
      <div
        class="bg-gradient-to-r from-teal-400 to-teal-200 rounded-2xl p-6 text-white shadow-lg relative overflow-hidden">
        <div class="relative z-10">
          <h2 class="text-2xl font-bold mb-1">毕业季清仓</h2>
          <p class="text-teal-50 text-sm">学长学姐带不走的好物都在这</p>
        </div>
        <!-- Decorative circles -->
        <div class="absolute -right-10 -top-10 w-32 h-32 bg-white/20 rounded-full blur-2xl"></div>
        <div class="absolute -right-4 bottom-0 w-24 h-24 bg-teal-500/30 rounded-full blur-xl"></div>
      </div>
    </div>

    <!-- Categories -->
    <div class="px-4 mb-6">
      <div class="grid grid-cols-4 gap-4">
        <div v-for="cat in categories" :key="cat.id" class="flex flex-col items-center space-y-2 cursor-pointer"
          @click="selectCategory(cat.id)">
          <div class="w-12 h-12 rounded-full flex items-center justify-center transition-all duration-200"
            :class="[cat.color, activeCategory === cat.id ? 'ring-2 ring-offset-2 ring-teal-500' : '']">
            <component :is="cat.icon" class="w-6 h-6" />
          </div>
          <span class="text-xs text-gray-600">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- Section Title -->
    <div class="px-4 mb-4 flex items-center justify-between">
      <h3 class="text-lg font-bold text-gray-900">最新发布</h3>
      <div
        class="flex items-center text-gray-500 text-sm bg-white px-2 py-1 rounded-full border border-gray-100 shadow-sm">
        <span>筛选</span>
        <FunnelIcon class="w-3 h-3 ml-1" />
      </div>
    </div>

    <!-- Product List (Masonry-like Grid) -->
    <div class="px-3">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
        <div class="columns-2 gap-3 space-y-3">
          <div v-for="item in goodsList" :key="item.id"
            class="break-inside-avoid bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-200 cursor-pointer"
            @click="router.push(`/goods/${item.id}`)">
            <!-- Image -->
            <div class="relative aspect-[4/5] bg-gray-100">
              <img :src="getFirstImage(item.images)" class="w-full h-full object-cover" loading="lazy" />
              <div
                class="absolute bottom-2 left-2 bg-black/40 backdrop-blur-sm text-white text-[10px] px-2 py-0.5 rounded-full flex items-center">
                <MapPinIcon class="w-3 h-3 mr-0.5" />
                {{ item.location || '校内' }}
              </div>
            </div>

            <!-- Content -->
            <div class="p-3">
              <h4 class="text-sm font-medium text-gray-900 line-clamp-2 mb-2 h-10 leading-5">
                {{ item.title }}
              </h4>

              <!-- Tags -->
              <div class="flex flex-wrap gap-1 mb-2">
                <span v-for="(tag, idx) in getTags(item.tags).slice(0, 2)" :key="idx"
                  class="text-[10px] px-1.5 py-0.5 rounded border"
                  :class="idx === 0 ? 'text-teal-600 border-teal-200 bg-teal-50' : 'text-gray-500 border-gray-200 bg-gray-50'">
                  {{ tag }}
                </span>
              </div>

              <!-- Price & Time -->
              <div class="flex items-center justify-between mb-2">
                <div class="text-red-500 font-bold">
                  <span class="text-xs">¥</span>
                  <span class="text-lg">{{ item.price }}</span>
                </div>
                <span class="text-[10px] text-gray-400">{{ formatTime(item.createTime) }}</span>
              </div>

              <!-- Seller -->
              <div class="flex items-center space-x-1.5 pt-2 border-t border-gray-50">
                <div class="w-5 h-5 rounded-full bg-gray-100 overflow-hidden">
                  <img v-if="item.seller?.avatar" :src="item.seller.avatar" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-[8px] text-gray-400 font-bold">
                    {{ item.seller?.nickname?.charAt(0).toUpperCase() || 'U' }}
                  </div>
                </div>
                <span class="text-xs text-gray-500 truncate max-w-[4rem]">
                  {{ item.seller?.nickname || item.seller?.username || '匿名' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<style scoped>
:deep(.van-search__content) {
  background-color: #f3f4f6;
}
</style>
