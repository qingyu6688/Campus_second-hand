<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'

const userStore = useUserStore()
const form = ref({
    id: '',
    username: '',
    nickname: '',
    avatar: '',
    password: ''
})

const loading = ref(false)

const fetchUserInfo = async () => {
    try {
        const res = await request.post('/admin/info')
        if (res) {
            form.value = { ...res, password: '' } // Clear password field
            // Update store as well to keep it fresh
            userStore.userInfo = res
            localStorage.setItem('admin_user_info', JSON.stringify(res))
        }
    } catch (error) {
        console.error(error)
    }
}

const handleAvatarSuccess = (response, uploadFile) => {
    // response is the result from /api/common/upload
    // Assuming backend returns Result<String> where data is the URL
    if (response.code === 200) {
        form.value.avatar = response.data
        ElMessage.success('头像上传成功')
    } else {
        ElMessage.error(response.msg || '上传失败')
    }
}

const beforeAvatarUpload = (rawFile) => {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('Avatar picture must be JPG format!')
        return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('Avatar picture size can not exceed 2MB!')
        return false
    }
    return true
}

const handleSubmit = async () => {
    loading.value = true
    try {
        const dataToSubmit = { ...form.value }
        if (!dataToSubmit.password) {
            delete dataToSubmit.password
        }

        const res = await request.post('/admin/update', dataToSubmit)
        if (res) {
            ElMessage.success('更新成功')
            fetchUserInfo()
        }
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchUserInfo()
})
</script>

<template>
    <div class="max-w-5xl mx-auto py-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <!-- Left Column: Profile Card -->
            <div class="md:col-span-1">
                <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div class="h-32 bg-gradient-to-r from-teal-500 to-emerald-600 relative">
                        <div class="absolute -bottom-12 left-1/2 transform -translate-x-1/2">
                            <el-upload class="avatar-uploader group relative" action="/api/common/upload"
                                :show-file-list="false" :on-success="handleAvatarSuccess"
                                :before-upload="beforeAvatarUpload" name="file"
                                :headers="{ Authorization: userStore.token }">
                                <div
                                    class="relative w-24 h-24 rounded-full border-4 border-white overflow-hidden shadow-md bg-white">
                                    <img v-if="form.avatar" :src="form.avatar" class="w-full h-full object-cover" />
                                    <div v-else
                                        class="w-full h-full flex items-center justify-center bg-gray-50 text-gray-300">
                                        <el-icon :size="32">
                                            <Plus />
                                        </el-icon>
                                    </div>
                                    <!-- Hover Overlay -->
                                    <div
                                        class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                        <span class="text-white text-xs font-medium">更换头像</span>
                                    </div>
                                </div>
                            </el-upload>
                        </div>
                    </div>
                    <div class="pt-16 pb-8 px-6 text-center">
                        <h3 class="text-xl font-bold text-gray-800">{{ form.nickname || '未设置昵称' }}</h3>
                        <p class="text-sm text-gray-500 mt-1">@{{ form.username }}</p>
                        <div class="mt-4 flex justify-center gap-2">
                            <span
                                class="px-3 py-1 bg-teal-50 text-teal-700 text-xs font-medium rounded-full">超级管理员</span>
                            <span
                                class="px-3 py-1 bg-blue-50 text-blue-700 text-xs font-medium rounded-full">状态正常</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Column: Edit Form -->
            <div class="md:col-span-2">
                <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                    <div class="flex items-center justify-between mb-8">
                        <div>
                            <h2 class="text-lg font-bold text-gray-800">编辑个人信息</h2>
                            <p class="text-sm text-gray-500 mt-1">更新您的个人详细信息和登录密码</p>
                        </div>
                    </div>

                    <el-form :model="form" label-position="top" class="space-y-5">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <el-form-item label="用户名">
                                <el-input v-model="form.username" disabled class="custom-input bg-gray-50" />
                                <span class="text-xs text-gray-400 mt-1.5 block">用户名作为登录凭证，不可修改</span>
                            </el-form-item>

                            <el-form-item label="昵称">
                                <el-input v-model="form.nickname" placeholder="请输入您的昵称" class="custom-input" />
                            </el-form-item>
                        </div>

                        <el-divider content-position="left"><span
                                class="text-gray-400 text-xs font-normal">安全设置</span></el-divider>

                        <el-form-item label="新密码">
                            <el-input v-model="form.password" type="password" placeholder="如果不修改密码，请留空" show-password
                                class="custom-input" />
                            <span class="text-xs text-gray-400 mt-1.5 block">密码长度建议不少于6位，包含字母和数字</span>
                        </el-form-item>

                        <div class="pt-4 flex justify-end">
                            <el-button type="primary" :loading="loading" @click="handleSubmit"
                                class="!bg-[#0f766e] !border-[#0f766e] !h-10 !px-8 !rounded-lg hover:!bg-teal-800 hover:!border-teal-800 shadow-lg shadow-teal-900/10">
                                保存更改
                            </el-button>
                        </div>
                    </el-form>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
:deep(.custom-input .el-input__wrapper) {
    box-shadow: none !important;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 8px 12px;
    transition: all 0.2s;
}

:deep(.custom-input .el-input__wrapper:hover) {
    border-color: #d1d5db;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
    border-color: #0f766e;
    box-shadow: 0 0 0 1px #0f766e !important;
}

:deep(.el-form-item__label) {
    font-weight: 500;
    color: #374151;
    margin-bottom: 6px !important;
}
</style>
