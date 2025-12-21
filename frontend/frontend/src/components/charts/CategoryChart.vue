<template>
  <v-chart class="chart" :option="option" autoresize />
</template>

<script>
// 引入 ECharts 核心
import * as echarts from 'echarts/core'

// 引入渲染器（必须！）
import { CanvasRenderer } from 'echarts/renderers'

// 引入图表类型
import { PieChart } from 'echarts/charts'

// 引入所需组件（新增 GraphicComponent）
import {
  TooltipComponent,
  LegendComponent,
  GraphicComponent  // ← 新增这一行
} from 'echarts/components'

// 注册所有用到的模块
echarts.use([
  CanvasRenderer,
  PieChart,
  TooltipComponent,
  LegendComponent,
  GraphicComponent  // ← 注册它
])

// 引入 vue-echarts 组件
import VChart from 'vue-echarts'

export default {
  name: 'CategoryChart',
  components: { VChart },
  props: {
    data: {
      type: Array,
      label: { show: false },
      labelLine: { show: false },
      required: true,
      default: () => []
    }
  },
  computed: {
    option() {
      let dataArray = Array.isArray(this.data)
          ? this.data
          : (Array.isArray(this.data?.data) ? this.data.data : [])

      const seriesData = dataArray
          .filter(item => item && typeof item === 'object')
          .map(item => ({
            name: item.name || '未分类',
            value: parseFloat(item.totalAmount) || 0
          }))
          .filter(item => item.value > 0)

      // if (seriesData.length === 0) {
      //   return {
      //     // 使用 graphic 显示提示文字 → 必须注册 GraphicComponent
      //     graphic: {
      //       type: 'text',
      //       left: 'center',
      //       top: 'middle',
      //       style: {
      //         text: '暂无支出数据',
      //         fontSize: 14,
      //         fill: '#999'
      //       }
      //     }
      //   }
      // }

      return {
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
        legend: { orient: 'horizontal', bottom: '2%', left: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: seriesData,
          label: { show: false },
          emphasis: { label: { show: true, fontSize: '14' } }
        }]
      }
    }
  }
}
</script>

<style scoped>
.chart {
  width: 100%;
  height: 300px;
}
</style>