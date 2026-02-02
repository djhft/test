<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-6">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-800 mb-2">记账</h1>
        <p class="text-sm sm:text-base text-gray-500">快速添加收入或支出记录</p>
      </header>

      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6 mb-6">
        <!-- 类型切换 -->
        <div class="flex rounded-xl bg-gray-100 p-1 mb-6">
          <button
              :class="['flex-1 py-3 rounded-xl text-sm font-medium transition-all duration-200',
                     tab==='income' ? 'bg-white shadow-md text-blue-500' : 'text-gray-600 hover:text-blue-400']"
              @click="setTab('income')"
          >
            <i class="fas fa-plus-circle mr-2"></i>收入
          </button>
          <button
              :class="['flex-1 py-3 rounded-xl text-sm font-medium transition-all duration-200',
                     tab==='expense' ? 'bg-white shadow-md text-blue-500' : 'text-gray-600 hover:text-blue-400']"
              @click="setTab('expense')"
          >
            <i class="fas fa-minus-circle mr-2"></i>支出
          </button>
        </div>

        <!-- 分类选择 -->
        <div class="mb-6">
          <label class="text-sm text-gray-600 mb-3 block">选择分类</label>
          <div class="grid grid-cols-4 sm:grid-cols-5 gap-3">
            <div v-for="item in (tab === 'expense' ? consume_grids : income_grids)"
                 :key="item.id"
                 @click="form.category = item.id"
                 :class="['cursor-pointer rounded-xl p-3 flex flex-col items-center transition-all duration-200',
                          form.category === item.id
                            ? 'bg-gradient-to-br from-blue-50 to-blue-50 border-2 border-blue-300'
                            : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent',
                          !form.category && showError ? 'border-red-500' : '']">
              <img :src="item.image" :alt="item.text" class="w-8 h-8 mb-1" />
              <span class="text-xs text-gray-700">{{ item.text }}</span>
            </div>
          </div>
        </div>

        <form @submit.prevent="save" class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="text-sm text-gray-600 mb-2 block">金额</label>
              <div class="relative">
                <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
                <input
                    v-model.number="form.amount"
                    type="number"
                    step="0.01"
                    required
                    class="input-field pl-10 rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2"
                    :class="[
                      'focus:ring-blue-500',
                      !form.amount && showError ? 'border-red-500' : ''
                    ]"
                />
              </div>
            </div>

            <div>
              <label class="text-sm text-gray-600 mb-2 block">日期</label>
              <input
                  v-model="form.date"
                  type="date"
                  class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="text-sm text-gray-600 mb-2 block">时间</label>
              <input
                  v-model="form.time"
                  type="time"
                  class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label class="text-sm text-gray-600 mb-2 block">备注</label>
            <input
                v-model="form.description"
                type="text"
                class="input-field rounded-xl transition-all duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                placeholder="可选：例如某某餐厅"
            />
          </div>

          <div>
            <label class="text-sm text-gray-600 mb-3 block">附件（票据图片）</label>
            <Uploader ref="uploader" @uploaded="onUploaded" />
          </div>

          <div class="flex gap-3 pt-4">
            <button
                type="submit"
                class="btn-primary flex-1 py-3 text-base font-medium rounded-xl transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] bg-blue-500 hover:bg-blue-600"
            >
              <i class="fas fa-save mr-2"></i>保存
            </button>
            <button
                type="button"
                class="btn-secondary py-3 px-6 text-base font-medium rounded-xl transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98]"
                @click="reset"
            >
              <i class="fas fa-redo mr-2"></i>重置
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import Uploader from '@/components/Uploader.vue'
import { createTransaction } from '@/api/transaction'

// 导入所有图片
import eat from '@/assets/icon/eat.png'
import shopping from '@/assets/icon/shopping.png'
import sock from '@/assets/icon/sock.png'
import fruit from '@/assets/icon/fruit.png'
import plane from '@/assets/icon/plane.png'
import car from '@/assets/icon/car.png'
import education from '@/assets/icon/education.png'
import children from '@/assets/icon/children.png'
import gift from '@/assets/icon/gift.png'
import pet from '@/assets/icon/pet.png'
import salary from '@/assets/icon/salary.png'
import bonus from '@/assets/icon/bonus.png'
import financing from '@/assets/icon/financing.png'
import lifefee from '@/assets/icon/lifefee.png'
import vicejob from '@/assets/icon/vicejob.png'
import wipeout from '@/assets/icon/wipeout.png'
import refund from '@/assets/icon/refund.png'

export default {
  name: 'Transaction',
  components: { Uploader },
  data() {
    const now = new Date()
    return {
      tab: 'income',
      showError: false,
      form: {
        userId: 1,
        amount: 0,
        type: 'INCOME',
        category: '',
        description: '',
        date: now.toISOString().slice(0,10),
        time: now.toTimeString().slice(0,5),
        attachmentUrls: ''
      },
      recognition: { visible: false, type: '', amount: '', category: '', date: '' },
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
  methods: {
    setTab(t){
      this.tab = t;
      this.form.type = t==='income' ? 'INCOME' : 'EXPENSE';
      // this.form.category = '';
    },
    onUploaded(data) {
      if (data != null){
        this.form.attachmentUrls = data.url;
        this.form.amount = data.amount;
        this.tab = data.type.toLowerCase(); // 确保是小写
        this.form.description = data.description;
      }
      else {
        this.form.attachmentUrls = '';
        return;
      }


      // 在下一个tick设置分类，确保tab已经切换
      this.$nextTick(() => {
        // 将categoryId转换为数字
        const categoryId = Number(data.categoryId);

        // 验证分类ID是否有效
        const validCategories = this.tab === 'expense'
            ? this.consume_grids.map(g => Number(g.id)) // 确保也是数字
            : this.income_grids.map(g => Number(g.id));

        if (validCategories.includes(categoryId)) {
          this.form.category = categoryId; // 使用数字类型的categoryId
        } else {
          console.warn('无效的分类ID:', categoryId);
          this.form.category = '';
        }
      });
    },

    async save() {
      this.showError = true;
      if (!this.form.category) {
        ElMessage({
          message: '请填写分类',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        });
        return
      }
      if(!this.form.amount) {
        ElMessage({
          message: '请填写金额',
          type: 'warning',
          offset: 60,
          customClass: 'top-message'
        })
        return
      }

      this.showError = false;

      const payload = {
        userId: this.form.userId,
        amount: this.form.amount,
        type: this.form.type,
        category: this.form.category,
        description: this.form.description,
        transactionDate: this.form.date,
        attachmentUrls: this.form.attachmentUrls
      }
      try {
        await createTransaction(payload)
        ElMessage({
          message: '保存成功',
          type: 'success',
          offset: 60,
          customClass: 'top-message'
        });
        // 保存成功后清除图片
        this.$refs.uploader.clear_url();
        this.reset()
      } catch (e) {
        ElMessage({
          message: '保存失败：' + (e.response?.data || e.message),
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        });
      }
    },

    reset() {
      this.form.amount = 0;
      this.form.category = '';
      this.form.description = '';
      this.form.attachmentUrls = [];
      this.showError = false;
      // 重置时也清除图片
      if (this.$refs.uploader) {
        this.$refs.uploader.clear();
      }
    },

  }
}
</script>

<style scoped>
.input-field {
  @apply w-full px-4 py-2 border border-gray-300 focus:outline-none focus:border-transparent;
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
</style>
