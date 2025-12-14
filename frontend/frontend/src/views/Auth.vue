<template>
  <div class="min-h-screen flex items-center justify-center p-4 bg-neutral">
    <div class="w-full max-w-md bg-white rounded-xl shadow-card p-8">
      <div class="flex justify-center mb-6">
        <img src="https://p3-flow-imagex-sign.byteimg.com/tos-cn-i-a9rns2rl98/rc/pc/super_tool/7090402f045f4aa588e11f39aaebbe8b~tplv-a9rns2rl98-image.image" alt="logo" class="h-20" />
      </div>

      <div class="text-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">欢迎使用财务记账系统</h1>
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
          <label class="block text-gray-700 text-sm font-medium mb-2">邮箱/手机号</label>
          <input v-model="loginForm.username" class="input-field" placeholder="请输入邮箱或手机号" />
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
          <input v-model="regForm.email" type="email" class="input-field" placeholder="请输入邮箱" />
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">手机号</label>
          <div class="flex gap-2">
            <input v-model="regForm.phone" class="input-field flex-1" placeholder="请输入手机号" />
            <button type="button" class="btn-secondary" @click="getCode">获取验证码</button>
          </div>
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-medium mb-2">验证码</label>
          <input v-model="regForm.code" class="input-field" placeholder="请输入验证码" />
        </div>

        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-medium mb-2">密码</label>
          <input v-model="regForm.password" type="password" class="input-field" placeholder="请设置密码（8-20位）" />
        </div>

        <button type="submit" class="w-full btn-primary">注册</button>
      </form>
    </div>
  </div>
</template>

<script>
import { login, register } from '../api/auth'

export default {
  name: 'Auth',
  data() {
    return {
      activeTab: 'login',
      remember: false,
      loginForm: { username: '', password: '' },
      regForm: { email: '', phone: '', code: '', password: '' }
    }
  },
  methods: {
    async doLogin() {
      try {
        const res = await login(this.loginForm)
        // backend returns JSON string in our skeleton, handle both forms
        let data = res.data
        try { if (typeof data === 'string') data = JSON.parse(data) } catch(e){}
        const token = data?.token || data
        if (!token) { alert('登录失败：无 token'); return }
        localStorage.setItem('token', token)
        this.$router.push('/dashboard')
      } catch (err) {
        alert('登录失败：' + (err.response?.data || err.message))
      }
    },
    async doRegister() {
      try {
        await register({ username: this.regForm.email || this.regForm.phone, password: this.regForm.password, email: this.regForm.email, phone: this.regForm.phone })
        alert('注册成功，请登录')
        this.activeTab = 'login'
      } catch (err) {
        alert('注册失败：' + (err.response?.data || err.message))
      }
    },
    getCode() {
      alert('发送验证码（开发模式：跳过）')
    }
  }
}
</script>
