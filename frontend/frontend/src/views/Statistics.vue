<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex">
        <div class="flex-1 lg:ml-64 min-w-0 py-8">
          <!-- Header -->
          <header class="mb-8 animate-fade-in">
            <h1 class="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-primary to-primary-dark bg-clip-text text-transparent">
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
                <i class="fas fa-calendar-alt text-primary"></i> 统计周期
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
                <i class="fas fa-clock text-gray-400"></i> 最近交易
              </h3>
              <button
                  class="text-sm transition-colors"
                  :class="allTransactions.length ? 'text-primary hover:text-primary-dark' : 'text-gray-300 cursor-not-allowed'"
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
                      {{
                        tx.type === 'INCOME'
                            ? getIncomeName(tx.categoryId)
                            : getConsumeName(tx.categoryId) || '未分类'
                      }} · {{ formatDate(tx.date) }}
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
                  <i class="fas fa-chart-line text-gray-400"></i> 收支趋势
                </h3>
              </div>
              <TrendChart :data="trendData" />
            </div>

            <div class="lg:col-span-2 bg-white rounded-xl shadow-sm p-5">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
                  <i class="fas fa-chart-pie text-gray-400"></i> 支出分类
                </h3>
              </div>
              <CategoryChart :data="categoryData" />
            </div>
          </div>

          <!-- AI 分析 -->
          <div class="bg-white rounded-xl shadow-sm p-5">
            <div class="flex justify-between items-center">
              <h3 class="text-sm font-semibold text-gray-800">AI 智能分析</h3>
              <el-button type="primary" size="small" @click="aiDialogVisible = true"> 打开分析 </el-button>
            </div>
            <div class="text-gray-400 text-center py-6 text-sm">
              AI 将基于当前统计周期生成智能财务分析
            </div>
          </div>

          <!-- AI 智能分析对话框 -->
          <el-dialog
              v-model="aiDialogVisible"
              width="620px"
              :close-on-click-modal="false"
              class="ai-dialog"
              aria-label="AI 财务分析对话窗口"
              append-to-body
          >
            <!-- ============ 头部区域 ============ -->
            <template #header>
              <div class="flex items-center justify-between w-full">
                <div class="flex items-center gap-2.5">
                  <div class="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center">
                    <i class="fas fa-robot text-primary text-lg" aria-hidden="true"></i>
                  </div>
                  <h3 class="text-lg font-semibold text-gray-800">AI 智能财务分析</h3>
                </div>
                <el-button
                    type="warning"
                    size="small"
                    @click="clearChatHistory"
                    :loading="clearingHistory"
                    class="ml-2"
                >
                  <i class="fas fa-history mr-1"></i>清空对话
                </el-button>
              </div>
            </template>

            <!-- ============ 聊天内容区域 ============ -->
            <div class="ai-chat-body max-h-[500px] overflow-y-auto pr-1">
              <!-- 空状态提示 -->
              <div v-if="!aiMessages.length" class="ai-empty py-8 text-center text-gray-500">
                <i class="fas fa-brain text-4xl mb-3 text-primary/70" aria-hidden="true"></i>
                <p class="text-sm">AI 将基于当前统计周期生成专业财务洞察与建议</p>
                <p class="text-xs mt-2 text-gray-400">
                  当前分析周期：{{ dateRange[0] }} 至 {{ dateRange[1] }}
                </p>
              </div>

              <!-- 消息列表 -->
              <div
                  v-for="(msg, index) in aiMessages"
                  :key="index"
                  class="message-item mb-4 flex"
                  :class="msg.role === 'user' ? 'justify-end' : 'justify-start'"
                  :aria-label="msg.role === 'ai' ? `AI 消息：${msg.content}` : `用户消息：${msg.content}`"
              >
                <!-- AI 消息（含头像） -->
                <template v-if="msg.role === 'ai'">
                  <div class="flex gap-3 max-w-[85%]">
                    <div
                        class="ai-avatar flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center"
                    >
                      <i class="fas fa-robot text-primary text-sm" aria-hidden="true"></i>
                    </div>
                    <div class="ai-bubble ai-bot rounded-xl shadow-sm">
                      <div class="ai-name text-xs text-primary font-medium mb-1">AI 财务助手</div>
                      <!-- 使用 vue-markdown-render 安全渲染 -->
                      <vue-markdown
                          :source="msg.content || ''"
                          :breaks="true"
                          :html="false"
                          class="ai-content markdown-body prose max-w-none"
                      />
                    </div>
                  </div>
                </template>

                <!-- 用户消息 -->
                <template v-else>
                  <div class="ai-bubble ai-user rounded-xl max-w-[70%] whitespace-pre-wrap break-words">
                    {{ msg.content }}
                  </div>
                </template>
              </div>

              <!-- 加载状态 -->
              <div v-if="aiLoading" class="ai-loading flex items-center py-2 text-gray-500">
                <span class="dot animate-bounce"></span>
                <span class="dot animate-bounce delay-100"></span>
                <span class="dot animate-bounce delay-200"></span>
                <span class="ml-2 text-sm">AI 正在深度分析中，请稍候...</span>
              </div>
            </div>

            <!-- ============ 底部输入区域 ============ -->
            <template #footer>
              <div class="ai-input-wrapper flex gap-2 items-end pt-2">
                <el-input
                    v-model="aiInput"
                    type="textarea"
                    :rows="2"
                    resize="none"
                    placeholder="输入您的问题（Enter 发送，Shift+Enter 换行）"
                    :disabled="aiLoading"
                    aria-label="AI 对话输入框"
                    class="flex-1"
                    @keydown.enter.exact.prevent="handleEnterSend"
                    @keydown.enter.shift.stop
                />
                <el-button
                    type="primary"
                    class="ai-send-btn flex-shrink-0 px-6"
                    :loading="aiLoading"
                    :disabled="!aiInput.trim() || aiLoading"
                    aria-label="发送消息"
                    @click="sendAI"
                >
                  <template #default>
                    <span v-if="!aiLoading">发送</span>
                    <i v-else class="fas fa-spinner fa-spin"></i>
                  </template>
                </el-button>
              </div>
            </template>
          </el-dialog>
        </div>
      </div>
    </div>

    <!-- 全部交易 Dialog -->
    <el-dialog v-model="dialogVisible" width="80%" :show-close="true" custom-class="transaction-dialog">
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
                  {{
                    tx.type === 'INCOME'
                        ? getIncomeName(tx.categoryId)
                        : getConsumeName(tx.categoryId) || '未分类'
                  }} · {{ formatDate(tx.date) }}
                </p>
              </div>
            </div>
            <div class="text-sm font-semibold" :class="tx.type === 'INCOME' ? 'text-green-600' : 'text-red-500'">
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
          <div class="text-sm text-gray-500">共 {{ total }} 条记录</div>
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
import { aiAnalysis, clearChatHistory } from '@/api/ai'
import { getSummary } from '@/api/stats'
import { getExpenseSummary, getIncomeSummary, getSpending, getTransactions } from '@/api/transaction.js'
// 移除 marked 和 DOMPurify 导入
// import { marked } from 'marked'
// import DOMPurify from 'dompurify'

// 新增：导入 vue-markdown-render
import Markdown from 'vue-markdown-render'

// 图标资源保持不变
import incomeIcon from '@/assets/total_income.png'
import expenseIcon from '@/assets/total_expenditures.png'
import balanceIcon from '@/assets/surplus.png'
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
    OverviewCard,
    // 注册为 vue-markdown 组件（模板中使用短横线命名）
    'vue-markdown': Markdown
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
      {label: '本周', value: 'week'},
      {label: '本月', value: 'month'},
      {label: '本季', value: 'quarter'},
      {label: '本年', value: 'year'}
    ]

    return {dialogVisible, currentPreset, dateShortcuts, datePresets, incomeIcon, expenseIcon, balanceIcon}
  },
  data() {
    return {
      aiDialogVisible: false,
      aiMessages: [],
      aiInput: '',
      totalIncome: 0,
      totalExpense: 0,
      aiReport: null,
      dateRange: [],
      allTransactions: [],
      loading: false,
      aiLoading: false,
      clearingHistory: false,
      trendData: [],
      categoryData: [],
      monthlyData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      allTxLoading: false,
      consume_grids: [
        {id: 1, image: eat, text: "三餐"},
        {id: 2, image: shopping, text: "购物"},
        {id: 3, image: sock, text: "零食"},
        {id: 4, image: fruit, text: "水果"},
        {id: 5, image: plane, text: "出行"},
        {id: 6, image: car, text: "修车"},
        {id: 7, image: education, text: "学习"},
        {id: 8, image: children, text: "小孩"},
        {id: 9, image: gift, text: "送礼"},
        {id: 10, image: pet, text: "宠物"}
      ],
      income_grids: [
        {id: 13, image: salary, text: "工资"},
        {id: 14, image: bonus, text: "奖金"},
        {id: 15, image: financing, text: "理财"},
        {id: 16, image: lifefee, text: "生活费"},
        {id: 17, image: vicejob, text: "兼职"},
        {id: 18, image: wipeout, text: "报销"},
        {id: 19, image: refund, text: "退款"},
        {id: 20, image: gift, text: "礼金"}
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
        this.aiMessages = [];
        this.fetchData()
      }
    },
    aiDialogVisible(newVal) {
      if (newVal && this.aiMessages.length === 0 && this.dateRange.length === 2) {
        this.sendInitialAnalysis();
      }
    }
  },
  methods: {
    handleEnterSend() {
      if (this.aiLoading) return
      if (!this.aiInput.trim()) return
      this.sendAI()
    },

    async sendInitialAnalysis() {
      const basePrompt = `请基于以下统计周期进行财务分析：${this.dateRange[0]} 至 ${this.dateRange[1]}\n请生成本周期的综合财务分析报告，包含收支结构、结余情况、风险提示和优化建议。`

      this.aiMessages.push({
        role: 'user',
        content: '生成财务分析报告'
      });

      this.aiLoading = true;

      try {
        const response = await aiAnalysis({
          message: basePrompt,
          period: {
            start: this.dateRange[0],
            end: this.dateRange[1]
          }
        });

        let aiReply = '';
        if (response.data && response.data.reply) {
          aiReply = response.data.reply;
        } else if (typeof response.data === 'string') {
          aiReply = response.data;
        } else if (response.reply) {
          aiReply = response.reply;
        } else {
          aiReply = '✅ 财务分析已完成，详情请查看上方统计图表';
        }

        this.aiMessages.push({
          role: 'ai',
          content: aiReply.trim()
        });
      } catch (error) {
        console.error('AI初始分析失败:', error);
        let errorMsg = '分析服务暂时不可用';

        if (error.response) {
          if (error.response.status === 401) {
            errorMsg = '请先登录账号';
            this.$router.push('/login');
          } else if (error.response.status === 500) {
            errorMsg = '服务器内部错误，请稍后重试';
          } else {
            errorMsg = error.response.data?.message || '请求失败';
          }
        } else if (error.message && error.message.includes('timeout')) {
          errorMsg = '请求超时，请检查网络连接';
        }

        this.aiMessages.push({
          role: 'ai',
          content: `⚠️ ${errorMsg}\n💡 建议：检查网络连接或稍后重试`
        });
      } finally {
        this.aiLoading = false;
      }
    },

    // ========== 已移除 renderSafeMarkdown 方法 ==========
    // 安全渲染由 vue-markdown-render 组件内部处理

    async clearChatHistory() {
      if (this.clearingHistory) return;

      this.$confirm('确定要清空当前对话历史吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        this.clearingHistory = true;

        try {
          const response = await clearChatHistory({
            start: this.dateRange[0],
            end: this.dateRange[1]
          });

          if (response.data.success) {
            this.aiMessages = [];
            this.$message.success('对话历史已清空');
          } else {
            throw new Error(response.data.error || '清除失败');
          }
        } catch (error) {
          console.error('清除对话历史失败:', error);
          let errorMsg = '清除对话历史失败';

          if (error.response && error.response.data && error.response.data.error) {
            errorMsg = error.response.data.error;
          } else if (error.message) {
            errorMsg = error.message;
          }

          this.$message.error(errorMsg);
        } finally {
          this.clearingHistory = false;
        }
      }).catch(() => {
        // 用户取消操作
      });
    },

    async sendAI() {
      if (this.aiLoading) return

      const basePrompt = `请基于以下统计周期进行财务分析：${this.dateRange[0]} 至 ${this.dateRange[1]}`
      const content = this.aiInput.trim() ? `${basePrompt}\n用户问题：${this.aiInput.trim()}` : `${basePrompt}\n请生成本周期的综合财务分析报告，包含收支结构、结余情况、风险提示和优化建议。`

      this.aiMessages.push({
        role: 'user',
        content: this.aiInput.trim() || '生成财务分析报告'
      })

      const userMessage = this.aiInput.trim();
      this.aiInput = ''
      this.aiLoading = true

      try {
        const response = await aiAnalysis({
          message: content,
          period: {
            start: this.dateRange[0],
            end: this.dateRange[1]
          }
        })

        let aiReply = ''
        if (response.data && response.data.reply) {
          aiReply = response.data.reply
        } else if (typeof response.data === 'string') {
          aiReply = response.data
        } else {
          aiReply = '✅ 财务分析已完成，详情请查看上方统计图表'
        }

        this.aiMessages.push({
          role: 'ai',
          content: aiReply.trim()
        })
      } catch (error) {
        console.error('AI分析请求失败:', error)
        let errorMsg = '分析服务暂时不可用'

        if (error.response) {
          if (error.response.status === 401) {
            errorMsg = '请先登录账号'
            this.$router.push('/login')
          } else if (error.response.status === 500) {
            errorMsg = '服务器内部错误，请稍后重试'
          } else {
            errorMsg = error.response.data?.message || '请求失败'
          }
        } else if (error.message && error.message.includes('timeout')) {
          errorMsg = '请求超时，请检查网络连接'
        }

        this.aiMessages.push({
          role: 'ai',
          content: `⚠️ ${errorMsg}\n💡 建议：检查网络连接或稍后重试`
        })

        this.$message.error(`AI分析失败：${errorMsg}`)
      } finally {
        this.aiLoading = false
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      if (typeof dateStr === 'string') {
        return dateStr.slice(0, 10)
      }
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
        return item ? item.image : salary;
      } else {
        const item = this.consume_grids.find(c => String(c.id) === String(categoryId));
        return item ? item.image : eat;
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

        this.allTransactions = Array.isArray(response.data?.records) ? response.data.records : []
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

      switch (type) {
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
      if (dates && dates.length === 2) {
        this.dateRange = dates
      }
    },

    async getIncomeSum() {
      try {
        await getIncomeSummary({
          start: this.dateRange[0],
          end: this.dateRange[1],
        })
      } catch (error) {
        console.error('获取收入汇总失败:', error)
        this.$message?.error('获取收入汇总失败')
      }
    },

    async getExpenseSum() {
      try {
        const res = await getExpenseSummary({
          start: this.dateRange[0],
          end: this.dateRange[1],
        })
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

        const rawData = Array.isArray(res.data) ? res.data : []
        this.categoryData = rawData.map(item => ({
          ...item,
          name: this.getConsumeName(item.categoryId)
        }))
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
.ai-dialog {
  border-radius: 16px;
}

/* Markdown 内容样式优化 */
.ai-content {
  line-height: 1.6;
  color: #334155;
  font-size: 0.95rem;
  word-wrap: break-word;
}

/* 深度选择器覆盖 vue-markdown-render 内部元素 */
.ai-content :deep(h1),
.ai-content :deep(h2),
.ai-content :deep(h3),
.ai-content :deep(h4),
.ai-content :deep(h5),
.ai-content :deep(h6) {
  margin-top: 1.2em;
  margin-bottom: 0.6em;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.3;
}

.ai-content :deep(h1) { font-size: 1.5rem; }
.ai-content :deep(h2) { font-size: 1.25rem; }
.ai-content :deep(h3) { font-size: 1.125rem; }

.ai-content :deep(p) {
  margin: 0.8em 0;
  color: #475569;
}

.ai-content :deep(ul),
.ai-content :deep(ol) {
  padding-left: 1.8em;
  margin: 0.8em 0;
  color: #475569;
}

.ai-content :deep(li) {
  margin: 0.4em 0;
  line-height: 1.6;
}

.ai-content :deep(a) {
  color: #3b82f6;
  text-decoration: underline;
  transition: opacity 0.2s;
  word-break: break-word;
}

.ai-content :deep(a:hover) {
  opacity: 0.85;
}

.ai-content :deep(code) {
  background-color: #f1f5f9;
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 0.9em;
  color: #0f172a;
}

.ai-content :deep(pre) {
  background-color: #f8fafc;
  border-left: 3px solid #3b82f6;
  padding: 1.2em;
  border-radius: 0 6px 6px 0;
  overflow-x: auto;
  margin: 1.2em 0;
  font-size: 0.9rem;
  line-height: 1.5;
}

.ai-content :deep(pre code) {
  background: none;
  padding: 0;
  border-radius: 0;
  font-size: inherit;
  color: inherit;
}

.ai-content :deep(blockquote) {
  border-left: 3px solid #cbd5e1;
  padding-left: 1.2em;
  margin: 1.2em 0;
  color: #475569;
  font-style: italic;
  background-color: #f8fafc;
  border-radius: 0 4px 4px 0;
}

.ai-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1.2em 0;
  font-size: 0.95em;
}

.ai-content :deep(th),
.ai-content :deep(td) {
  border: 1px solid #e2e8f0;
  padding: 0.6em 0.8em;
  text-align: left;
}

.ai-content :deep(th) {
  background-color: #f1f5f9;
  font-weight: 600;
}

.ai-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 0.8em 0;
}

.ai-content :deep(hr) {
  border: 0;
  border-top: 1px solid #e2e8f0;
  margin: 1.5em 0;
}

/* 保留原有动画样式 */
.ai-loading .dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background-color: #94a3b8;
  border-radius: 50%;
  margin-right: 4px;
}

.ai-loading .dot:nth-child(2) {
  animation-delay: 0.1s;
}

.ai-loading .dot:nth-child(3) {
  animation-delay: 0.2s;
}

/* 其他原有样式保持不变 */
.message-item {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.ai-bubble.ai-bot {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
}

.ai-bubble.ai-user {
  background-color: #dbeafe;
  color: #0c4a6e;
  border: 1px solid #bfdbfe;
}

/* 保留其他必要样式 */
</style>