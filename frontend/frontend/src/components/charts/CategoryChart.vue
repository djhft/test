<template>
  <div style="height:300px;">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'CategoryChart',
  setup() {
    const canvas = ref(null)
    onMounted(() => {
      const ctx = canvas.value.getContext('2d')
      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['餐饮', '交通', '购物', '娱乐', '其他'],
          datasets: [{
            data: [2000, 800, 1200, 600, 600],
            backgroundColor: ['#ef4444', '#3b82f6', '#f59e0b', '#10b981', '#8b5cf6'],
            hoverOffset: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom' } }
        }
      })
    })

    return { canvas }
  }
}
</script>

<style scoped>
/* nothing special */
</style>
