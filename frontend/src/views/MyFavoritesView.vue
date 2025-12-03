<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { ClockIcon, HeartIcon } from '@heroicons/vue/24/outline'
import { HeartIcon as HeartIconSolid } from '@heroicons/vue/24/solid'
import request from '@/api/request'

const router = useRouter()
const goodsList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)

const onLoad = async () => {
    if (refreshing.value) {
        goodsList.value = []
        refreshing.value = false
    }

    try {
        const res = await request.get('/favorite/my-goods', {
            params: {
                page: page.value,
                size: 10
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

const handleUnfavorite = (item) => {
    showDialog({
        title: '取消收藏',
        message: '确定要取消收藏这个商品吗？',
        showCancelButton: true,
    }).then(async (action) => {
        if (action === 'confirm') {
            try {
                await request.post('/favorite/toggle', {
                    goodsId: item.id
                })
                showToast('已取消收藏')
                goodsList.value = goodsList.value.filter(g => g.id !== item.id)
            } catch (error) {
                showToast('操作失败')
            }
        }
    })
}

const formatTime = (time) => {
    if (!time) return ''
    const date = new Date(time)
    return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<template>
    <div class="min-h-screen bg-gray-50 pb-20">
        <van-nav-bar title="我的收藏" left-arrow fixed placeholder @click-left="router.back()" class="z-50" />

        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
            <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad"
                class="p-4 space-y-4">
                <template #empty>
                    <van-empty description="还没有收藏任何商品哦" />
                </template>

                <div v-for="item in goodsList" :key="item.id"
                    class="bg-white rounded-2xl p-3 shadow-sm flex space-x-3 active:scale-[0.99] transition-transform duration-200"
                    @click="router.push(`/goods/${item.id}`)">
                    <!-- Image -->
                    <div class="w-28 h-28 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 relative">
                        <img :src="getFirstImage(item.images)" class="w-full h-full object-cover" />
                        <!-- Status Badge -->
                        <div v-if="item.status === 2"
                            class="absolute inset-0 bg-black/50 flex items-center justify-center">
                            <span class="text-white text-xs font-bold border border-white px-2 py-1 rounded-full">已卖出</span>
                        </div>
                    </div>

                    <!-- Content -->
                    <div class="flex-1 flex flex-col justify-between py-1">
                        <div>
                            <div class="flex justify-between items-start">
                                <h3 class="text-sm font-bold text-gray-900 line-clamp-2 flex-1 mr-2 leading-snug">{{
                                    item.title }}</h3>
                            </div>
                            <div class="mt-2 flex items-baseline space-x-2">
                                <span class="text-red-500 font-bold text-lg">¥{{ item.price }}</span>
                                <span class="text-gray-400 text-xs line-through" v-if="item.originalPrice">¥{{
                                    item.originalPrice
                                }}</span>
                            </div>
                            <div class="flex items-center text-xs text-gray-400 mt-1">
                                <ClockIcon class="w-3 h-3 mr-1" />
                                {{ formatTime(item.createTime) }} · {{ item.viewCount || 0 }}浏览
                            </div>
                        </div>

                        <div class="flex justify-end space-x-2 mt-2">
                            <button @click.stop="handleUnfavorite(item)"
                                class="flex items-center text-xs font-medium text-red-500 bg-red-50 px-3 py-1.5 rounded-full active:bg-red-100 transition-colors">
                                <HeartIconSolid class="w-3.5 h-3.5 mr-1" />
                                取消收藏
                            </button>
                        </div>
                    </div>
                </div>
            </van-list>
        </van-pull-refresh>
    </div>
</template>
