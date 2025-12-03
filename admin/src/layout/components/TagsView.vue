<script setup>
import { computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsStore } from '@/stores/tags'
import { Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()

const visitedViews = computed(() => tagsStore.visitedViews)

const isActive = (view) => {
    return view.path === route.path
}

const addTags = () => {
    const { name } = route
    if (name) {
        tagsStore.addView(route)
    }
}

const closeTag = (view) => {
    tagsStore.delView(view)
    if (isActive(view)) {
        toLastView(visitedViews.value, view)
    }
}

const toLastView = (visitedViews, view) => {
    const latestView = visitedViews.slice(-1)[0]
    if (latestView) {
        router.push(latestView.path)
    } else {
        router.push('/')
    }
}

watch(() => route.path, () => {
    addTags()
})

onMounted(() => {
    addTags()
})
</script>

<template>
    <div
        class="tags-view-container bg-white border-b border-gray-200 px-4 flex items-center space-x-2 overflow-x-auto shadow-sm z-0">
        <router-link v-for="tag in visitedViews" :key="tag.path" :to="{ path: tag.path }" class="tags-view-item"
            :class="{ active: isActive(tag) }">
            {{ tag.title }}
            <span v-if="!tag.meta.affix" class="el-icon-close ml-1.5" @click.prevent.stop="closeTag(tag)">
                <el-icon class="w-3 h-3 rounded-full hover:bg-white/20 p-0.5 box-content transition-colors">
                    <Close />
                </el-icon>
            </span>
        </router-link>
    </div>
</template>

<style scoped>
.tags-view-container {
    height: 34px;
    width: 100%;
    white-space: nowrap;
}

.tags-view-item {
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    height: 26px;
    line-height: 26px;
    border: 1px solid #e5e7eb;
    color: #4b5563;
    background: #fff;
    padding: 0 10px;
    font-size: 12px;
    border-radius: 2px;
    text-decoration: none;
    transition: all 0.2s;
    margin-top: 4px;
}

.tags-view-item.active {
    background-color: #0f766e;
    color: #fff;
    border-color: #0f766e;
}

.tags-view-item:hover {
    color: #0f766e;
    border-color: #0f766e;
}

.tags-view-item.active:hover {
    color: #fff;
}
</style>
