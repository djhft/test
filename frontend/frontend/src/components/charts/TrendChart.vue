<template>
  <div class="chart-wrapper">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'TrendChart',
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const canvas = ref(null)
    let chartInstance = null

    const buildChart = () => {
      if (!canvas.value) return
      const ctx = canvas.value.getContext('2d')

      if (chartInstance) {
        chartInstance.destroy()
      }

      const labels = props.data.map(item => item.label)
      const income = props.data.map(item => item.income)
      const expense = props.data.map(item => item.expense)

      chartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: '收入',
              data: income,
              borderColor: '#10b981',
              backgroundColor: 'rgba(16,185,129,0.15)',
              tension: 0.4,
              fill: true,
              pointRadius: 4,
              borderWidth: 3
            },
            {
              label: '支出',
              data: expense,
              borderColor: '#ef4444',
              backgroundColor: 'rgba(239,68,68,0.15)',
              tension: 0.4,
              fill: true,
              pointRadius: 4,
              borderWidth: 3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          resizeDelay: 100,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: { display: false },
            tooltip: {
              callbacks: {
                label(ctx) {
                  return `${ctx.dataset.label}：¥${ctx.parsed.y.toLocaleString()}`
                }
              }
            }
          },
          scales: {
            x: {
              grid: { display: false }
            },
            y: {
              beginAtZero: true,
              ticks: {
                callback: v => `¥${v.toLocaleString()}`
              }
            }
          }
        }
      })
    }

    onMounted(buildChart)

    watch(
        () => props.data,
        () => buildChart(),
        { deep: true }
    )

    onBeforeUnmount(() => {
      chartInstance?.destroy()
    })

    return { canvas }
  }
}
</script>

<style scoped>
.chart-wrapper {
  height: 260px;
}
</style>
