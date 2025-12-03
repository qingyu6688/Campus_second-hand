import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/LoginView.vue')
        },
        {
            path: '/publish',
            name: 'publish',
            component: () => import('../views/PublishView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('../views/ProfileView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/profile/edit',
            name: 'profile-edit',
            component: () => import('../views/ProfileEditView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/address',
            name: 'address',
            component: () => import('../views/AddressView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/goods/:id',
            name: 'goods-detail',
            component: () => import('../views/DetailView.vue'),
            meta: { hideNav: true }
        },
        {
            path: '/my-goods',
            name: 'my-goods',
            component: () => import('../views/MyGoodsView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/my-favorites',
            name: 'my-favorites',
            component: () => import('../views/MyFavoritesView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/user/:id',
            name: 'user-home',
            component: () => import('../views/UserHomeView.vue'),
            meta: { hideNav: true }
        },
        {
            path: '/order/create/:goodsId',
            name: 'order-create',
            component: () => import('../views/OrderCreateView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/my-orders',
            name: 'my-orders',
            component: () => import('../views/MyOrdersView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/my-sold',
            name: 'my-sold',
            component: () => import('../views/MySoldView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/order/detail/:id',
            name: 'order-detail',
            component: () => import('../views/OrderDetailView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/chat/:id',
            name: 'chat',
            component: () => import('../views/ChatView.vue'),
            meta: { requiresAuth: true, hideNav: true }
        },
        {
            path: '/message',
            name: 'message',
            component: () => import('../views/MessageView.vue'),
            meta: { requiresAuth: true }
        },
        // Add other routes here
    ]
})

export default router
