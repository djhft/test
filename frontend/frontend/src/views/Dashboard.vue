<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-4">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl sm:text-2xl font-bold text-gray-800">仪表盘</h1>
            <p class="text-sm text-gray-500">欢迎回来，以下是本期财务概览</p>
          </div>
          <div class="hidden sm:flex gap-2">
            <button @click="$router.push('/transaction')" class="btn-primary">快速记账</button>
            <button @click="$router.push('/statistics')" class="btn-secondary">查看统计</button>
          </div>
        </div>
      </header>

      <!-- 概览卡片：手机2列->平板2列->桌面4列（用 grid 自动） -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-2xl shadow-card p-4">
          <p class="text-sm text-gray-500">本月收入</p>
          <p class="text-xl font-bold mt-2 text-green-600">¥{{ format(income) }}</p>
          <p class="text-xs text-gray-500 mt-1">较上月 +{{ incomeChange }}%</p>
        </div>

        <div class="bg-white rounded-2xl shadow-card p-4">
          <p class="text-sm text-gray-500">本月支出</p>
          <p class="text-xl font-bold mt-2 text-red-500">¥{{ format(expense) }}</p>
          <p class="text-xs text-gray-500 mt-1">较上月 +{{ expenseChange }}%</p>
        </div>

        <div class="bg-white rounded-2xl shadow-card p-4">
          <p class="text-sm text-gray-500">结余</p>
          <p class="text-xl font-bold mt-2 text-blue-500">¥{{ format(income - expense) }}</p>
          <p class="text-xs text-gray-500 mt-1">较上月 +{{ balanceChange }}%</p>
        </div>

        <div class="bg-white rounded-2xl shadow-card p-4">
          <p class="text-sm text-gray-500">预算剩余</p>
          <p class="text-xl font-bold mt-2 text-gray-800">¥{{ format(budgetRemaining) }}</p>
          <p class="text-xs text-gray-500 mt-1">{{ budgetUsedPercent }}% 已使用</p>
        </div>
      </div>

      <!-- 图表 + 最近交易：移动竖排，平板横向并列 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">
        <div class="lg:col-span-2 bg-white rounded-2xl shadow-card p-4">
          <h3 class="font-semibold text-gray-800 mb-3">收支趋势</h3>
          <div class="h-60 sm:h-72 lg:h-80">
            <TrendChart />
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-card p-4">
          <h3 class="font-semibold text-gray-800 mb-3">最近交易</h3>
          <div class="space-y-3">
            <div v-for="tx in recentTx" :key="tx.id" class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                  <i class="fa fa-cutlery text-red-500"></i>
                </div>
                <div>
                  <div class="text-sm text-gray-800">{{ tx.description || tx.type }}</div>
                  <div class="text-xs text-gray-500">{{ tx.transactionDate || tx.createdAt?.slice(0,10) }}</div>
                </div>
              </div>
              <div :class="tx.type==='INCOME' ? 'text-success' : 'text-danger'">
                {{ tx.type==='INCOME' ? '+' : '-' }}¥{{ Number(tx.amount).toLocaleString() }}
              </div>
            </div>
            <div v-if="!recentTx.length" class="text-center text-gray-400 py-6">暂无最近交易</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TrendChart from '@/components/Charts/TrendChart.vue'
import { listTransactions } from '@/api/transaction'

export default {
  name: 'Dashboard',
  components: { TrendChart },
  data() {
    return {
      income: 12580,
      expense: 8340,
      incomeChange: 5.3,
      expenseChange: 3.2,
      balanceChange: 12.5,
      budgetRemaining: 3660,
      budgetUsedPercent: 68,
      recentTx: []
    }
  },
  async created() {
    try {
      const res = await listTransactions(1) // demo userId=1
      let data = res.data
      try { if (typeof data === 'string') data = JSON.parse(data) } catch(e){}
      this.recentTx = Array.isArray(data) ? data.slice(0,6) : []
    } catch (e) { console.warn(e) }
  },
  methods: {
    format(v) { return v == null ? '0.00' : Number(v).toLocaleString() }
  }
}
</script>

<style scoped>
/* minor local styles if needed */
</style>
