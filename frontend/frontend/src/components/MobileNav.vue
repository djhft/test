<template>
  <nav class="lg:hidden fixed bottom-0 left-0 right-0 bg-white shadow-lg border-t border-gray-200 flex justify-around py-2 z-10">
    <a href="#" @click.prevent="navigate('statistics')" :class="['mobile-nav-link', active==='statistics' ? 'active' : '']" data-page="statistics">
      <i class="fa fa-bar-chart text-xl mb-1"></i>
      <span>统计</span>
    </a>

    <a href="#" @click.prevent="navigate('transaction')" :class="['mobile-nav-link', active==='transaction' ? 'active' : '']" data-page="transaction">
      <i class="fa fa-credit-card text-xl mb-1"></i>
      <span>记账</span>
    </a>

    <a href="#" @click.prevent="navigate('profile')" :class="['mobile-nav-link', active==='profile' ? 'active' : '']" data-page="profile">
      <i class="fa fa-user text-xl mb-1"></i>
      <span>我的</span>
    </a>
  </nav>
</template>

<script>
export default {
  name: 'MobileNav',
  data() {
    return {
      active: (this.$route && this.$route.path) ? this.$route.path.replace('/', '') : 'statistics'
    }
  },
  watch: {
    '$route'(to) {
      this.active = to.path.replace('/', '') || 'statistics'
    }
  },
  methods: {
    navigate(page) {
      const mapping = {
        statistics: '/statistics',
        transaction: '/transaction',
        profile: '/profile'
      }
      this.$router.push(mapping[page] || '/dashboard')
    }
  }
}
</script>

<style scoped>
.mobile-nav-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 6px;
  padding-bottom: 6px;
  color: #6b7280; /* gray-600 */
}
.mobile-nav-link.active {
  color: #3b82f6;
}
</style>
