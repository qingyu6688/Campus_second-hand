<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
    id: null,
    name: '',
    icon: '',
    sortOrder: 0,
    status: 1
})

const rules = {
    name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
    sortOrder: [{ required: true, message: '请输入排序权重', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await request.get('/category/list')
        if (res) {
            tableData.value = res
        }
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleAdd = () => {
    dialogTitle.value = '添加分类'
    dialogVisible.value = true
    Object.assign(form, {
        id: null,
        name: '',
        icon: '',
        sortOrder: 0,
        status: 1
    })
    if (formRef.value) formRef.value.resetFields()
}

const handleEdit = (row) => {
    dialogTitle.value = '编辑分类'
    dialogVisible.value = true
    Object.assign(form, { ...row })
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该分类吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/category/${row.id}`)
            ElMessage.success('删除成功')
            fetchData()
        } catch (error) {
            console.error(error)
        }
    })
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await request.post('/category/save', form)
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
        <div class="bg-white p-4 rounded-lg shadow-sm">
            <div class="mb-4 flex justify-between items-center">
                <h2 class="text-lg font-bold text-gray-800">分类管理</h2>
                <el-button type="primary" :icon="Plus" @click="handleAdd"
                    class="!bg-[#0f766e] !border-[#0f766e]">添加分类</el-button>
            </div>

            <el-table v-loading="loading" :data="tableData" style="width: 100%" border stripe row-key="id">
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="name" label="分类名称" min-width="120" />
                <el-table-column prop="icon" label="图标" width="100" align="center">
                    <template #default="scope">
                        <el-icon v-if="scope.row.icon" :size="20">
                            <component :is="scope.row.icon" />
                        </el-icon>
                    </template>
                </el-table-column>
                <el-table-column prop="sortOrder" label="排序" width="100" align="center" sortable />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                            {{ scope.row.status === 1 ? '启用' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center" fixed="right">
                    <template #default="scope">
                        <el-button link type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- Dialog -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入分类名称" />
                </el-form-item>
                <el-form-item label="图标" prop="icon">
                    <el-input v-model="form.icon" placeholder="例如: Goods, Book, User" />
                    <div class="text-xs text-gray-400 mt-1">请输入Element Plus图标组件名称 (如: Goods)</div>
                </el-form-item>
                <el-form-item label="排序权重" prop="sortOrder">
                    <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="1">启用</el-radio>
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
