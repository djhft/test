<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex">
        <div class="flex-1 lg:ml-64 min-w-0 py-8">
          <!-- Header -->
          <header class="mb-8 animate-fade-in">
            <h1
                class="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-primary to-primary-dark bg-clip-text text-transparent"
            >
              统计分析
            </h1>
            <p class="text-sm text-gray-500 mt-1">
              多维度分析您的财务状况
            </p>
          </header>

          <!-- 统计周期 -->
          <div class="bg-white rounded-xl shadow-sm p-5 mb-8">
            <div class="flex flex-col gap-4">
              <div class="flex items-center gap-2 text-gray-700 text-sm font-medium">
                <i class="fas fa-calendar-alt text-primary"></i>
                统计周期
              </div>

              <div class="flex flex-col lg:flex-row lg:items-center gap-4">
                <el-date-picker
                    v-model="dateRange"
                    type="daterange"
                    unlink-panels
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    :shortcuts="dateShortcuts"
                    class="w-full lg:max-w-md"
                    @change="onDateRangeChange"
                />

                <div class="grid grid-cols-2 sm:flex gap-2">
                  <el-button
                      v-for="preset in datePresets"
                      :key="preset.value"
                      size="small"
                      :type="currentPreset === preset.value ? 'primary' : 'default'"
                      @click="selectPreset(preset.value)"
                  >
                    {{ preset.label }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 概览卡片 -->
          <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6 mb-8">
            <OverviewCard title="总收入" color="green" :value="totalIncome" :icon="incomeIcon" />
            <OverviewCard title="总支出" color="red" :value="totalExpense" :icon="expenseIcon" />
            <OverviewCard title="结余" color="blue" :value="balance" :icon="balanceIcon" />
          </div>

          <!-- 最近交易 -->
          <div class="bg-white rounded-xl shadow-sm p-5 mb-8">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
                <i class="fas fa-clock text-gray-400"></i>
                最近交易
              </h3>
              <button
                  class="text-sm transition-colors"
                  :class="allTransactions.length
          ? 'text-primary hover:text-primary-dark'
          : 'text-gray-300 cursor-not-allowed'"
                  :disabled="!allTransactions.length"
                  @click="allTransactions.length && (dialogVisible = true)"
              >
                查看全部
              </button>
            </div>

            <div v-if="allTransactions.length" class="divide-y divide-gray-100">
              <div
                  v-for="tx in allTransactions.slice(0, 4)"
                  :key="tx.id"
                  class="flex items-center justify-between py-3 hover:bg-gray-50 rounded-lg px-2 transition"
              >
                <div class="flex items-center gap-3">
                  <!-- 添加图标显示 -->
                  <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
                    <img
                        :src="getCategoryIcon(tx.type, tx.categoryId)"
                        :alt="tx.type === 'INCOME' ? '收入' : '支出'"
                        class="w-6 h-6"
                    />
                  </div>
                  <div>
                    <p class="text-sm font-medium text-gray-800">
                      {{ tx.type === 'INCOME'
                        ? getIncomeName(tx.categoryId)
                        : getConsumeName(tx.categoryId) || '未分类'
                      }}
                      · {{ formatDate(tx.date) }}
                    </p>
                    <p class="text-xs text-gray-500 mt-1">
                      {{ tx.description || (tx.type === 'INCOME' ? '收入' : '支出') }}

                    </p>
                  </div>
                </div>
                <div
                    class="text-sm font-semibold"
                    :class="tx.type === 'INCOME' ? 'text-green-600' : 'text-red-500'"
                >
                  {{ tx.type === 'INCOME' ? '+' : '-' }}¥{{ formatAmount(tx.amount) }}
                </div>
              </div>
            </div>

            <div v-else class="text-center text-gray-400 py-10 text-sm">
              暂无交易记录
            </div>
          </div>

          <!-- 图表 -->
          <div class="grid grid-cols-1 lg:grid-cols-5 gap-6 mb-8">
            <div class="lg:col-span-3 bg-white rounded-xl shadow-sm p-5">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
                  <i class="fas fa-chart-line text-gray-400"></i>
                  收支趋势
                </h3>
              </div>
              <TrendChart :data="trendData" />
            </div>

            <div class="lg:col-span-2 bg-white rounded-xl shadow-sm p-5">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
                  <i class="fas fa-chart-pie text-gray-400"></i>
                  支出分类
                </h3>
              </div>
              <!-- 这里是关键修复 -->
              <CategoryChart :data="categoryData" />
            </div>
          </div>

          <!--          <div class="bg-white rounded-xl shadow-sm p-5 mb-8">-->
          <!--            <div class="flex items-center justify-between mb-4">-->
          <!--              <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">-->
          <!--                <i class="fas fa-chart-bar text-gray-400"></i>-->
          <!--                月度统计-->
          <!--              </h3>-->
          <!--            </div>-->
          <!--            <MonthlyChart :data="monthlyData" />-->

          <!--          </div>-->


          <!-- AI 分析 -->
          <div class="bg-white rounded-xl shadow-sm p-5">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-sm font-semibold text-gray-800">AI 智能分析</h3>
              <el-button type="primary" size="small" @click="generateAI" :loading="aiLoading">
                生成分析
              </el-button>
            </div>

            <div v-if="aiReport" class="bg-gray-50 rounded-lg p-4 text-sm leading-relaxed text-gray-700">
              {{ aiReport }}
            </div>
            <div v-else class="text-gray-400 text-center py-6 text-sm">
              点击按钮生成分析报告
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 全部交易 Dialog -->
    <el-dialog
        v-model="dialogVisible"
        width="80%"
        :show-close="true"
        custom-class="transaction-dialog"
    >
      <template #header>
        <div class="flex items-center gap-2">
          <i class="fas fa-list-alt text-primary text-xl"></i>
          <span class="text-lg font-semibold">全部交易记录</span>
        </div>
      </template>

      <div v-loading="allTxLoading" class="max-h-96 overflow-y-auto px-2">
        <div v-if="allTransactions.length" class="divide-y divide-gray-100">
          <div
              v-for="tx in allTransactions"
              :key="tx.id"
              class="flex items-center justify-between py-3 hover:bg-gray-50 rounded-lg px-2 transition-all"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
                <img
                    :src="getCategoryIcon(tx.type, tx.categoryId)"
                    :alt="tx.type === 'INCOME' ? '收入' : '支出'"
                    class="w-6 h-6"
                />
              </div>
              <div>
                <p class="text-sm font-medium text-gray-800">
                  {{ tx.description || (tx.type === 'INCOME' ? '收入' : '支出') }}
                </p>
                <p class="text-xs text-gray-500 mt-1">
                  {{ tx.type === 'INCOME'
                    ? getIncomeName(tx.categoryId)
                    : getConsumeName(tx.categoryId) || '未分类'
                  }}
                  · {{ formatDate(tx.date) }}
                </p>
              </div>
            </div>
            <div
                class="text-sm font-semibold"
                :class="tx.type === 'INCOME' ? 'text-green-600' : 'text-red-500'"
            >
              {{ tx.type === 'INCOME' ? '+' : '-' }}¥{{ formatAmount(tx.amount) }}
            </div>
          </div>
        </div>
        <div v-else class="text-center text-gray-400 py-6 text-sm">
          <i class="fas fa-inbox text-4xl mb-2"></i>
          <p>暂无交易记录</p>
        </div>
      </div>

      <template #footer>
        <div class="flex justify-between items-center">
          <div class="text-sm text-gray-500">
            共 {{ total }} 条记录
          </div>
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="mt-4"
          />
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from 'vue'
import TrendChart from '@/components/Charts/TrendChart.vue'
import CategoryChart from '@/components/Charts/CategoryChart.vue'
import MonthlyChart from '@/components/Charts/MonthlyChart.vue'
import OverviewCard from '@/components/OverviewCard.vue'
import { aiAnalysis } from '@/api/ai'
import { getSummary } from '@/api/stats'
import { getExpenseSummary, getIncomeSummary, getSpending, getTransactions } from '@/api/transaction.js'

import incomeIcon from '@/assets/total_income.png'
import expenseIcon from '@/assets/total_expenditures.png'
import balanceIcon from '@/assets/surplus.png'

/* 你原来的所有 icon import 全部保留 */
import eat from "@/assets/icon/eat.png";
import shopping from "@/assets/icon/shopping.png";
import sock from "@/assets/icon/sock.png";
import fruit from "@/assets/icon/fruit.png";
import plane from "@/assets/icon/plane.png";
import car from "@/assets/icon/car.png";
import education from "@/assets/icon/education.png";
import children from "@/assets/icon/children.png";
import gift from "@/assets/icon/gift.png";
import pet from "@/assets/icon/pet.png";
import salary from "@/assets/icon/salary.png";
import bonus from "@/assets/icon/bonus.png";
import financing from "@/assets/icon/financing.png";
import lifefee from "@/assets/icon/lifefee.png";
import vicejob from "@/assets/icon/vicejob.png";
import wipeout from "@/assets/icon/wipeout.png";
import refund from "@/assets/icon/refund.png";

export default {
  name: 'Statistics',
  components: {
    TrendChart,
    CategoryChart,
    MonthlyChart,
    OverviewCard
  },

  setup() {
    const dialogVisible = ref(false)
    const currentPreset = ref('month')

    const dateShortcuts = [
      {
        text: '最近一周',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setDate(end.getDate() - 7)
          return [start, end]
        }
      },
      {
        text: '最近一个月',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setMonth(end.getMonth() - 1)
          return [start, end]
        }
      },
      {
        text: '最近三个月',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setMonth(end.getMonth() - 3)
          return [start, end]
        }
      }
    ]

    const datePresets = [
      { label: '本周', value: 'week' },
      { label: '本月', value: 'month' },
      { label: '本季', value: 'quarter' },
      { label: '本年', value: 'year' }
    ]

    return {
      dialogVisible,
      currentPreset,
      dateShortcuts,
      datePresets,
      incomeIcon,
      expenseIcon,
      balanceIcon
    }
  },

  data() {
    return {
      totalIncome: 0,
      totalExpense: 0,
      aiReport: null,
      dateRange: [],
      allTransactions: [],
      loading: false,
      aiLoading: false,
      trendData: [],
      categoryData: [],
      monthlyData: [],
      consume_grids: [
        { id: 1, image: eat, text: "三餐" },
        { id: 2, image: shopping, text: "购物" },
        { id: 3, image: sock, text: "零食" },
        { id: 4, image: fruit, text: "水果" },
        { id: 5, image: plane, text: "出行" },
        { id: 6, image: car, text: "修车" },
        { id: 7, image: education, text: "学习" },
        { id: 8, image: children, text: "小孩" },
        { id: 9, image: gift, text: "送礼" },
        { id: 10, image: pet, text: "宠物" }
      ],
      income_grids: [
        { id: 13, image: salary, text: "工资" },
        { id: 14, image: bonus, text: "奖金" },
        { id: 15, image: financing, text: "理财" },
        { id: 16, image: lifefee, text: "生活费" },
        { id: 17, image: vicejob, text: "兼职" },
        { id: 18, image: wipeout, text: "报销" },
        { id: 19, image: refund, text: "退款" },
        { id: 20, image: gift, text: "礼金" }
      ]
    }
  },

  computed: {
    balance() {
      return this.totalIncome - this.totalExpense
    }
  },

  watch: {
    dateRange(newVal) {
      if (newVal && newVal.length === 2) {
        this.fetchData()
      }
    }
  },

  methods: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      // 如果是日期字符串，直接返回前面10位
      if (typeof dateStr === 'string') {
        return dateStr.slice(0, 10)
      }
      // 如果是Date对象，格式化
      const date = new Date(dateStr)
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },

    getConsumeName(id) {
      const item = this.consume_grids.find(c => String(c.id) === String(id));
      return item ? item.text : '未分类';
    },

    getIncomeName(id) {
      const item = this.income_grids.find(c => String(c.id) === String(id));
      return item ? item.text : '未分类';
    },

    getCategoryIcon(type, categoryId) {
      if (type === 'INCOME') {
        const item = this.income_grids.find(c => String(c.id) === String(categoryId));
        return item ? item.image : salary; // 默认使用工资图标
      } else {
        const item = this.consume_grids.find(c => String(c.id) === String(categoryId));
        return item ? item.image : eat; // 默认使用三餐图标
      }
    },

    formatAmount(amount) {
      if (!amount) return '0.00'
      return parseFloat(amount).toFixed(2)
    },

    async fetchData() {
      if (this.loading) return
      this.loading = true

      try {
        const summaryResponse = await getSummary({
          start: this.dateRange[0],
          end: this.dateRange[1]
        })

        this.totalIncome = summaryResponse.data?.totalIncome || 0
        this.totalExpense = summaryResponse.data?.totalExpense || 0
        this.trendData = summaryResponse.data?.trendData || []
        this.monthlyData = summaryResponse.data?.monthlyData || []

        // 🔴 统一在这里调用
        await this.getSpend()
        await this.getExpenseSum()
        await this.fetchAllTransactions()

      } finally {
        this.loading = false
      }
    },

    async fetchAllTransactions() {
      this.allTxLoading = true
      try {
        const response = await getTransactions({
          start: this.dateRange[0],
          end: this.dateRange[1],
          page: this.currentPage,
          pageSize: this.pageSize
        })

        this.allTransactions = Array.isArray(response.data?.records)
            ? response.data.records
            : []
        this.total = response.data?.total || 0
      } catch (error) {
        console.error('获取全部交易失败:', error)
        this.$message?.error('获取全部交易失败')
      } finally {
        this.allTxLoading = false
      }
    },

    selectPreset(type) {
      this.currentPreset = type
      const end = new Date()
      const start = new Date()

      switch(type) {
        case 'week':
          start.setDate(end.getDate() - 7)
          break
        case 'month':
          start.setDate(1)
          break
        case 'quarter':
          const currentMonth = end.getMonth()
          start.setMonth(Math.floor(currentMonth / 3) * 3, 1)
          break
        case 'year':
          start.setMonth(0, 1)
          break
      }

      this.dateRange = [this.format(start), this.format(end)]
    },

    format(d) {
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },

    onDateRangeChange() {
      this.currentPreset = ''
    },

    onCalendarChange(dates) {
      // 日历选择变化时更新日期范围
      if (dates && dates.length === 2) {
        this.dateRange = dates
      }
    },

    async generateAI() {
      if (!this.totalIncome && !this.totalExpense) {
        this.$message?.warning('暂无数据可供分析')
        return
      }

      this.aiLoading = true
      try {
        const res = await aiAnalysis({
          totalIncome: this.totalIncome,
          totalExpense: this.totalExpense,
          balance: this.balance,
          dateRange: this.dateRange
        })
        this.aiReport = res.data?.report || res.data
        this.$message?.success('AI分析生成成功')
      } catch (error) {
        console.error('生成AI分析失败:', error)
        this.$message?.error('生成分析报告失败')
      } finally {
        this.aiLoading = false
      }
    },

    async getIncomeSum()
    {
      try{
        new Date();
        const res = await getIncomeSummary(
            {
              start: this.dateRange[0],
              end: this.dateRange[1],
            },

        )
      } catch (error) {
        console.error('获取收入汇总失败:', error)
        this.$message?.error('获取收入汇总失败')
      }
    },

    async getExpenseSum() {
      try{
        const res = await getExpenseSummary(
            {
              start: this.dateRange[0],
              end: this.dateRange[1],
            },


        )
        this.trendData = res.data
      } catch (error) {
        console.error('获取支出汇总失败:', error)
        this.$message?.error('获取支出汇总失败')
      }
    },

    async getSpend() {
      try {
        const res = await getSpending({
          start: this.dateRange[0],
          end: this.dateRange[1],
        })

        // 通常 res.data 才是你要的数组
        const rawData = Array.isArray(res.data) ? res.data : []

        this.categoryData = rawData.map(item => ({
          ...item,
          name: this.getConsumeName(item.categoryId)
        }))
        // const labels = []
        // const data = []
        //
        // res.data.forEach(item => {
        //   if (!item || !item.categoryId) return
        //   labels.push(this.getConsumeName(item.categoryId))
        //   data.push(Number(item.totalAmount || 0))
        // })
        //
        // // 关键：整体替换，而不是 push
        // this.categoryData = {
        //   labels,
        //   data
        // }

      } catch (error) {
        console.error(error)
      }
    },


    handleSizeChange(val) {
      this.pageSize = val
      this.fetchAllTransactions()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchAllTransactions()
    },
  },
  created() {
    const start = new Date()
    start.setDate(1)
    const end = new Date()
    this.dateRange = [this.format(start), this.format(end)]
  }
}
</script>

<style scoped>
.animate-fade-in {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-dialog__body) {
  padding: 0 20px 20px;
}

:deep(.el-pager li.active) {
  background-color: #409eff;
  color: #fff;
}

:deep(.transaction-dialog) {
  border-radius: 12px;
}

:deep(.transaction-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.transaction-dialog .el-dialog__body) {
  padding: 20px 24px;
}

:deep(.transaction-dialog .el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

:deep(.el-pagination) {
  --el-pagination-button-bg-color: #f5f7fa;
  --el-pagination-hover-color: #409eff;
}

:deep(.el-pagination .el-pager li) {
  border-radius: 4px;
  margin: 0 2px;
  transition: all 0.3s;
}

:deep(.el-pagination .el-pager li:hover) {
  transform: translateY(-1px);
}

:deep(.el-pagination .el-pager li.active) {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  border-radius: 4px;
  transition: all 0.3s;
}

:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover) {
  transform: translateY(-1px);
}

:deep(.el-pagination .el-select .el-input) {
  width: 110px;
}

:deep(.el-pagination .el-select .el-input .el-input__wrapper) {
  border-radius: 4px;
}
</style>