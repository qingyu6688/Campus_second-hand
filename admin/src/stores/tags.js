import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTagsStore = defineStore('tags', () => {
    const visitedViews = ref([])
    const cachedViews = ref([])

    const addView = (view) => {
        if (visitedViews.value.some(v => v.path === view.path)) return
        visitedViews.value.push(Object.assign({}, view, {
            title: view.meta.title || 'no-name'
        }))

        if (!cachedViews.value.includes(view.name)) {
            cachedViews.value.push(view.name)
        }
    }

    const delView = (view) => {
        const i = visitedViews.value.findIndex(v => v.path === view.path)
        if (i > -1) {
            visitedViews.value.splice(i, 1)
        }
        const index = cachedViews.value.indexOf(view.name)
        if (index > -1) {
            cachedViews.value.splice(index, 1)
        }
    }

    return { visitedViews, cachedViews, addView, delView }
})
