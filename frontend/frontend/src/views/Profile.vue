<template>
  <div class="min-h-screen bg-neutral lg:ml-64">
    <div class="w-full max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <header class="mb-4">
        <h1 class="text-lg sm:text-xl font-bold text-gray-800">个人中心</h1>
        <p class="text-sm text-gray-500">管理账户与个人资料</p>
      </header>

      <div class="bg-white rounded-2xl shadow-card p-4 sm:p-6">
        <div class="flex items-center gap-4 mb-4">
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
            <input v-model="password" type="password" placeholder="新密码（当前版本请使用忘记密码流程）" class="input-field rounded-xl" />
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
import { getMe, updateMe } from '@/api/user';

export default {
  name: 'Profile',
  data() {
    return {
      user: { username: '', nickname: '', email: '', phone: '', avatar: '' },
      password: '',
      defaultAvatar: 'https://via.placeholder.com/150?text=Avatar'
    };
  },
  async created() {
    try {
      const res = await getMe();
      // 后端可能返回 user / data.user ，兼容处理
      const data = res.data || {};
      const user = data.user || data;
      this.user = {
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        email: user.email,
        phone: user.phone,
        avatar: user.avatar
      };
    } catch (err) {
      console.error('获取用户信息失败', err);
      // token 可能无效，request 的拦截器会跳转到登录页
    }
  },
  methods: {
    async save() {
      try {
        if (this.password && this.password.length > 0) {
          alert('当前版本暂不支持在此处修改密码，请使用忘记密码流程或在后端实现专门接口。');
          // 如果后端支持，可以把 password 送到专门的修改密码接口
          return;
        }
        const payload = {
          nickname: this.user.nickname,
          email: this.user.email,
          phone: this.user.phone,
          avatar: this.user.avatar
        };
        const res = await updateMe(payload);
        alert('保存成功');
        // 更新本地 user（若需要）
        return res;
      } catch (err) {
        const msg = err.response?.data?.message || err.message;
        alert('保存失败：' + msg);
      }
    },
    logout() {
      localStorage.removeItem('token');
      this.$router.push('/auth');
    }
  }
};
</script>