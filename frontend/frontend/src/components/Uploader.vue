<template>
  <div>
    <div class="border-2 border-dashed border-primary rounded-lg p-4 text-center">
      <div class="mb-2">
        <i class="fa fa-camera text-primary text-3xl"></i>
      </div>
      <p class="text-gray-500 mb-1 font-medium">直接上传图片即可自动识别账单信息</p>
      <p class="text-gray-400 text-xs mb-3">支持JPG、PNG格式，单张不超过5MB</p>

      <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="onFile" />
      <div class="flex justify-center gap-3">
        <button v-if="!uploadedUrl" type="button" class="btn-primary" @click="trigger">上传消费截图或发票</button>
        <button v-if="uploadedUrl" type="button" class="btn-secondary" @click="clear">清除</button>
      </div>

      <div v-if="uploadedUrl" class="mt-3 flex justify-center">
        <div class="relative group">
          <div class="w-20 h-20 overflow-hidden rounded-md border">
            <img :src="uploadedUrl" class="object-cover w-full h-full" />
          </div>
          <button
              @click="clear"
              class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
            <i class="fas fa-times text-xs"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {deleteImage, uploadImage} from '@/api/upload';
import { ElMessage } from 'element-plus';

export default {
  name: 'Uploader',
  data() {
    return {
      filename:null,
      file: null,
      preview: null,
      uploadedUrl: null
    }
  },
  methods: {
    trigger() {
      this.$refs.fileInput.click()
    },
    clear_url() {
      if (this.preview) {
        URL.revokeObjectURL(this.preview);
      }
      this.file = null;
      this.preview = null;
      this.uploadedUrl = null;
      this.filename = null;
      this.$emit('clear');
      this.$emit('uploaded', null);
    },
    async clear() {
      try {

        if (this.filename) {

          await deleteImage(this.filename);
          ElMessage.success('图片删除成功');
          if (this.preview) {
            URL.revokeObjectURL(this.preview);
          }

        }
      } catch (error) {
        console.error('删除服务器图片失败:', error);
        ElMessage.error('删除服务器图片失败');
      } finally {
        this.file = null;
        this.preview = null;
        this.uploadedUrl = null;
        this.filename = null;
        this.$emit('clear');
        this.$emit('uploaded', null);
      }
    },
    async onFile(e) {
      const chosen = e.target.files[0]
      if (!chosen) return

      // 文件大小检查
      if (chosen.size > 5 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过5MB')
        return
      }

      // 文件类型检查
      if (!chosen.type.startsWith('image/')) {
        ElMessage.error('只能上传图片文件')
        return
      }

      // 清理之前的预览
      if (this.preview) {
        URL.revokeObjectURL(this.preview)
      }

      this.file = chosen
      this.preview = URL.createObjectURL(chosen)

      try {
        const formData = new FormData()
        formData.append('file', chosen)

        const response = await uploadImage(formData)
        const url = response.data.url || response.data
        if (url) {
          this.uploadedUrl = url
          this.filename = response.data.filename
          this.$emit('uploaded', url)
          ElMessage.success('图片上传成功')
        } else {
          throw new Error('上传响应中没有URL')
        }
      } catch (err) {
        if (err.response) {
          const errorData = err.response.data

          // 显示更详细的错误信息
          if (errorData.error) {
            ElMessage.error('上传失败: ' + errorData.error)
          } else if (errorData.message) {
            ElMessage.error('上传失败: ' + errorData.message)
          } else {
            ElMessage.error('上传失败，请重试')
          }
        } else {
          ElMessage.error('上传失败: ' + err.message)
        }
        this.clear()
      }
    }

  },
  beforeUnmount() {
    if (this.preview) {
      URL.revokeObjectURL(this.preview)
    }
  }
}
</script>

<style scoped>
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

.opacity-0 {
  opacity: 0;
}

.transition-opacity {
  transition-property: opacity;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}
</style>
