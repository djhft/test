<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full px-4 sm:px-6 lg:px-8 py-4 sm:py-6">
      <header class="mb-4 sm:mb-6">
        <h1 class="text-xl sm:text-2xl lg:text-3xl font-bold text-gray-800 transition-all duration-300 break-words">统计分析</h1>
        <p class="text-sm sm:text-base text-gray-500 mt-1 break-words">多维度分析您的财务状况</p>
      </header>

      <!-- 概览卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-3 sm:gap-4 mb-4 sm:mb-6">
        <div class="bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 hover:shadow-lg transition-shadow duration-300">
          <div class="flex items-center">
            <img src="../assets/total_income.png" alt="总收入" class="w-8 h-8 sm:w-9 sm:h-9 flex-shrink-0" />
            <p class="text-gray-500 text-lg sm:text-xl lg:text-2xl ml-3 truncate">总收入</p>
          </div>
          <p class="text-xl sm:text-2xl font-bold text-green-600 mt-2 break-words">¥{{ totalIncome }}</p>
        </div>

        <div class="bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 hover:shadow-lg transition-shadow duration-300">
          <div class="flex items-center">
            <img src="../assets/total_expenditures.png" alt="总支出" class="w-8 h-8 sm:w-9 sm:h-9 flex-shrink-0" />
            <p class="text-gray-500 text-lg sm:text-xl lg:text-2xl ml-3 truncate">总支出</p>
          </div>
          <p class="text-xl sm:text-2xl font-bold text-red-500 mt-2 break-words">¥{{ totalExpense }}</p>
        </div>

        <div class="bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 hover:shadow-lg transition-shadow duration-300">
          <div class="flex items-center">
            <img src="../assets/surplus.png" alt="结余" class="w-8 h-8 sm:w-9 sm:h-9 flex-shrink-0" />
            <p class="text-gray-500 text-lg sm:text-xl lg:text-2xl ml-3 truncate">结余</p>
          </div>
          <p class="text-xl sm:text-2xl font-bold text-blue-500 mt-2 break-words">¥{{ totalIncome - totalExpense }}</p>
        </div>
      </div>

      <!-- 图表区 -->
      <div class="grid grid-cols-1 lg:grid-cols-5 gap-3 sm:gap-4 mb-4 sm:mb-6">
        <div class="lg:col-span-3 bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 overflow-hidden">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 gap-2">
            <h3 class="text-base sm:text-lg font-semibold text-gray-800 break-words">收支趋势</h3>
            <div class="flex gap-2 text-xs sm:text-sm text-gray-500 flex-wrap">
              <button class="hover:text-primary transition-colors">周</button>
              <button class="hover:text-primary transition-colors">月</button>
              <button class="hover:text-primary transition-colors">年</button>
            </div>
          </div>
          <div class="relative w-full min-h-[300px] sm:min-h-[350px] lg:min-h-[400px]">
            <div class="absolute inset-0">
              <TrendChart class="w-full h-full" />
            </div>
          </div>
        </div>

        <div class="lg:col-span-2 bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 overflow-hidden">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 gap-2">
            <h3 class="text-base sm:text-lg font-semibold text-gray-800 break-words">支出分类</h3>
            <button @click="$router.push('/transaction')"
                    class="text-xs sm:text-sm text-primary hover:text-primary-dark transition-colors whitespace-nowrap">
              去记账
            </button>
          </div>
          <div class="relative w-full min-h-[300px] sm:min-h-[350px] lg:min-h-[400px]">
            <div class="absolute inset-0">
              <CategoryChart class="w-full h-full" />
            </div>
          </div>
        </div>
      </div>


      <!-- 月度对比 -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4 mb-4 sm:mb-6 overflow-hidden">
        <h3 class="text-base sm:text-lg font-semibold text-gray-800 mb-3 break-words">月度对比</h3>
        <div class="relative w-full min-h-[300px] sm:min-h-[350px] lg:min-h-[400px]">
          <div class="absolute inset-0">
            <MonthlyChart class="w-full h-full" />
          </div>
        </div>
      </div>


      <!-- AI 报告 -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-card p-3 sm:p-4">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 gap-2">
          <h3 class="text-base sm:text-lg font-semibold text-gray-800 break-words">AI 智能分析</h3>
          <button class="btn-primary text-sm sm:text-base px-3 sm:px-4 py-1.5 sm:py-2 w-full sm:w-auto">
            <i class="fa fa-magic mr-2"></i>生成分析
          </button>
        </div>

        <div v-if="aiReport"
             class="text-gray-800 text-sm sm:text-base whitespace-pre-wrap prose max-w-none break-words">
          {{ aiReport }}
        </div>
        <div v-else class="text-gray-500 text-sm sm:text-base break-words">点击右侧按钮，生成财务分析报告。</div>
      </div>
    </div>
  </div>
</template>

<script>
import TrendChart from '@/components/Charts/TrendChart.vue'
import CategoryChart from '@/components/Charts/CategoryChart.vue'
import MonthlyChart from '@/components/Charts/MonthlyChart.vue'
import { aiAnalysis } from '@/api/ai'

export default {
  name: 'Statistics',
  components: {TrendChart, CategoryChart, MonthlyChart},
  data() {
    return {
      totalIncome: 12580,
      totalExpense: 8340,
      aiReport: null
    }
  },
  methods: {
    async generateAI() {
      try {
        const payload = {
          totalIncome: this.totalIncome,
          totalExpense: this.totalExpense,
          balance: this.totalIncome - this.totalExpense
        }
        const res = await aiAnalysis(payload)
        let data = res.data
        try {
          if (typeof data === 'string') data = JSON.parse(data)
        } catch (e) {
        }
        this.aiReport = data?.report || data
      } catch (e) {
        alert('AI 调用失败：' + (e.response?.data || e.message))
      }
    }
  }
}
</script>

<style scoped>
.shadow-card {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.btn-primary {
  @apply bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors duration-200;
}
</style>
