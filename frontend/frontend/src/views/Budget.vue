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
                    newBudget.category === item.image
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
    <!-- 编辑弹窗部分 -->
    <el-dialog
        v-model="editDialogVisible"
        title="修改预算设置"
        width="400px"
    >
      <div class="space-y-4">
        <div class="text-sm text-gray-600">
          分类：<span class="font-medium">{{ editForm.category }}</span>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">预算金额</label>
          <el-input
              v-model.number="editForm.amount"
              type="number"
              placeholder="请输入新的预算金额"
          >
            <template #prefix>¥</template>
          </el-input>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">预警阈值</label>
          <el-select v-model.number="editForm.alert" class="w-full">
            <el-option :value="80" label="80% - 温馨提醒" />
            <el-option :value="90" label="90% - 严重警告" />
            <el-option :value="100" label="100% - 超支提醒" />
          </el-select>
        </div>
      </div>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit(b)">确认</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import {ElMessage} from 'element-plus'
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
import {createBudget, deleteBudget, getBudgets, updateBudget} from "@/api/budgets.js";

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
        { id: 1,value:'eat', image: eat, text: "三餐" },
        { id: 2,value:'shopping', image: shopping, text: "购物" },
        { id: 3,value:'sock', image: sock, text: "零食" },
        { id: 4,value:'fruit', image: fruit, text: "水果" },
        { id: 5,value:'plane', image: plane, text: "出行" },
        { id: 6,value:'car', image: car, text: "修车" },
        { id: 7,value:'education', image: education, text: "学习" },
        { id: 8,value:'children', image: children, text: "小孩" },
        { id: 9,value:'gift', image: gift, text: "送礼" },
        { id: 10,value: 'pet', image: pet, text: "宠物" }
      ],
      budgets: [
        {category: '餐饮', amount: 2000, used: 1560, image: 'eat'},
        {category: '购物', amount: 3000, used: 2850, image: 'shopping'},
      ],

      /* ===== 新增（仅用于当前预算编辑） ===== */
      editDialogVisible: false,
      editIndex: null,
// 在 data 中更新 editForm 的结构
      editForm: {
        category: '',
        amount: 0,
        alert: 80  // 添加预警阈值
      },
    }
  },
  created() {
    this.getAllBudgets()
  },
  methods: {
    selectCategory(item) {
      this.newBudget.category = item.image;
      this.showError = false;
    },

    async getAllBudgets() {
      try {
        const request = await getBudgets();
        for (let i = 0; i < request.data.length; i++) {
          request.data[i].category = this.getConsumeNameById(request.data[i].categoryId)
          request.data[i].image = this.getConsumeImage(request.data[i].categoryId).image
          console.log(request.data[i].image)

        }
        this.budgets = request.data
        console.log(request)
      } catch (error) {
        console.error(error)
      }
    },
    getConsumeImage(category) {
      return this.consume_grids.find(c => String(c.id) === String(category))
    },

    getConsumeNameById(id) {
      const cat = this.consume_grids.find(c => c.id === id)
      return cat ? cat.text : '其他'
    },

    getConsumeIdByImage(image) {
      const cat = this.consume_grids.find(c => c.image === image)
      return cat ? cat.id : 1
    },

    async addBudget() {
      this.showError = true
      if (!this.newBudget.category || !this.newBudget.amount || !this.newBudget.month) {
        ElMessage.warning('请填写完整信息')
        return
      }

      // 检查是否已存在相同分类的预算
      const existingBudget = this.budgets.find(budget => {
        const selectedCategory = this.consume_grids.find(
            item => item.image === this.newBudget.category
        )
        return budget.categoryId === this.getConsumeIdByImage(this.newBudget.category)
      })

      if (existingBudget) {
        ElMessage.warning('该分类已存在预算，请勿重复添加')
        return
      }

      try {
        const budgetData = {
          categoryId: this.getConsumeIdByImage(this.newBudget.category),
          amount: this.newBudget.amount,
          periodStart: this.newBudget.month + '-01',
          periodEnd: this.getMonthLastDay(this.newBudget.month),
          alert: this.newBudget.alert,
          used: 0
        }
        console.log(budgetData)
        await createBudget(budgetData)
        ElMessage.success('预算已保存')
        this.clearBudget()
        await this.getAllBudgets() // 重新获取列表
      } catch (error) {
        console.error('创建预算失败:', error)
        ElMessage.error('创建预算失败：' + (error.response?.data || error.message))
      }
    }
    ,
    getMonthLastDay(yearMonth) {
      const [year, month] = yearMonth.split('-');
      // 创建下个月的第一天，然后减去一天，就是这个月的最后一天
      const lastDay = new Date(year, month, 0).getDate();
      return `${yearMonth}-${lastDay.toString().padStart(2, '0')}`;
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
      this.editForm.alert = budget.alert || 80  // 设置默认值
      this.editDialogVisible = true
    },

    async confirmEdit() {
      if (!this.editForm.amount) {
        ElMessage.warning('请输入预算金额')
        return
      }

      try {
        const budgetId = this.budgets[this.editIndex].id
        const updateData = {
          amount: this.editForm.amount,
          alert: this.editForm.alert
        }

        // 调用 updateBudget API 更新数据
        await updateBudget(budgetId, updateData)

        // 更新本地数据
        this.budgets[this.editIndex] = {
          ...this.budgets[this.editIndex],
          ...updateData
        }

        this.editDialogVisible = false
        ElMessage.success('预算设置已更新')
      } catch (error) {
        console.error('更新预算失败:', error)
        ElMessage.error('更新预算失败：' + (error.response?.data || error.message))
      }
    }
    ,

    async deleteBudget(index) {
      try {
        const budgetId = this.budgets[index].id
        await deleteBudget(budgetId)  // 调用删除API

        // API调用成功后，从本地数组中移除该项
        this.budgets.splice(index, 1)
        ElMessage.success('预算已删除')
      } catch (error) {
        console.error('删除预算失败:', error)
        ElMessage.error('删除预算失败：' + (error.response?.data || error.message))
      }
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
