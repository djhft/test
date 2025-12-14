<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-6">
        <h1 class="text-xl sm:text-2xl font-bold text-gray-800 mb-2">预算管理</h1>
        <p class="text-sm sm:text-base text-gray-500">设置与监控您的月度预算</p>
      </header>

      <!-- ================= 添加预算（完全未修改） ================= -->
      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6 mb-6 transition-all duration-300 hover:shadow-lg">
        <!-- 分类选择 -->
        <div class="mb-6">
          <label class="text-sm text-gray-600 mb-3 block flex items-center">
            <i class="fas fa-tag mr-2 text-blue-500"></i>选择分类
          </label>
          <div class="grid grid-cols-4 sm:grid-cols-5 gap-3">
            <div v-for="item in consume_grids"
                 :key="item.id"
                 @click="selectCategory(item)"
                 :class="['cursor-pointer rounded-xl p-3 flex flex-col items-center transition-all duration-200',
                          newBudget.category === item.text
                            ? 'bg-gradient-to-br from-blue-50 to-blue-50 border-2 border-blue-300 shadow-md transform scale-105'
                            : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent hover:shadow-sm',
                          showError && !newBudget.category ? 'border-red-500 animate-pulse' : '']">
              <img :src="item.image" :alt="item.text" class="w-8 h-8 mb-1" />
              <span class="text-xs text-gray-700">{{ item.text }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-600 mb-2 block flex items-center">
              <i class="fas fa-yen-sign mr-2 text-blue-500"></i>预算金额
            </label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
              <input
                  v-model.number="newBudget.amount"
                  type="number"
                  class="input-field pl-10 rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  :class="showError && !newBudget.amount ? 'border-red-500 animate-pulse' : ''"
                  placeholder="请输入预算金额"
              />
            </div>
          </div>
          <div>
            <label class="text-sm text-gray-600 mb-2 block flex items-center">
              <i class="fas fa-calendar mr-2 text-blue-500"></i>月份
            </label>
            <input
                v-model="newBudget.month"
                type="month"
                class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                :class="showError && !newBudget.month ? 'border-red-500 animate-pulse' : ''"
            />
          </div>
        </div>

        <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-600 mb-2 block flex items-center">
              <i class="fas fa-exclamation-triangle mr-2 text-blue-500"></i>预警阈值
            </label>
            <select
                v-model.number="newBudget.alert"
                class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <option :value="80">80% - 温馨提醒</option>
              <option :value="90">90% - 严重警告</option>
              <option :value="100">100% - 超支提醒</option>
            </select>
          </div>
        </div>

        <div class="mt-6 flex gap-3">
          <button
              class="btn-primary flex-1 py-3 text-base font-medium rounded-xl"
              @click="addBudget"
          >
            <i class="fas fa-save mr-2"></i>保存预算
          </button>
          <button
              class="btn-secondary py-3 px-6 text-base font-medium rounded-xl"
              @click="clearBudget"
          >
            <i class="fas fa-redo mr-2"></i>重置
          </button>
        </div>
      </div>

      <!-- ================= 当前预算（仅这里有改动） ================= -->
      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6 transition-all duration-300 hover:shadow-lg">
        <h3 class="text-lg font-semibold text-gray-800 mb-4 flex items-center">
          <i class="fas fa-list mr-2 text-blue-500"></i>当前预算
        </h3>

        <div class="space-y-4">
          <div v-for="(b, i) in budgets" :key="i"
               class="border border-gray-100 rounded-xl p-4 transition-all duration-200 hover:shadow-md hover:border-blue-200">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center">
                <img :src="b.image" :alt="b.category" class="w-8 h-8 mr-3" />
                <div>
                  <div class="text-base text-gray-800 font-medium">{{ b.category }}</div>
                  <div class="text-sm text-gray-500">
                    <span class="mr-2">预算 ¥{{ b.amount.toLocaleString() }}</span>
                    <span>已用 ¥{{ b.used.toLocaleString() }}</span>
                  </div>
                </div>
              </div>

              <!-- Element Plus 操作按钮 -->
              <div class="flex gap-2">
                <el-button type="primary" link size="small"
                           @click="openEditDialog(b, i)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small"
                           @click="deleteBudget(i)">
                  删除
                </el-button>
              </div>
            </div>

            <div class="relative">
              <div class="w-full bg-gray-200 h-3 rounded-full overflow-hidden">
                <div
                    class="h-3 rounded-full transition-all duration-500 ease-out"
                    :class="[
                    b.used / b.amount >= 1 ? 'bg-red-500' :
                    b.used / b.amount >= 0.8 ? 'bg-yellow-500' :
                    'bg-green-500'
                  ]"
                    :style="{ width: Math.min(b.used / b.amount * 100, 100) + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <div v-if="!budgets.length" class="text-center text-gray-400 py-12">
            暂无预算
          </div>
        </div>
      </div>
    </div>

    <!-- ================= 编辑弹窗（Element Plus） ================= -->
    <el-dialog
        v-model="editDialogVisible"
        title="修改预算金额"
        width="360px"
    >
      <div class="space-y-4">
        <div class="text-sm text-gray-600">
          分类：<span class="font-medium">{{ editForm.category }}</span>
        </div>

        <el-input
            v-model.number="editForm.amount"
            type="number"
            placeholder="请输入新的预算金额"
        >
          <template #prefix>¥</template>
        </el-input>
      </div>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
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

export default {
  name: 'Budget',
  data() {
    const now = new Date()
    return {
      showError: false,
      newBudget: {
        category: '',
        amount: 0,
        month: now.toISOString().slice(0,7),
        alert: 80
      },
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
      budgets: [
        {category: '餐饮', amount: 2000, used: 1560, image: eat},
        {category: '购物', amount: 3000, used: 2850, image: shopping}
      ],

      /* ===== 新增（仅用于当前预算编辑） ===== */
      editDialogVisible: false,
      editIndex: null,
      editForm: {
        category: '',
        amount: 0
      }
    }
  },
  methods: {
    selectCategory(item) {
      this.newBudget.category = item.text;
      this.showError = false;
    },

    addBudget() {
      this.showError = true
      if (!this.newBudget.category || !this.newBudget.amount || !this.newBudget.month) {
        ElMessage.warning('请填写完整信息')
        return
      }

      const selectedCategory = this.consume_grids.find(
          item => item.text === this.newBudget.category
      )

      this.budgets.push({
        category: this.newBudget.category,
        amount: this.newBudget.amount,
        used: 0,
        image: selectedCategory.image
      })

      ElMessage.success('预算已保存')
      this.clearBudget()
    },

    clearBudget() {
      const now = new Date()
      this.newBudget = {
        category: '',
        amount: 0,
        month: now.toISOString().slice(0,7),
        alert: 80
      }
      this.showError = false
    },

    /* ===== 当前预算相关方法 ===== */
    openEditDialog(budget, index) {
      this.editIndex = index
      this.editForm.category = budget.category
      this.editForm.amount = budget.amount
      this.editDialogVisible = true
    },

    confirmEdit() {
      if (!this.editForm.amount) {
        ElMessage.warning('请输入预算金额')
        return
      }
      this.budgets[this.editIndex].amount = this.editForm.amount
      this.editDialogVisible = false
      ElMessage.success('预算已更新')
    },

    deleteBudget(index) {
      this.budgets.splice(index, 1)
      ElMessage.success('预算已删除')
    }
  }
}
</script>

<style scoped>
.input-field {
  @apply w-full px-4 py-2 border border-gray-300 focus:outline-none focus:border-transparent;
}
.btn-primary {
  @apply bg-blue-500 text-white hover:bg-blue-600;
}
.btn-secondary {
  @apply bg-gray-100 text-gray-700 hover:bg-gray-200;
}
.shadow-card {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
</style>
