import { createRouter, createWebHistory } from 'vue-router'

import Auth from '../views/Auth.vue'
import Transaction from '../views/Transaction.vue'
import Statistics from '../views/Statistics.vue'
import Budget from '../views/Budget.vue'
import Savings from '../views/Savings.vue'
import Profile from '../views/Profile.vue'

const routes = [
    { path: '/', redirect: '/statistics' },
    { path: '/auth', component: Auth },
    { path: '/transaction', component: Transaction },
    { path: '/statistics', component: Statistics },
    { path: '/budget', component: Budget },
    { path: '/savings', component: Savings },
    { path: '/profile', component: Profile }
]

export default createRouter({
    history: createWebHistory(),
    routes
})
