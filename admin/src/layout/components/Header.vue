<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { SwitchButton, ArrowRight } from '@element-plus/icons-vue'
import { computed } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const handleLogout = () => {
    userStore.logout()
    router.push('/login')
}

const breadcrumbs = computed(() => {
    let matched = route.matched.filter((item) => item.meta && item.meta.title)
    return matched
})
</script>

<template>
    <div class="bg-white border-b border-gray-200 flex items-center justify-between px-4 h-14 z-10 flex-shrink-0">
        <div class="flex items-center">
            <el-breadcrumb :separator-icon="ArrowRight" class="text-sm">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
                    {{ item.meta.title }}
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="flex items-center space-x-3">
            <el-dropdown trigger="click">
                <div
                    class="flex items-center cursor-pointer hover:bg-gray-50 px-2 py-1 rounded transition-colors outline-none">
                    <el-avatar :size="28" :src="userStore.userInfo.avatar" class="mr-2 bg-gray-200 text-gray-600">
                        {{ userStore.userInfo.nickname?.charAt(0) || 'A' }}
                    </el-avatar>
                    <span class="text-xs font-medium text-gray-600">{{ userStore.userInfo.nickname || '超级管理员' }}</span>
                </div>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="router.push('/profile')">个人信息</el-dropdown-item>
                        <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>

            <el-button type="danger" plain size="small" circle @click="handleLogout" title="退出登录"
                class="!w-7 !h-7 !min-h-0">
                <el-icon :size="14">
                    <SwitchButton />
                </el-icon>
            </el-button>
        </div>
    </div>
</template>
