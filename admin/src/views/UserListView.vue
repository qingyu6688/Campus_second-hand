<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import request from '@/api/request'

// Search params
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    username: '',
    nickname: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
    id: null,
    username: '',
    password: '',
    nickname: '',
    mobile: '',
    schoolName: '',
    status: 1
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// Fetch data
const fetchData = async () => {
    loading.value = true
    try {
        const res = await request.get('/user/page', { params: queryParams })
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

// Search
const handleSearch = () => {
    queryParams.pageNum = 1
    fetchData()
}

// Reset Search
const handleReset = () => {
    queryParams.username = ''
    queryParams.nickname = ''
    handleSearch()
}

// Pagination
const handleSizeChange = (val) => {
    queryParams.pageSize = val
    fetchData()
}

const handleCurrentChange = (val) => {
    queryParams.pageNum = val
    fetchData()
}

// Add User
const handleAdd = () => {
    dialogTitle.value = '添加用户'
    dialogVisible.value = true
    Object.assign(form, {
        id: null,
        username: '',
        password: '',
        nickname: '',
        mobile: '',
        schoolName: '',
        status: 1
    })
    if (formRef.value) formRef.value.resetFields()
}

// Edit User
const handleEdit = (row) => {
    dialogTitle.value = '编辑用户'
    dialogVisible.value = true
    Object.assign(form, { ...row, password: '' }) // Clear password for security
}

// Delete User
const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该用户吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/user/${row.id}`)
            ElMessage.success('删除成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
}

// Submit Form
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await request.post('/user/save', form)
                ElMessage.success(form.id ? '更新成功' : '添加成功')
                dialogVisible.value = false
                fetchData()
            } catch (error) {
                console.error(error)
            }
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
                <el-form-item label="用户名">
                    <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable
                        @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable
                        @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch"
                        class="!bg-[#0f766e] !border-[#0f766e]">搜索</el-button>
                    <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- Action Bar & Table -->
        <div class="bg-white p-4 rounded-lg shadow-sm">
            <div class="mb-4">
                <el-button type="primary" :icon="Plus" @click="handleAdd"
                    class="!bg-[#0f766e] !border-[#0f766e]">添加用户</el-button>
            </div>

            <el-table v-loading="loading" :data="tableData" style="width: 100%" border stripe>
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="username" label="用户名" min-width="120" />
                <el-table-column prop="nickname" label="昵称" min-width="120" />
                <el-table-column prop="role" label="角色" width="100" align="center">
                    <template #default>
                        <el-tag type="info">普通用户</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="schoolName" label="学校" min-width="150" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                            {{ scope.row.status === 1 ? '正常' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="注册时间" width="180" align="center" />
                <el-table-column label="操作" width="180" align="center" fixed="right">
                    <template #default="scope">
                        <el-button link type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- Pagination -->
            <div class="flex justify-end mt-4">
                <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </div>

        <!-- Dialog -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="如果不修改请留空" show-password />
                </el-form-item>
                <el-form-item label="手机号" prop="mobile">
                    <el-input v-model="form.mobile" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="学校" prop="schoolName">
                    <el-input v-model="form.schoolName" placeholder="请输入学校名称" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="1">正常</el-radio>
                        <el-radio :label="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit"
                        class="!bg-[#0f766e] !border-[#0f766e]">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>
