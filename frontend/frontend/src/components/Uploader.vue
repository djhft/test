<template>
  <div>
    <div class="border-2 border-dashed border-primary rounded-lg p-4 text-center">
      <div class="mb-2">
        <i class="fa fa-camera text-primary text-3xl"></i>
      </div>
      <p class="text-gray-500 mb-1 font-medium">直接上传图片即可自动识别账单信息</p>
      <p class="text-gray-400 text-xs mb-3">支持JPG、PNG格式，单张不超过5MB</p>

      <input ref="fileInput" type="file" accept="image/*" multiple class="hidden" @change="onFiles" />
      <div class="flex justify-center gap-3">
        <button type="button" class="btn-primary" @click="trigger">上传消费截图或发票</button>
        <button v-if="files.length" type="button" class="btn-secondary" @click="clear">清除</button>
      </div>

      <div class="mt-3 flex flex-wrap gap-3 justify-center">
        <div v-for="(u, i) in previews" :key="i" class="w-20 h-20 overflow-hidden rounded-md border">
          <img :src="u" class="object-cover w-full h-full" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Uploader',
  props: {
    uploadUrl: { type: String, default: '/api/files/upload' }
  },
  data() {
    return {
      files: [],
      previews: []
    }
  },
  methods: {
    trigger() {
      this.$refs.fileInput.click()
    },
    clear() {
      this.files = []
      this.previews = []
      this.$emit('clear')
    },
    async onFiles(e) {
      const chosen = Array.from(e.target.files || [])
      if (!chosen.length) return
      // limit to 5
      const toUpload = chosen.slice(0, 5)
      this.files = toUpload

      // show previews
      this.previews = []
      for (const f of toUpload) {
        const url = URL.createObjectURL(f)
        this.previews.push(url)
      }

      // upload sequentially
      const uploadedUrls = []
      for (const f of toUpload) {
        try {
          const fd = new FormData()
          fd.append('file', f)
          const res = await fetch(this.uploadUrl, {
            method: 'POST',
            body: fd,
            credentials: 'include' // if needed
          })
          // assume backend returns JSON { url: "/files/xxx" }
          const data = await res.json()
          const url = data.url || (data && data[0]) || null
          if (url) uploadedUrls.push(url)
        } catch (err) {
          console.error('upload error', err)
        }
      }

      // emit uploaded urls
      if (uploadedUrls.length) {
        this.$emit('uploaded', uploadedUrls)
      }
    }
  },
  beforeUnmount() {
    // revoke object URLs
    this.previews.forEach(u => URL.revokeObjectURL(u))
  }
}
</script>

<style scoped>
/* nothing extra */
</style>
