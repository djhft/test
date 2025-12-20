<template>
  <div class="min-h-screen flex items-center justify-center p-4 bg-neutral">
    <div class="w-full max-w-md bg-white rounded-xl shadow-card p-8">
      <div class="flex justify-center mb-6">
        <img src="../assets/logo.png" alt="logo" class="h-12" />
      </div>

      <div class="text-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">欢迎使用个人财务记账系统</h1>
        <p class="text-gray-500 mt-2">科学管理个人财务，提升理财效率</p>
      </div>

      <div class="flex mb-6">
        <button
            :class="['flex-1 py-2 text-center font-medium', activeTab==='login' ? 'border-b-2 border-primary text-primary' : 'border-b-2 border-gray-200 text-gray-500']"
            @click="activeTab='login'">登录</button>
        <button
            :class="['flex-1 py-2 text-center font-medium', activeTab==='register' ? 'border-b-2 border-primary text-primary' : 'border-b-2 border-gray-200 text-gray-500']"
            @click="activeTab='register'">注册</button>
      </div>

      <!-- 登录 -->
      <form v-if="activeTab === 'login'">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">邮箱/手机号/用户名</label>
          <input v-model="loginForm.usernameOrEmailOrPhone" class="input-field" placeholder="请输入邮箱/手机号/用户名" />
        </div>

        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-medium mb-2">密码</label>
          <input v-model="loginForm.password" type="password" class="input-field" placeholder="请输入密码" />
          <div class="flex items-center justify-between mt-2">
            <div class="flex items-center">
              <input v-model="remember" type="checkbox" class="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded" />
              <label class="ml-2 block text-sm text-gray-700">记住我</label>
            </div>
            <a class="text-sm text-primary hover:text-accent" href="#" @click.prevent>忘记密码?</a>
          </div>
        </div>

        <button type="button" class="w-full btn-primary" @click.prevent="doLogin">登录</button>
      </form>

      <!-- 注册 -->
      <form v-else @submit.prevent="doRegister">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">邮箱</label>
          <input
              v-model="regForm.email"
              type="email"
              :class="['input-field', { 'border-red-500': errors.email }]"
              placeholder="请输入邮箱"
              @blur="validateEmail"
          />
          <p v-if="errors.email" class="text-red-500 text-sm mt-1">{{ errors.email }}</p>
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">手机号</label>
          <div class="flex gap-2">
            <input
                v-model="regForm.phone"
                :class="['input-field', { 'border-red-500': errors.phone }]"
                placeholder="请输入手机号"
                @blur="validatePhone"
            />
          </div>
          <p v-if="errors.phone" class="text-red-500 text-sm mt-1">{{ errors.phone }}</p>
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">用户名</label>
          <input
              v-model="regForm.username"
              :class="['input-field', { 'border-red-500': errors.username }]"
              placeholder="请输入用户名"
              @blur="validateUsername"
          />
          <p v-if="errors.username" class="text-red-500 text-sm mt-1">{{ errors.username }}</p>
        </div>

        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-medium mb-2">密码</label>
          <input
              v-model="regForm.password"
              type="password"
              :class="['input-field', { 'border-red-500': errors.password }]"
              placeholder="请输入密码"
              @blur="validatePassword"
          />
          <p v-if="errors.password" class="text-red-500 text-sm mt-1">{{ errors.password }}</p>
        </div>

        <button
            type="submit"
            :class="['w-full btn-primary', { 'opacity-50 cursor-not-allowed': !isFormValid }]"
            :disabled="!isFormValid"
        >注册</button>
      </form>
    </div>
  </div>
</template>

<script>
import { login, register } from '@/api/auth';
import {ElMessage} from "element-plus";

export default {
  name: 'Auth',
  data() {
    return {
      activeTab: 'login',
      remember: false,
      loginForm: { usernameOrEmailOrPhone: '', password: '' },
      regForm: {
        email: '',
        phone: '',
        username: '',
        password: ''
      },
      errors: {
        email: '',
        phone: '',
        username: '',
        password: ''
      }
    };
  },
  created() {
    // 组件创建时检查是否有记住的登录信息
    const savedCredentials = localStorage.getItem('rememberedCredentials');
    if (savedCredentials) {
      try {
        const credentials = JSON.parse(savedCredentials);
        this.loginForm.usernameOrEmailOrPhone = credentials.username;
        this.loginForm.password = credentials.password;
        this.remember = true;
      } catch (e) {
        console.error('解析保存的登录信息失败:', e);
        localStorage.removeItem('rememberedCredentials');
      }
    }
  },
  computed: {
    isFormValid() {
      return this.regForm.email &&
          this.regForm.phone &&
          this.regForm.username &&
          this.regForm.password &&
          !this.errors.email &&
          !this.errors.phone &&
          !this.errors.username &&
          !this.errors.password;
    }
  },
  methods: {
    validateEmail() {
      if (!this.regForm.email) {
        this.errors.email = '邮箱不能为空';
        return false;
      }
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.regForm.email)) {
        this.errors.email = '请输入有效的邮箱地址';
        return false;
      }
      this.errors.email = '';
      return true;
    },
    validatePhone() {
      if (!this.regForm.phone) {
        this.errors.phone = '手机号不能为空';
        return false;
      }
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.regForm.phone)) {
        this.errors.phone = '请输入有效的手机号';
        return false;
      }
      this.errors.phone = '';
      return true;
    },
    validateUsername() {
      if (!this.regForm.username) {
        this.errors.username = '用户名不能为空';
        return false;
      }
      if (this.regForm.username.length < 2) {
        this.errors.username = '用户名至少需要2个字符';
        return false;
      }
      this.errors.username = '';
      return true;
    },
    validatePassword() {
      if (!this.regForm.password) {
        this.errors.password = '密码不能为空';
        return false;
      }
      if (this.regForm.password.length < 6) {
        this.errors.password = '密码至少需要6个字符';
        return false;
      }
      this.errors.password = '';
      return true;
    },
    async doLogin() {
      // 添加表单验证
      if (!this.loginForm.usernameOrEmailOrPhone || !this.loginForm.password) {
        ElMessage({
          message: '请输入用户名和密码',
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        });
        return;
      }
      try {
        const res = await login(this.loginForm);
        // 后端返回 { user, token }
        const token = res.data.token || res.data.data?.token;
        if (token) {
          // 处理记住我功能
          if (this.remember) {
            // 保存登录信息
            localStorage.setItem('rememberedCredentials', JSON.stringify({
              username: this.loginForm.usernameOrEmailOrPhone,
              password: this.loginForm.password
            }));
          } else {
            // 清除保存的登录信息
            localStorage.removeItem('rememberedCredentials');
          }
          localStorage.setItem('token', token);
          this.$router.push('/statistics');
        } else {
          ElMessage({
            message: '登录失败：后端未返回 token',
            type: 'error',
            offset: 60,
            customClass: 'top-message'
          });
        }
      } catch (err) {
        const msg = err.response?.data?.message || err.message;
        ElMessage({
          message: '登录失败：' + msg,
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        });
      }
    },
    async doRegister() {
      // 验证所有字段
      const isEmailValid = this.validateEmail();
      const isPhoneValid = this.validatePhone();
      const isUsernameValid = this.validateUsername();
      const isPasswordValid = this.validatePassword();

      if (!isEmailValid || !isPhoneValid || !isUsernameValid || !isPasswordValid) {
        ElMessage({
          message: '请正确填写所有必填字段',
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        });
        return;
      }

      try {
        const payload = {
          email: this.regForm.email,
          phone: this.regForm.phone,
          username: this.regForm.username,
          password: this.regForm.password
        };
        const res = await register(payload);
        const token = res.data.token || res.data.data?.token;
        if (token) {
          localStorage.setItem('token', token);
          this.$router.push('/statistics');
        } else {
          ElMessage({
            message: '注册成功，请手动登录',
            type: 'success',
            offset: 60,
            customClass: 'top-message'
          });
          this.activeTab = 'login';
        }
      } catch (err) {
        const msg = err.response?.data?.message || err.message;
        ElMessage({
          message: '注册失败：' + msg,
          type: 'error',
          offset: 60,
          customClass: 'top-message'
        });
      }
    }
  }
};
</script>

<style scoped>
.input-field {
  @apply w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent;
}

.input-field.border-red-500 {
  @apply border-red-500;
}

.btn-primary {
  @apply bg-primary text-white py-2 px-4 rounded-lg hover:bg-primary-dark transition-colors duration-200;
}

.btn-primary:disabled {
  @apply opacity-50 cursor-not-allowed;
}
</style>
