<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card income">
        <template #header>
          <div class="card-header">
            <span>总收入</span>
            <el-tag type="success">收入</el-tag>
          </div>
        </template>
        <div class="card-amount">
          <span class="amount">¥ {{ summaryData.totalIncome || '0.00' }}</span>
          <span class="label">本月收入</span>
        </div>
      </el-card>

      <el-card class="stat-card expense">
        <template #header>
          <div class="card-header">
            <span>总支出</span>
            <el-tag type="danger">支出</el-tag>
          </div>
        </template>
        <div class="card-amount">
          <span class="amount">¥ {{ summaryData.totalExpense || '0.00' }}</span>
          <span class="label">本月支出</span>
        </div>
      </el-card>

      <el-card class="stat-card balance">
        <template #header>
          <div class="card-header">
            <span>结余</span>
            <el-tag type="info">净额</el-tag>
          </div>
        </template>
        <div class="card-amount">
          <span class="amount">¥ {{ summaryData.balance || '0.00' }}</span>
          <span class="label">本月结余</span>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>收支趋势</span>
            <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">本年</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div class="chart-container">
          <div ref="trendChartRef" style="width: 100%; height: 300px"></div>
        </div>
      </el-card>

      <div class="chart-row">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入分类</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="incomePieRef" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>

        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>支出分类</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="expensePieRef" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, toRefs, onUnmounted, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  GetBillSummaryApi,
  GetBillCategoryStatsApi,
  GetBillTrendApi
} from '@/util/request'

export default {
  name: 'Hello',
  setup() {
    const store = useStore()
    const trendChartRef = ref(null)
    const incomePieRef = ref(null)
    const expensePieRef = ref(null)
    let trendChart = null
    let incomePieChart = null
    let expensePieChart = null

    const state = reactive({
      timeRange: 'month',
      summaryData: {
        totalIncome: 0,
        totalExpense: 0,
        balance: 0,
      },
      categoryStats: {
        income: [],
        expense: []
      },
      trendData: {
        dates: [],
        incomes: [],
        expenses: []
      },
      loading: ref(false)
    })

    const getSummaryData = async () => {
      try {
        state.loading = true
        // 确保 timeRange 有值
        const currentTimeRange = state.timeRange || 'month'
        const res = await GetBillSummaryApi({
          username: store.state.uInfo.userInfo.username,
          timeRange: currentTimeRange
        })
        if (res.code === 200) {
          state.summaryData = res.data
        } else {
          ElMessage.error(res.message || '获取数据失败')
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      } finally {
        state.loading = false
      }
    }

    const getCategoryStats = async () => {
      try {
        state.loading = true
        // 确保 timeRange 有值
        const currentTimeRange = state.timeRange || 'month'
        const res = await GetBillCategoryStatsApi({
          username: store.state.uInfo.userInfo.username,
          timeRange: currentTimeRange
        })
        if (res.code === 200) {
          state.categoryStats = res.data
          renderPieCharts()
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取分类统计失败:', error)
        ElMessage.error('获取分类统计失败')
      } finally {
        state.loading = false
      }
    }

    const getTrendData = async () => {
      try {
        state.loading = true
        // 确保 timeRange 有值
        const currentTimeRange = state.timeRange || 'month'
        const res = await GetBillTrendApi({
          username: store.state.uInfo.userInfo.username,
          timeRange: currentTimeRange
        })
        if (res.code === 200) {
          state.trendData = res.data
          renderTrendChart()
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
        ElMessage.error('获取趋势数据失败')
      } finally {
        state.loading = false
      }
    }

    // 渲染趋势图
    const renderTrendChart = () => {
      if (!trendChartRef.value) return

      try {
        if (!trendChart) {
          trendChart = echarts.init(trendChartRef.value)
        }

        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['收入', '支出']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: state.trendData.dates || []
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '收入',
              type: 'line',
              data: state.trendData.incomes || [],
              itemStyle: {
                color: '#67C23A'
              }
            },
            {
              name: '支出',
              type: 'line',
              data: state.trendData.expenses || [],
              itemStyle: {
                color: '#F56C6C'
              }
            }
          ]
        }

        trendChart.setOption(option)
      } catch (error) {
        console.error('渲染趋势图失败:', error)
      }
    }

    // 渲染饼图
    const renderPieCharts = () => {
      try {
        // 收入饼图
        if (incomePieRef.value && !incomePieChart) {
          incomePieChart = echarts.init(incomePieRef.value)
        }
        if (incomePieChart) {
          incomePieChart.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [
              {
                type: 'pie',
                radius: '50%',
                data: state.categoryStats.income || [],
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          })
        }

        // 支出饼图
        if (expensePieRef.value && !expensePieChart) {
          expensePieChart = echarts.init(expensePieRef.value)
        }
        if (expensePieChart) {
          expensePieChart.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [
              {
                type: 'pie',
                radius: '50%',
                data: state.categoryStats.expense || [],
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          })
        }
      } catch (error) {
        console.error('渲染饼图失败:', error)
      }
    }

    // 监听窗口大小变化
    const handleResize = () => {
      try {
        trendChart?.resize()
        incomePieChart?.resize()
        expensePieChart?.resize()
      } catch (error) {
        console.error('调整图表大小失败:', error)
      }
    }

    // 监听时间范围变化
    const handleTimeRangeChange = async () => {
      await getSummaryData()
      await getCategoryStats()
      await getTrendData()
    }

    // 组件挂载时获取数据
    onMounted(() => {
      // 确保 timeRange 有初始值
      state.timeRange = state.timeRange || 'month'
      getSummaryData()
      getCategoryStats()
      getTrendData()
      window.addEventListener('resize', handleResize)
    })

    // 组件卸载时清理
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      // 销毁图表实例
      trendChart?.dispose()
      incomePieChart?.dispose()
      expensePieChart?.dispose()
    })

    return {
      ...toRefs(state),
      trendChartRef,
      incomePieRef,
      expensePieRef,
      handleTimeRangeChange
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-amount {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.amount {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.label {
  color: #909399;
  font-size: 14px;
}

.charts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  height: 100%;
}

.chart-container {
  padding: 20px;
}

@media screen and (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>