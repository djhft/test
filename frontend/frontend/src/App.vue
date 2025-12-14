<template>
  <div class="flex">

    <!-- PC 左侧导航栏 -->
    <Sidebar class="hidden lg:block" />

    <!-- 主内容 -->
    <div class="flex-1 min-h-screen bg-neutral">
      <router-view />
    </div>

    <!-- 移动端底部导航 -->
    <MobileNav class="lg:hidden" />
  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'
import MobileNav from '@/components/MobileNav.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

export default {
  components: { Sidebar, MobileNav, ElementPlus }
}
</script>
