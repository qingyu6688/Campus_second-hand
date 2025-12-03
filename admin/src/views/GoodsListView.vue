<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, View, Refresh, SoldOut, Picture } from '@element-plus/icons-vue'
import request from '@/api/request'

const queryParams = reactive({
    page: 1,
    size: 10,
    keyword: '',
    categoryId: '',
    status: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])

// Fetch categories for filter
const fetchCategories = async () => {
    try {
        const res = await request.get('/category/list')
        if (res) {
            categories.value = res
        }
    } catch (error) {
        console.error(error)
    }
}

// Fetch goods data
const fetchData = async () => {
    loading.value = true
    try {
        const res = await request.get('/goods/admin/page', { params: queryParams })
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
    queryParams.keyword = ''
    queryParams.categoryId = ''
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
    ElMessageBox.confirm('确认删除该商品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/goods/admin/${row.id}`)
            ElMessage.success('删除成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
}

const handleOffShelf = (row) => {
    ElMessageBox.confirm('确认下架该商品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.post('/goods/admin/status', { id: row.id, status: 3 }) // 3: Off-shelf
            ElMessage.success('下架成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
}

// Status map
const getStatusTag = (status) => {
    const map = {
        0: { type: 'success', label: '在售' },
        1: { type: 'warning', label: '交易中' },
        2: { type: 'info', label: '已售出' },
        3: { type: 'danger', label: '已下架' }
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
    fetchCategories()
    fetchData()
})
</script>

<template>
    <div class="p-6">
        <!-- Search Bar -->
        <div class="bg-white p-4 rounded-lg shadow-sm mb-4">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
                <el-form-item label="关键词">
                    <el-input v-model="queryParams.keyword" placeholder="商品名称/描述" clearable
                        @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="queryParams.categoryId" placeholder="全部分类" clearable class="!w-40">
                        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="!w-32">
                        <el-option label="在售" :value="0" />
                        <el-option label="交易中" :value="1" />
                        <el-option label="已售出" :value="2" />
                        <el-option label="已下架" :value="3" />
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
                <el-table-column label="商品信息" min-width="320">
                    <template #default="scope">
                        <div class="flex items-start">
                            <!-- Image List -->
                            <div class="flex items-center space-x-2 mr-3 flex-shrink-0">
                                <template v-for="(img, index) in parseImages(scope.row.images).slice(0, 3)"
                                    :key="index">
                                    <el-image :src="img" :preview-src-list="parseImages(scope.row.images)"
                                        :initial-index="index" preview-teleported
                                        class="w-14 h-14 rounded-lg object-cover bg-gray-50 border border-gray-100"
                                        fit="cover">
                                        <template #error>
                                            <div
                                                class="w-full h-full flex items-center justify-center text-gray-300 bg-gray-50">
                                                <el-icon :size="16">
                                                    <Picture />
                                                </el-icon>
                                            </div>
                                        </template>
                                    </el-image>
                                </template>
                                <div v-if="parseImages(scope.row.images).length > 3"
                                    class="w-8 h-14 flex items-center justify-center bg-gray-50 rounded-lg border border-gray-100 text-xs text-gray-400 font-medium">
                                    +{{ parseImages(scope.row.images).length - 3 }}
                                </div>
                            </div>

                            <!-- Text Info -->
                            <div class="min-w-0 flex-1 py-1">
                                <div class="font-medium text-gray-800 line-clamp-1 text-base" :title="scope.row.title">
                                    {{ scope.row.title }}</div>
                                <div class="text-sm text-red-500 font-medium mt-1">￥{{ scope.row.price }}</div>
                                <div class="text-xs text-gray-400 mt-1 line-clamp-1" :title="scope.row.description">{{
                                    scope.row.description }}
                                </div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="分类" width="120" align="center">
                    <template #default="scope">
                        {{categories.find(c => c.id === scope.row.categoryId)?.name || scope.row.categoryId}}
                    </template>
                </el-table-column>
                <el-table-column label="卖家" width="150">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.seller">
                            <el-avatar :size="24" :src="scope.row.seller.avatar" class="mr-2">{{
                                scope.row.seller.nickname?.charAt(0) }}</el-avatar>
                            <span class="text-sm">{{ scope.row.seller.nickname }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="getStatusTag(scope.row.status).type" size="small">
                            {{ getStatusTag(scope.row.status).label }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
                <el-table-column label="操作" width="150" align="center" fixed="right">
                    <template #default="scope">
                        <el-button v-if="scope.row.status === 0" link type="warning" :icon="SoldOut"
                            @click="handleOffShelf(scope.row)">下架</el-button>
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
