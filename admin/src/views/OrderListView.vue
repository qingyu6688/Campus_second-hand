<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Refresh, Picture } from '@element-plus/icons-vue'
import request from '@/api/request'

const queryParams = reactive({
    page: 1,
    size: 10,
    orderNo: '',
    status: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Fetch order data
const fetchData = async () => {
    loading.value = true
    try {
        const res = await request.get('/order/admin/page', { params: queryParams })
        if (res) {
            tableData.value = res.records
            total.value = res.total
        }
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    queryParams.page = 1
    fetchData()
}

const handleReset = () => {
    queryParams.orderNo = ''
    queryParams.status = ''
    handleSearch()
}

const handleSizeChange = (val) => {
    queryParams.size = val
    fetchData()
}

const handleCurrentChange = (val) => {
    queryParams.page = val
    fetchData()
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/order/admin/${row.id}`)
            ElMessage.success('删除成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
}

// Status map
const getStatusTag = (status) => {
    const map = {
        0: { type: 'warning', label: '待交易' },
        1: { type: 'success', label: '已完成' },
        2: { type: 'info', label: '已取消' }
    }
    return map[status] || { type: 'info', label: '未知' }
}

const parseImages = (imagesStr) => {
    if (!imagesStr) return []
    try {
        // Try parsing as JSON array
        const parsed = JSON.parse(imagesStr)
        if (Array.isArray(parsed)) {
            return parsed
        }
        return [imagesStr]
    } catch (e) {
        // Fallback to comma separation if not JSON
        return imagesStr.split(',').filter(item => item.trim())
    }
}

onMounted(() => {
    fetchData()
})
</script>

<template>
    <div class="p-6">
        <!-- Search Bar -->
        <div class="bg-white p-4 rounded-lg shadow-sm mb-4">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
                <el-form-item label="订单号">
                    <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable
                        @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="!w-32">
                        <el-option label="待交易" :value="0" />
                        <el-option label="已完成" :value="1" />
                        <el-option label="已取消" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch"
                        class="!bg-[#0f766e] !border-[#0f766e]">搜索</el-button>
                    <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- Table -->
        <div class="bg-white p-4 rounded-lg shadow-sm">
            <el-table v-loading="loading" :data="tableData" style="width: 100%" border stripe>
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="orderNo" label="订单号" width="180" align="center" />

                <el-table-column label="商品信息" min-width="250">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.goods">
                            <el-image :src="parseImages(scope.row.goods.images)[0]"
                                :preview-src-list="parseImages(scope.row.goods.images)" preview-teleported
                                class="w-12 h-12 rounded object-cover mr-3 bg-gray-100 flex-shrink-0">
                                <template #error>
                                    <div class="w-full h-full flex items-center justify-center text-gray-300">
                                        <el-icon>
                                            <Picture />
                                        </el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div class="min-w-0">
                                <div class="font-medium text-gray-800 line-clamp-1" :title="scope.row.goods.title">{{
                                    scope.row.goods.title }}</div>
                                <div class="text-xs text-gray-500 mt-1">ID: {{ scope.row.goodsId }}</div>
                            </div>
                        </div>
                        <div v-else class="text-gray-400 text-sm">商品已删除</div>
                    </template>
                </el-table-column>

                <el-table-column prop="amount" label="金额" width="120" align="center">
                    <template #default="scope">
                        <span class="text-red-500 font-medium">￥{{ scope.row.amount }}</span>
                    </template>
                </el-table-column>

                <el-table-column label="买家" width="150">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.buyer">
                            <el-avatar :size="24" :src="scope.row.buyer.avatar" class="mr-2">{{
                                scope.row.buyer.nickname?.charAt(0) }}</el-avatar>
                            <span class="text-sm truncate" :title="scope.row.buyer.nickname">{{ scope.row.buyer.nickname
                                }}</span>
                        </div>
                        <div v-else class="text-gray-400 text-sm">未知用户</div>
                    </template>
                </el-table-column>

                <el-table-column label="卖家" width="150">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.seller">
                            <el-avatar :size="24" :src="scope.row.seller.avatar" class="mr-2">{{
                                scope.row.seller.nickname?.charAt(0) }}</el-avatar>
                            <span class="text-sm truncate" :title="scope.row.seller.nickname">{{
                                scope.row.seller.nickname }}</span>
                        </div>
                        <div v-else class="text-gray-400 text-sm">未知用户</div>
                    </template>
                </el-table-column>

                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="getStatusTag(scope.row.status).type" size="small">
                            {{ getStatusTag(scope.row.status).label }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="createTime" label="创建时间" width="180" align="center" />

                <el-table-column label="操作" width="100" align="center" fixed="right">
                    <template #default="scope">
                        <el-button link type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- Pagination -->
            <div class="flex justify-end mt-4">
                <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size"
                    :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </div>
    </div>
</template>
