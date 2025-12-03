import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/dashboard'
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/LoginView.vue')
        },
        {
            path: '/',
            component: () => import('../layout/MainLayout.vue'),
            children: [
                {
                    path: 'dashboard',
                    name: 'dashboard',
                    component: () => import('../views/DashboardView.vue'),
                    meta: { title: '控制台', affix: true }
                },
                {
                    path: 'users',
                    name: 'users',
                    component: () => import('../views/UserListView.vue'),
                    meta: { title: '用户管理' }
                },
                {
                    path: 'category',
                    name: 'category',
                    component: () => import('../views/CategoryListView.vue'),
                    meta: { title: '分类管理' }
                },
                {
                    path: 'goods',
                    name: 'goods',
                    component: () => import('../views/GoodsListView.vue'),
                    meta: { title: '商品管理' }
                },
                {
                    path: 'orders',
                    name: 'orders',
                    component: () => import('../views/OrderListView.vue'),
                    meta: { title: '订单管理' }
                },
                {
                    path: 'comments',
                    name: 'comments',
                    component: () => import('../views/CommentListView.vue'),
                    meta: { title: '评论管理' }
                },
                {
                    path: 'chats',
                    name: 'chats',
                    component: () => import('../views/ChatListView.vue'),
                    meta: { title: '聊天管理' }
                },
                {
                    path: 'profile',
                    name: 'profile',
                    component: () => import('../views/ProfileView.vue'),
                    meta: { title: '个人信息' }
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('admin_token')
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router
