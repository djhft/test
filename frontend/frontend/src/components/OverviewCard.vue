<template>
  <div
      class="group bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 p-4 sm:p-6"
  >
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3">
        <div
            class="w-12 h-12 rounded-xl flex items-center justify-center transition-transform group-hover:scale-110"
            :class="bgClass"
        >
          <img :src="icon" class="w-6 h-6"  alt=""/>
        </div>
        <span class="text-gray-600 text-lg font-medium">
          {{ title }}
        </span>
      </div>

      <div
          class="opacity-0 group-hover:opacity-100 transition-opacity"
          :class="trendColorClass"
      >
        <i :class="trendIcon"></i>
      </div>
    </div>

    <!-- Value -->
    <p
        class="text-2xl sm:text-3xl font-bold mt-4"
        :class="valueColorClass"
    >
      ¥{{ value.toLocaleString() }}
    </p>

  </div>
</template>

<script>
export default {
  name: 'OverviewCard',
  props: {
    title: {
      type: String,
      required: true
    },
    value: {
      type: Number,
      required: true
    },
    icon: {
      type: String,
      required: true
    },
    color: {
      type: String,
      required: true,
      validator: (v) => ['green', 'red', 'blue'].includes(v)
    }
  },
  computed: {
    bgClass() {
      return {
        green: 'bg-green-100',
        red: 'bg-red-100',
        blue: 'bg-blue-100'
      }[this.color]
    },
    valueColorClass() {
      return {
        green: 'text-green-600',
        red: 'text-red-500',
        blue: 'text-blue-500'
      }[this.color]
    },
    barClass() {
      return {
        green: 'bg-green-500',
        red: 'bg-red-500',
        blue: 'bg-blue-500'
      }[this.color]
    },
    trendColorClass() {
      return {
        green: 'text-green-500',
        red: 'text-red-500',
        blue: 'text-blue-500'
      }[this.color]
    },
    trendIcon() {
      return {
        green: 'fas fa-arrow-trend-up',
        red: 'fas fa-arrow-trend-down',
        blue: 'fas fa-piggy-bank'
      }[this.color]
    },
    progress() {
      // 模拟进度条比例（你之后可接真实比例）
      return {
        green: 0.75,
        red: 0.6,
        blue: 0.4
      }[this.color]
    }
  }
}
</script>
