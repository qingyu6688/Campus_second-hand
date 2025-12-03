<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/api/request'
import { User, Goods, List, Money } from '@element-plus/icons-vue'

const stats = ref([
    { title: '总用户数', value: 0, icon: User, color: 'bg-blue-500', key: 'userCount' },
    { title: '总商品数', value: 0, icon: Goods, color: 'bg-green-500', key: 'goodsCount' },
    { title: '总订单数', value: 0, icon: List, color: 'bg-orange-500', key: 'orderCount' },
    { title: '总交易额', value: 0, icon: Money, color: 'bg-purple-500', key: 'totalAmount', isMoney: true },
])

const recentOrders = ref([])
const trendChartRef = ref(null)
const categoryChartRef = ref(null)

const fetchData = async () => {
    try {
        const res = await request.get('/admin/dashboard/stats')
        if (res) {
            // Update stats
            stats.value.forEach(item => {
                if (item.key === 'totalAmount') {
                    item.value = res.totalAmount || 0
                } else {
                    item.value = res[item.key] || 0
                }
            })

            // Update recent orders
            recentOrders.value = res.recentOrders || []

            // Render charts
            initCharts(res.orderTrend, res.categoryData)
        }
    } catch (error) {
        console.error(error)
    }
}

const initCharts = (trendData, categoryData) => {
    if (!trendData || !categoryData) return

    // Order Trend Chart
    const trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
        title: { text: '近7日订单趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: trendData.map(item => item.date) },
        yAxis: { type: 'value' },
        series: [{
            data: trendData.map(item => item.count),
            type: 'line',
            smooth: true,
            areaStyle: { opacity: 0.3 },
            itemStyle: { color: '#0f766e' },
            lineStyle: { color: '#0f766e' }
        }]
    })

    // Category Chart
    const categoryChart = echarts.init(categoryChartRef.value)
    categoryChart.setOption({
        title: { text: '商品分类分布' },
        tooltip: { trigger: 'item' },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: { show: false, position: 'center' },
            emphasis: {
                label: { show: true, fontSize: 20, fontWeight: 'bold' }
            },
            data: categoryData
        }]
    })

    // Resize listener
    window.addEventListener('resize', () => {
        trendChart.resize()
        categoryChart.resize()
    })
}

const parseImages = (imagesStr) => {
    if (!imagesStr) return []
    try {
        const parsed = JSON.parse(imagesStr)
        if (Array.isArray(parsed)) return parsed
        return [imagesStr]
    } catch (e) {
        return imagesStr.split(',').filter(item => item.trim())
    }
}

onMounted(() => {
    fetchData()
})
</script>

<template>
    <div class="p-6">
        <h2 class="text-2xl font-bold mb-6 text-gray-800">控制台</h2>
        
        <!-- Stats Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div v-for="(item, index) in stats" :key="index" class="bg-white rounded-lg shadow-sm p-6 flex items-center transition-transform hover:scale-105">
                <div :class="['w-12 h-12 rounded-full flex items-center justify-center text-white mr-4 shadow-md', item.color]">
                    <el-icon :size="24"><component :is="item.icon" /></el-icon>
                </div>
                <div>
                    <p class="text-gray-500 text-sm font-medium">{{ item.title }}</p>
                    <p class="text-2xl font-bold text-gray-800">
                        {{ item.isMoney ? '¥ ' + item.value : item.value }}
                    </p>
                </div>
            </div>
        </div>

        <!-- Charts -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <div class="bg-white rounded-lg shadow-sm p-6">
                <div ref="trendChartRef" class="w-full h-80"></div>
            </div>
            <div class="bg-white rounded-lg shadow-sm p-6">
                <div ref="categoryChartRef" class="w-full h-80"></div>
            </div>
        </div>

        <!-- Recent Orders -->
        <div class="bg-white rounded-lg shadow-sm p-6">
            <h3 class="text-lg font-bold mb-4 text-gray-800 flex items-center">
                <el-icon class="mr-2 text-[#0f766e]"><List /></el-icon>
                最近订单
            </h3>
            <el-table :data="recentOrders" style="width: 100%">
                <el-table-column prop="orderNo" label="订单号" width="180" />
                <el-table-column label="商品" min-width="200">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.goods">
                            <img :src="parseImages(scope.row.goods.images)[0]" class="w-10 h-10 rounded object-cover mr-2 bg-gray-100" />
                            <span class="truncate">{{ scope.row.goods.title }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="amount" label="金额" width="120">
                    <template #default="scope">
                        <span class="text-red-500 font-medium">¥ {{ scope.row.amount }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="时间" width="180" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 1" type="success" size="small">已完成</el-tag>
                        <el-tag v-else-if="scope.row.status === 0" type="warning" size="small">待交易</el-tag>
                        <el-tag v-else type="info" size="small">已取消</el-tag>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>
