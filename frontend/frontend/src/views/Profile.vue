<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-4">
        <h1 class="text-lg sm:text-xl font-bold text-gray-800">个人中心</h1>
        <p class="text-sm text-gray-500">管理账户与个人资料</p>
      </header>

      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6">
        <div class="flex items-center gap-4 mb-4">
          <div class="w-20 h-20 rounded-full bg-gray-100 overflow-hidden">
            <img :src="user.avatar || defaultAvatar" alt="avatar" class="w-full h-full object-cover" />
          </div>
          <div>
            <div class="text-lg font-medium text-gray-800">{{ user.nickname || user.username }}</div>
            <div class="text-sm text-gray-500">{{ user.email || user.phone }}</div>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label class="text-sm text-gray-500 mb-1 block">昵称</label>
            <input v-model="user.nickname" class="input-field rounded-xl" />
          </div>
          <div>
            <label class="text-sm text-gray-500 mb-1 block">邮箱</label>
            <input v-model="user.email" class="input-field rounded-xl" />
          </div>

          <div>
            <label class="text-sm text-gray-500 mb-1 block">手机号</label>
            <input v-model="user.phone" class="input-field rounded-xl" />
          </div>

          <div>
            <label class="text-sm text-gray-500 mb-1 block">修改密码</label>
            <input v-model="password" type="password" placeholder="新密码" class="input-field rounded-xl" />
          </div>
        </div>

        <div class="mt-4 flex gap-2">
          <button class="btn-primary" @click="save">保存</button>
          <button class="btn-secondary" @click="logout">退出</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      user: { username: 'testuser', nickname: '测试用户', email: '', phone: '', avatar: '' },
      password: '',
      defaultAvatar: 'https://via.placeholder.com/150?text=Avatar'
    }
  },
  methods: {
    save() { alert('保存成功（示例）') },
    logout() { localStorage.removeItem('token'); this.$router.push('/auth') }
  }
}
</script>
