<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Refresh, ChatDotRound } from '@element-plus/icons-vue'
import request from '@/api/request'

const queryParams = reactive({
    page: 1,
    size: 10,
    keyword: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Fetch chat data
const fetchData = async () => {
    loading.value = true
    try {
        const res = await request.get('/chat/admin/page', { params: queryParams })
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
    ElMessageBox.confirm('确认删除该条消息吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/chat/admin/${row.id}`)
            ElMessage.success('删除成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
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
                <el-form-item label="关键词">
                    <el-input v-model="queryParams.keyword" placeholder="消息内容" clearable
                        @keyup.enter="handleSearch" />
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
                
                <el-table-column label="发送者" width="180">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.sender">
                            <el-avatar :size="24" :src="scope.row.sender.avatar" class="mr-2">{{ scope.row.sender.nickname?.charAt(0) }}</el-avatar>
                            <span class="text-sm truncate" :title="scope.row.sender.nickname">{{ scope.row.sender.nickname }}</span>
                        </div>
                        <div v-else class="text-gray-400 text-sm">未知用户</div>
                    </template>
                </el-table-column>

                <el-table-column label="接收者" width="180">
                    <template #default="scope">
                        <div class="flex items-center" v-if="scope.row.receiver">
                            <el-avatar :size="24" :src="scope.row.receiver.avatar" class="mr-2">{{ scope.row.receiver.nickname?.charAt(0) }}</el-avatar>
                            <span class="text-sm truncate" :title="scope.row.receiver.nickname">{{ scope.row.receiver.nickname }}</span>
                        </div>
                        <div v-else class="text-gray-400 text-sm">未知用户</div>
                    </template>
                </el-table-column>

                <el-table-column prop="content" label="消息内容" min-width="200" show-overflow-tooltip />

                <el-table-column prop="createTime" label="发送时间" width="180" align="center" />

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
