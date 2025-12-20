<template>
  <div class="chart-wrapper">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'CategoryChart',
  props: {
    categoryData: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const canvas = ref(null)
    let chart = null

    const initChart = () => {
      chart = new Chart(canvas.value.getContext('2d'), {
        type: 'doughnut',
        data: {
          labels: props.categoryData.labels || [],
          datasets: [{
            data: props.categoryData.data || [],
            backgroundColor: [
              '#ef4444',
              '#3b82f6',
              '#f59e0b',
              '#10b981',
              '#8b5cf6',
              '#ec4899'
            ]
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              maxHeight: 80   // ⭐ 防止无限撑高
            }
          }
        }
      })
    }

    watch(
        () => props.categoryData,
        (val) => {
          if (!chart) return
          chart.data.labels = val.labels || []
          chart.data.datasets[0].data = val.data || []
          chart.update()
        },
        { deep: true }
    )

    onMounted(initChart)
    onBeforeUnmount(() => chart?.destroy())

    return { canvas }
  }
}
</script>

<style scoped>
.chart-wrapper {
  height: 300px;
  overflow: hidden; /* 最后一道保险 */
}
</style>
