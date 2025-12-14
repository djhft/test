<template>
  <aside class="hidden lg:flex flex-col w-64 bg-white shadow-md fixed h-full left-0 top-0 p-4">
    <div class="flex items-center justify-center mb-8 mt-4">
      <img src="../assets/logo.png" alt="logo" class="h-12" />
      <span class="text-xl font-bold ml-2">个人财务记账系统</span>
    </div>

    <nav class="flex-1 space-y-2">
      <a href="#" @click.prevent="navigate('statistics')" :class="['nav-link', active==='statistics' ? 'active' : '']" data-page="statistics">
        <img src="../assets/statistics.png" alt="statistics" class="w-5 h-5" />
        <span>统计分析</span>
      </a>

      <a href="#" @click.prevent="navigate('transaction')" :class="['nav-link', active==='transaction' ? 'active' : '']" data-page="transaction">
        <img src="../assets/account_book.png" alt="transaction" class="w-5 h-5">
        <span>记账</span>
      </a>

      <a href="#" @click.prevent="navigate('budget')" :class="['nav-link', active==='budget' ? 'active' : '']" data-page="budget">
        <img src="../assets/budget_management.png" alt="budget" class="w-5 h-5">
        <span>预算管理</span>
      </a>

      <a href="#" @click.prevent="navigate('savings')" :class="['nav-link', active==='savings' ? 'active' : '']" data-page="savings">
        <img src="../assets/savings_goal.png" alt="savings" class="w-5 h-5">
        <span>储蓄目标</span>
      </a>
    </nav>

    <div class="mt-auto pt-4 border-t border-gray-200">
      <a href="#" @click.prevent="navigate('profile')" :class="['nav-link', active==='profile' ? 'active' : '']">
        <i class="fa fa-user w-5"></i>
        <span>个人中心</span>
      </a>

      <a href="#" class="nav-link text-red-500 hover:bg-red-50" @click.prevent="logout">
        <i class="fa fa-sign-out w-5"></i>
        <span>退出登录</span>
      </a>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar',
  data() {
    return {
      active: 'statistics'
    }
  },
  created() {
    // initialize active based on current route
    const path = (this.$route && this.$route.path) ? this.$route.path.replace('/', '') : 'dashboard'
    this.active = path || 'statistics'
  },
  watch: {
    '$route'(to) {
      this.active = to.path.replace('/', '') || 'statistics'
    }
  },
  methods: {
    navigate(page) {
      // map page -> route
      const mapping = {
        statistics: '/statistics',
        transaction: '/transaction',
        budget: '/budget',
        savings: '/savings',
        profile: '/profile'
      }
      const target = mapping[page] || '/dashboard'
      this.$router.push(target)
    },
    logout() {
      localStorage.removeItem('token')
      this.$router.push('/auth')
    }
  }
}
</script>

<style scoped>
/* Scoped adjustments if needed */
</style>
