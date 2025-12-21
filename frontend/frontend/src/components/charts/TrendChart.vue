<!-- src/components/Charts/IncomeExpenseTrendChart.vue -->
<template>
  <v-chart class="chart" :option="option" autoresize />
</template>

<script>
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent
} from 'echarts/components'

echarts.use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent
])

import VChart from 'vue-echarts'

export default {
  name: 'IncomeExpenseTrendChart',
  components: { VChart },
  props: {
    data: {
      type: Array,
      required: true,
      default: () => []
    },
    title: {
      type: String,
      default: '收支趋势'
    }
  },
  computed: {
    option() {
      const rawData = this.data.filter(
          item => item && typeof item === 'object' && item.date && item.totalAmount != null
      )


      // if (rawData.length === 0) {
      //   return {
      //     graphic: {
      //       type: 'text',
      //       left: 'center',
      //       top: 'middle',
      //       style: {
      //         text: '暂无收支数据',
      //         fontSize: 14,
      //         fill: '#999'
      //       }
      //     }
      //   }
      // }

      // 1. 提取所有唯一日期并排序
      const dateSet = new Set(rawData.map(item => item.date))
      const sortedDates = Array.from(dateSet).sort()

      // 2. 构建映射：{ date -> { income: ..., expense: ... } }
      const map = {}
      sortedDates.forEach(date => {
        map[date] = { income: null, expense: null }
      })

      rawData.forEach(item => {
        const amount = parseFloat(item.totalAmount) || 0
        if (item.type === 'INCOME') {
          map[item.date].income = amount
        } else if (item.type === 'EXPENSE') {
          map[item.date].expense = amount
        }
      })

      // 3. 生成两个系列数据（用 null 表示无数据，ECharts 会断开连线）
      const incomeData = sortedDates.map(date => map[date].income ?? null)
      const expenseData = sortedDates.map(date => map[date].expense ?? null)

      return {
        title: {
          text: this.title,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            let tip = params[0]?.name + '<br/>'
            params.forEach(param => {
              if (param.value != null) {
                const sign = param.seriesName === '收入' ? '+' : '−'
                tip += `${param.marker} ${param.seriesName}: ${sign}¥${param.value}<br/>`
              }
            })
            return tip
          }
        },
        legend: {
          data: ['收入', '支出'],
          bottom: '0%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: sortedDates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '金额 (¥)'
        },
        series: [
          {
            name: '收入',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              color: '#52c41a', // 绿色
              width: 2
            },
            data: incomeData
          },
          {
            name: '支出',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              color: '#f5222d', // 红色
              width: 2
            },
            data: expenseData
          }
        ]
      }
    }
  }
}
</script>

<style scoped>
.chart {
  width: 100%;
  height: 400px;
}
</style>