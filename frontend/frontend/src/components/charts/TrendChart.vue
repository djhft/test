<template>
  <div style="height:300px;">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'TrendChart',
  setup() {
    const canvas = ref(null)
    let chart = null

    onMounted(() => {
      const ctx = canvas.value.getContext('2d')
      chart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          datasets: [
            {
              label: '收入',
              data: [1200, 800, 1500, 2000, 1800, 900, 1100],
              borderColor: '#10b981',
              backgroundColor: 'rgba(16,185,129,0.1)',
              tension: 0.4,
              fill: true
            },
            {
              label: '支出',
              data: [800, 1200, 1000, 1500, 900, 1300, 700],
              borderColor: '#ef4444',
              backgroundColor: 'rgba(239,68,68,0.1)',
              tension: 0.4,
              fill: true
            }
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
