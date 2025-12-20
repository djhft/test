import { createRouter, createWebHistory } from 'vue-router'

import Auth from '../views/Auth.vue'
import Transaction from '../views/Transaction.vue'
import Statistics from '../views/Statistics.vue'
import Budget from '../views/Budget.vue'
import Savings from '../views/Savings.vue'
import Profile from '../views/Profile.vue'

const routes = [
    {
        path: '/',
        redirect: '/statistics'
    },
    {
        path: '/auth',
        component: Auth,
        meta: { requiresGuest: true } // 只有未登录用户可以访问
    },
    {
        path: '/transaction',
        component: Transaction,
        meta: { requiresAuth: true } // 需要登录才能访问
    },
    {
        path: '/statistics',
        component: Statistics,
        meta: { requiresAuth: true }
    },
    {
        path: '/budget',
        component: Budget,
        meta: { requiresAuth: true }
    },
    {
        path: '/savings',
        component: Savings,
        meta: { requiresAuth: true }
    },
    {
        path: '/profile',
        component: Profile,
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 检查是否需要认证
    if (to.meta.requiresAuth) {
        if (token) {
            next()
        } else {
            next('/auth')
        }
    }
    // 检查是否是游客页面（已登录用户不能访问）
    else if (to.meta.requiresGuest) {
        if (token) {
            next('/statistics')
        } else {
            next()
        }
    }
    // 其他页面直接放行
    else {
        next()
    }
})

export default router
