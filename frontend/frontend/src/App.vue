<template>
  <div class="flex min-h-screen overflow-x-hidden">


    <!-- PC 左侧导航栏（参与布局，不再 fixed） -->
    <Sidebar class="hidden lg:flex" />


    <!-- 主内容区域：关键是 min-w-0 -->
    <div class="flex-1 min-w-0 bg-neutral overflow-x-hidden">
      <router-view />
    </div>


    <!-- 移动端底部导航 -->
    <MobileNav class="lg:hidden" />
  </div>
</template>


<script>
import Sidebar from '@/components/Sidebar.vue'
import MobileNav from '@/components/MobileNav.vue'


export default {
  name: 'App',
  components: {Sidebar, MobileNav},
  created() {
    const token = localStorage.getItem('token')
    if (token && this.$route.path === '/auth') {
      this.$router.push('/statistics')
    }
  }
}
</script>