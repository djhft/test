<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-6">
        <h1 class="text-xl sm:text-2xl font-bold text-gray-800 mb-2 flex items-center">
          <i class="fas fa-piggy-bank mr-3 text-blue-500"></i>储蓄目标
        </h1>
        <p class="text-sm sm:text-base text-gray-500">设定并追踪您的储蓄目标</p>
      </header>

      <!-- 添加目标表单 -->
      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6 mb-6 transition-all duration-300 hover:shadow-lg">
        <form @submit.prevent="addGoal" class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- 目标名称输入 -->
            <div>
              <label class="text-sm text-gray-600 mb-2 block flex items-center">
                <i class="fas fa-tag mr-2 text-blue-500"></i>目标名称
              </label>
              <input
                  v-model="goal.name"
                  class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  :class="{ 'border-red-500 animate-pulse': showError && !goal.name }"
                  placeholder="例如：旅行基金"
              />
            </div>

            <!-- 目标金额输入 -->
            <div>
              <label class="text-sm text-gray-600 mb-2 block flex items-center">
                <i class="fas fa-yen-sign mr-2 text-blue-500"></i>目标金额
              </label>
              <div class="relative">
                <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
                <input
                    v-model.number="goal.targetAmount"
                    type="number"
                    class="input-field pl-10 rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    :class="{ 'border-red-500 animate-pulse': showError && !goal.amount }"
                    placeholder="请输入目标金额"
                />
              </div>
            </div>

            <!-- 目标日期选择 -->
            <div>
              <label class="text-sm text-gray-600 mb-2 block flex items-center">
                <i class="fas fa-calendar mr-2 text-blue-500"></i>目标日期
              </label>
              <input
                  v-model="goal.targetDate"
                  type="date"
                  :min="minDate"
                  class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  :class="{ 'border-red-500 animate-pulse': showError && !goal.date }"
              />
            </div>

            <!-- 分类选择 -->
            <div>
              <label class="text-sm text-gray-600 mb-2 block flex items-center">
                <i class="fas fa-folder mr-2 text-blue-500"></i>分类
              </label>
              <div class="relative">
                <button
                    type="button"
                    @click="toggleCategories"
                    class="w-full px-4 py-2 border border-gray-300 rounded-xl text-left flex items-center justify-between hover:border-blue-300 transition-colors"
                    :class="{ 'border-red-500 animate-pulse': showError && !goal.category }"
                >
                  <div class="flex items-center">
                    <img :src="getCategoryImage(goal.category)" :alt="goal.category" class="w-6 h-6 mr-2" />
                    <span class="text-gray-700">{{ getCategoryLabel(goal.category) }}</span>
                  </div>
                  <i class="fas fa-chevron-down text-gray-400 transition-transform" :class="{ 'rotate-180': showCategories }"></i>
                </button>

                <transition name="fade">
                  <div v-if="showCategories"
                       class="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-lg z-10">
                    <div class="grid grid-cols-4 gap-2 p-3 max-h-48 overflow-y-auto">
                      <div v-for="cat in categories"
                           :key="cat.value"
                           @click="selectCategory(cat)"
                           class="cursor-pointer rounded-lg p-3 flex flex-col items-center transition-all duration-200 hover:bg-blue-50">
                        <img :src="cat.image" :alt="cat.label" class="w-6 h-6 mb-1" />
                        <span class="text-xs text-gray-700">{{ cat.label }}</span>
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>
          </div>

          <!-- 描述输入 -->
          <div>
            <label class="text-sm text-gray-600 mb-2 block flex items-center">
              <i class="fas fa-align-left mr-2 text-blue-500"></i>描述
            </label>
            <textarea
                v-model="goal.description"
                rows="3"
                class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                placeholder="添加目标描述（可选）"
            ></textarea>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-3">
            <button
                type="submit"
                class="btn-primary flex-1 py-3 text-base font-medium rounded-xl transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center"
            >
              <i class="fas fa-plus-circle mr-2"></i>创建目标
            </button>
            <button
                type="button"
                class="btn-secondary py-3 px-6 text-base font-medium rounded-xl transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center"
                @click="resetGoal"
            >
              <i class="fas fa-redo mr-2"></i>重置
            </button>
          </div>
        </form>
      </div>

      <!-- 目标列表 -->
      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6 transition-all duration-300 hover:shadow-lg">
        <h3 class="text-lg font-semibold text-gray-800 mb-4 flex items-center">
          <i class="fas fa-list mr-2 text-blue-500"></i>我的目标
        </h3>
        <div class="space-y-4">
          <transition-group name="list">
            <div v-for="g in goals" :key="g.id"
                 class="border border-gray-100 rounded-xl p-4 transition-all duration-200 hover:shadow-md hover:border-blue-200">
              <!-- 目标信息 -->
              <div class="flex items-center justify-between mb-3">
                <div class="flex items-center">
                  <img :src="getCategoryImage(g.category)" :alt="g.category" class="w-10 h-10 mr-3" />
                  <div>
                    <h4 class="font-medium text-gray-800">{{ g.name }}</h4>
                    <div class="text-sm text-gray-500">
                      <span class="mr-3">目标 ¥{{ formatAmount(g.targetAmount) }}</span>
                      <span>已存 ¥{{ formatAmount(g.currentAmount) }}</span>
                    </div>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-base font-medium" :class="g.currentAmount >= g.targetAmount ? 'text-green-500' : 'text-gray-800'">
                    {{ Math.round(g.currentAmount / g.targetAmount * 100) }}%
                  </div>
                  <div class="text-sm text-gray-500">
                    剩余 ¥{{ formatAmount(Math.max(0, g.targetAmount - g.currentAmount)) }}
                  </div>
                </div>
              </div>

              <!-- 进度条 -->
              <div class="relative">
                <div class="w-full bg-gray-200 h-3 rounded-full overflow-hidden">
                  <div
                      class="h-3 rounded-full transition-all duration-500 ease-out relative"
                      :class="getProgressClass(g)"
                      :style="{ width: Math.min(g.currentAmount / g.targetAmount * 100, 100) + '%' }"
                  >
                    <div v-if="g.currentAmount >= g.targetAmount"
                         class="absolute right-0 top-1/2 transform -translate-y-1/2 w-2 h-2 bg-white rounded-full animate-pulse"></div>
                  </div>
                </div>
                <div v-if="g.currentAmount >= g.targetAmount" class="absolute -top-6 right-0 text-xs text-green-500 font-medium flex items-center">
                  <i class="fas fa-check-circle mr-1"></i>
                  已达成
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="mt-4 flex justify-end gap-2">
                <button
                    class="btn-secondary text-sm py-2 px-4 rounded-lg transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] flex items-center"
                    @click="openDepositDialog(g)"
                >
                  <i class="fas fa-plus-circle mr-1"></i>存款
                </button>
                <button
                    class="text-blue-500 text-sm py-2 px-4 rounded-lg hover:bg-blue-50 transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] flex items-center"
                    @click="edit(g)"
                >
                  <i class="fas fa-pencil mr-1"></i>编辑
                </button>
                <button
                    class="text-red-500 text-sm py-2 px-4 rounded-lg hover:bg-red-50 transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] flex items-center"
                    @click="deleteGoal(g)"
                >
                  <i class="fas fa-trash mr-1"></i>删除
                </button>
              </div>
            </div>
          </transition-group>

          <!-- 空状态 -->
          <div v-if="!goals.length" class="text-center text-gray-400 py-12">
            <i class="fas fa-inbox text-4xl mb-3"></i>
            <p>暂无目标</p>
            <p class="text-sm mt-2">点击上方添加您的第一个储蓄目标</p>
          </div>
        </div>
      </div>

      <!-- 存款对话框 -->
      <el-dialog v-model="depositDialog.visible" title="存款" width="400px">
        <div class="space-y-4">
          <div>
            <label class="text-sm text-gray-600 mb-2 block">存款金额</label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
              <input
                  v-model.number="depositDialog.amount"
                  type="number"
                  class="input-field pl-10 rounded-xl"
                  placeholder="请输入存款金额"
              />
            </div>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <button class="btn-secondary" @click="depositDialog.visible = false">取消</button>
            <button class="btn-primary ml-2" @click="confirmDeposit(g)">确认</button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑目标对话框 -->
      <el-dialog v-model="editDialog" title="编辑目标" width="500px">
        <form @submit.prevent="updateGoal" class="space-y-4">
          <div>
            <label class="text-sm text-gray-600 mb-2 block">目标名称</label>
            <input
                v-model="editingGoal.name"
                class="input-field rounded-xl"
                placeholder="请输入目标名称"
            />
          </div>
          <div>
            <label class="text-sm text-gray-600 mb-2 block">目标金额</label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
              <input
                  v-model.number="editingGoal.targetAmount"
                  type="number"
                  class="input-field pl-10 rounded-xl"
                  placeholder="请输入目标金额"
              />
            </div>
          </div>
          <div>
            <label class="text-sm text-gray-600 mb-2 block">目标日期</label>
            <input
                v-model="editingGoal.targetDate"
                type="date"
                :min="minDate"
                class="input-field rounded-xl"
            />
          </div>
          <div>
            <label class="text-sm text-gray-600 mb-2 block">分类</label>
            <div class="relative">
              <button
                  type="button"
                  @click="toggleEditCategories"
                  class="w-full px-4 py-2 border border-gray-300 rounded-xl text-left flex items-center justify-between hover:border-blue-300 transition-colors"
              >
                <div class="flex items-center">
                  <img :src="getCategoryImage(editingGoal.category)" :alt="editingGoal.category" class="w-6 h-6 mr-2" />
                  <span class="text-gray-700">{{ getCategoryLabel(editingGoal.category) }}</span>
                </div>
                <i class="fas fa-chevron-down text-gray-400 transition-transform" :class="{ 'rotate-180': showEditCategories }"></i>
              </button>
              <transition name="fade">
                <div v-if="showEditCategories"
                     class="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-lg z-10">
                  <div class="grid grid-cols-4 gap-2 p-3 max-h-48 overflow-y-auto">
                    <div v-for="cat in categories"
                         :key="cat.value"
                         @click="selectEditCategory(cat)"
                         class="cursor-pointer rounded-lg p-3 flex flex-col items-center transition-all duration-200 hover:bg-blue-50">
                      <img :src="cat.image" :alt="cat.label" class="w-6 h-6 mb-1" />
                      <span class="text-xs text-gray-700">{{ cat.label }}</span>
                    </div>
                  </div>
                </div>
              </transition>
            </div>
          </div>
          <div>
            <label class="text-sm text-gray-600 mb-2 block">描述</label>
            <textarea
                v-model="editingGoal.describeText"
                rows="3"
                class="input-field rounded-xl"
                placeholder="添加目标描述（可选）"
            ></textarea>
          </div>
        </form>
        <template #footer>
          <span class="dialog-footer">
            <button class="btn-secondary" @click="editDialog = false">取消</button>
            <button class="btn-primary ml-2" @click="updateGoal">保存</button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入所有图标
import travel from '@/assets/icon/plane.png'
import education from '@/assets/icon/education.png'
import house from '@/assets/icon/house.png'
import emergency from '@/assets/icon/emergency.png'
import medical from '@/assets/icon/medical.png'
import investment from '@/assets/icon/investment.png'
import shopping from '@/assets/icon/shopping.png'
import food from '@/assets/icon/eat.png'
import transport from '@/assets/icon/car.png'
import entertainment from '@/assets/icon/entertainment.png'
import insurance from '@/assets/icon/insurance.png'
import retirement from '@/assets/icon/retirement.png'
import wedding from '@/assets/icon/wedding.png'
import vehicle from '@/assets/icon/vehicle.png'
import renovation from '@/assets/icon/renovation.png'
import other from '@/assets/icon/other.png'
import {createSavings, deleteSavings, getSavings, updateSavings} from "@/api/savings.js";

export default {
  name: 'Savings',
  data() {
    return {
      showError: false,
      showCategories: false,
      showEditCategories: false,
      goal: {
        name: '',
        targetAmount: 0,  // 修改字段名
        currentAmount: 0,
        targetDate: '',   // 修改字段名
        category: 'travel',
        description: ''
      },
      editingGoal: null,
      editDialog: false,
      categories: [
        { value: 'travel', label: '旅行', image: travel },
        { value: 'education', label: '教育', image: education },
        { value: 'house', label: '住房', image: house },
        { value: 'emergency', label: '应急', image: emergency },
        { value: 'medical', label: '医疗', image: medical },
        { value: 'investment', label: '投资', image: investment },
        { value: 'shopping', label: '购物', image: shopping },
        { value: 'food', label: '餐饮', image: food },
        { value: 'transport', label: '交通', image: transport },
        { value: 'entertainment', label: '娱乐', image: entertainment },
        { value: 'insurance', label: '保险', image: insurance },
        { value: 'retirement', label: '养老', image: retirement },
        { value: 'wedding', label: '婚礼', image: wedding },
        { value: 'vehicle', label: '购车', image: vehicle },
        { value: 'renovation', label: '装修', image: renovation },
        { value: 'other', label: '其他', image: other }
      ],
      goals: [],
      depositDialog: {
        visible: false,
        amount: 0,
        currentGoal: null
      }
    }
  },
  computed: {
    minDate() {
      return new Date().toISOString().split('T')[0]
    }
  },
  created() {
    this.get_goal();
  },
  methods: {
    // 格式化金额
    formatAmount(amount) {
      return Number(amount).toFixed(2)
    },
    // 获取分类图片
    getCategoryImage(category) {
      const cat = this.categories.find(c => c.value === category)
      return cat ? cat.image : travel
    },
    // 获取分类标签
    getCategoryLabel(value) {
      const cat = this.categories.find(c => c.value === value)
      return cat ? cat.label : '请选择'
    },
    // 获取进度条样式
    getProgressClass(goal) {
      const progress = Number(goal.currentAmount) / Number(goal.targetAmount)
      if (progress >= 1) return 'bg-green-500'
      if (progress >= 0.8) return 'bg-yellow-500'
      return 'bg-blue-500'
    },
    // 切换分类选择器
    toggleCategories() {
      this.showCategories = !this.showCategories
    },
    // 选择分类
    selectCategory(cat) {
      this.goal.category = cat.value
      this.showCategories = false
    },
    // 切换编辑对话框中的分类选择器
    toggleEditCategories() {
      this.showEditCategories = !this.showEditCategories
    },
    // 选择编辑对话框中的分类
    selectEditCategory(cat) {
      this.editingGoal.category = cat.value
      this.showEditCategories = false
    },
    // 添加目标
    async addGoal() {
      this.showError = true
      if (!this.validateGoal()) {
        return
      }

      try {
        await createSavings(this.goal)
        ElMessage({
          message: '目标已创建',
          type: 'success',
          offset: 60,
          customClass: 'top-message'
        })
        this.resetGoal()
        await this.get_goal()  // 重新获取列表
      } catch (error) {
        console.error('创建目标失败:', error)
        ElMessage({
          message: '创建失败：' + (error.response?.data || error.message),
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        })
      }
    },
    // 验证目标信息
    validateGoal() {
      console.log(this.goal)
      if (!this.goal.name?.trim()) {
        ElMessage({
          message: '请输入目标名称',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      if (!this.goal.targetAmount || this.goal.targetAmount <= 0) {
        ElMessage({
          message: '请输入有效的目标金额',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      if (!this.goal.targetDate) {
        ElMessage({
          message: '请选择目标日期',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      return true
    },
    // 重置表单
    resetGoal() {
      this.goal = {
        name: '',
        targetAmount: 0,
        currentAmount: 0,
        targetDate: '',
        category: 'travel',
        description: ''
      }
      this.showError = false
    },
    // 打开存款对话框
    openDepositDialog(goal) {
      this.depositDialog = {
        visible: true,
        amount: 0,
        currentGoal: goal
      }
    },
    // 确认存款
    async confirmDeposit() {
      if (!this.depositDialog.amount || this.depositDialog.amount <= 0) {
        ElMessage({
          message: '请输入有效的存款金额',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return
      }

      try {
        const newAmount = Number(this.depositDialog.currentGoal.currentAmount) + Number(this.depositDialog.amount)
        await updateSavings(this.depositDialog.currentGoal.id, {
          currentAmount: newAmount
        })

        ElMessage({
          message: '存款成功',
          type: 'success',
          offset: 60,
          customClass: 'top-message'
        })

        this.depositDialog.currentGoal.currentAmount = newAmount
        this.depositDialog.visible = false
      } catch (error) {
        console.error('存款失败:', error)
        ElMessage({
          message: '存款失败：' + (error.response?.data || error.message),
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        })
      }
    },
    // 获取目标列表
    async get_goal() {
      try {
        const response = await getSavings()
        this.goals = response.data || []  // 直接赋值，不需要push
      } catch (error) {
        console.error('获取目标列表失败:', error)
        ElMessage({
          message: '获取列表失败：' + (error.response?.data || error.message),
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        })
      }
    },
    // 编辑目标
    edit(goal) {
      console.log(goal)
      this.editingGoal = { ...goal }
      this.editDialog = true
    },
    // 更新目标
    async updateGoal() {
      if (!this.validateEditGoal()) {
        return
      }

      try {
        await updateSavings(this.editingGoal.id, this.editingGoal)
        ElMessage({
          message: '目标已更新',
          type: 'success',
          offset: 60,
          customClass: 'top-message'
        })
        this.editDialog = false
        await this.get_goal()  // 重新获取列表
      } catch (error) {
        console.error('更新目标失败:', error)
        ElMessage({
          message: '更新失败：' + (error.response?.data || error.message),
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        })
      }
    },
    // 验证编辑的目标信息
    validateEditGoal() {
      if (!this.editingGoal.name?.trim()) {
        ElMessage({
          message: '请输入目标名称',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      if (!this.editingGoal.targetAmount || this.editingGoal.targetAmount <= 0) {
        ElMessage({
          message: '请输入有效的目标金额',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      if (!this.editingGoal.targetDate) {
        ElMessage({
          message: '请选择目标日期',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return false
      }
      return true
    },
    // 删除目标
    async deleteGoal(goal) {
      try {
        await ElMessageBox.confirm(
            '确定要删除这个目标吗？',
            '警告',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        )

        // 这里需要添加删除API调用
        await deleteSavings(goal.id)

        const index = this.goals.findIndex(g => g.id === goal.id)
        if (index !== -1) {
          this.goals.splice(index, 1)
          ElMessage({
            type: 'success',
            message: '删除成功',
            offset: 60,
            customClass: 'top-message'
          })
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除目标失败:', error)
          ElMessage({
            type: 'error',
            message: '删除失败：' + (error.response?.data || error.message),
            offset: 60,
            customClass: 'top-message'
          })
        }
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
  @apply bg-blue-500 text-white hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500;
}

.btn-secondary {
  @apply bg-gray-100 text-gray-700 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500;
}

.shadow-card {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.top-message {
  z-index: 3000 !important;
  margin-top: 10px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 滚动条样式 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #555;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>
