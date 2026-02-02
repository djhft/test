<template>
  <div class="flex min-h-screen overflow-x-hidden">

    <!-- 左侧导航栏：仅非登录页显示 -->
    <Sidebar
        v-if="!isAuthPage"
        class="hidden lg:flex"
    />

    <!-- 主内容区域 -->
    <div
        class="flex-1 min-w-0 overflow-x-hidden"
        :class="isAuthPage ? 'bg-gray-100' : 'bg-neutral'"
    >
      <router-view />
    </div>

    <!-- 移动端底部导航：仅非登录页显示 -->
    <MobileNav
        v-if="!isAuthPage"
        class="lg:hidden"
    />
  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'
import MobileNav from '@/components/MobileNav.vue'

export default {
  name: 'App',
  components: {
    Sidebar,
    MobileNav
  },
  computed: {
    // 是否是登录 / 注册页面
    isAuthPage() {
      return this.$route.path === '/auth'
      // 如果你后面拆成 /login /register，可改成：
      // return ['/auth', '/login', '/register'].includes(this.$route.path)
    }
  },
  created() {
    const token = localStorage.getItem('token')
    if (token && this.$route.path === '/auth') {
      this.$router.push('/statistics')
    }
  }
}
</script>
