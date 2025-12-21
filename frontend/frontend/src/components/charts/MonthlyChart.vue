<template>
  <div style="height:300px;">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
// import Chart from 'chart.js/auto'

export default {
  name: 'MonthlyChart',
  setup() {
    const canvas = ref(null)
    onMounted(() => {
      const ctx = canvas.value.getContext('2d')
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['一月', '二月', '三月', '四月', '五月', '六月'],
          datasets: [
            { label: '收入', data: [7500,8200,7800,8500,9000,8500], backgroundColor: '#10b981' },
            { label: '支出', data: [4800,5100,4900,5200,5500,5200], backgroundColor: '#ef4444' }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'top' } },
          scales: { y: { beginAtZero: true } }
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
