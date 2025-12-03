<script setup>
import { useRoute } from 'vue-router'
import {
    Odometer,
    User,
    Goods,
    List,
    Menu as IconMenu,
    ChatLineSquare,
    ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
</script>

<template>
    <div class="w-[220px] bg-[#001529] text-white flex flex-col transition-all duration-300 shadow-xl z-20 h-full">
        <div
            class="h-14 flex items-center justify-center text-lg font-bold border-b border-gray-800 bg-[#001529] flex-shrink-0">
            <span class="text-[#0f766e] mr-2 text-xl">Campus</span> Admin
        </div>
        <el-menu :default-active="route.path" active-text-color="#fff" background-color="#001529"
            class="el-menu-vertical-demo flex-1 border-r-0 py-4 overflow-y-auto" text-color="#a6adb4" router>
            <el-menu-item index="/dashboard">
                <el-icon>
                    <Odometer />
                </el-icon>
                <span>控制台</span>
            </el-menu-item>

            <el-menu-item index="/users">
                <el-icon>
                    <User />
                </el-icon>
                <span>用户管理</span>
            </el-menu-item>

            <el-menu-item index="/category">
                <el-icon>
                    <IconMenu />
                </el-icon>
                <span>分类管理</span>
            </el-menu-item>

            <el-menu-item index="/goods">
                <el-icon>
                    <Goods />
                </el-icon>
                <span>商品管理</span>
            </el-menu-item>

            <el-menu-item index="/orders">
                <el-icon>
                    <List />
                </el-icon>
                <span>订单管理</span>
            </el-menu-item>

            <el-menu-item index="/comments">
                <el-icon>
                    <ChatLineSquare />
                </el-icon>
                <span>评论管理</span>
            </el-menu-item>

            <el-menu-item index="/chats">
                <el-icon>
                    <ChatDotRound />
                </el-icon>
                <span>聊天管理</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<style scoped>
.el-menu-item {
    margin: 4px 0;
    height: 50px;
    line-height: 50px;
}

.el-menu-item.is-active {
    background-color: #0f766e !important;
    border-right: 3px solid #34d399;
}

.el-menu-item:hover {
    color: #fff !important;
    background-color: #002140 !important;
}
</style>
