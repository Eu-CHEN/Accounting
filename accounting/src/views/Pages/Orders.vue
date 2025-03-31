<template>
  <div class="orders-container">
    <div class="page-header">
      <h2>我的订单</h2>
      <div class="header-actions">
        <el-radio-group v-model="orderStatus" size="large" @change="filterOrders">
          <el-radio-button label="all">全部订单</el-radio-button>
          <el-radio-button label="unpaid">待付款</el-radio-button>
          <el-radio-button label="undelivered">待发货</el-radio-button>
          <el-radio-button label="delivered">待收货</el-radio-button>
          <el-radio-button label="completed">已完成</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="orders-list" v-loading="loading">
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <span class="order-date">下单时间：{{ order.createTime }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>

        <el-table :data="order.items" style="width: 100%" :show-header="false">
          <el-table-column width="80">
            <template #default="scope">
              <img :src="scope.row.image" class="product-image">
            </template>
          </el-table-column>
          <el-table-column>
            <template #default="scope">
              <div class="product-info">
                <div class="product-name">{{ scope.row.name }}</div>
                <div class="product-price">
                  <span class="price">¥{{ scope.row.price }}</span>
                  <span class="quantity">x {{ scope.row.quantity }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="order-footer">
          <div class="order-total">
            共 {{ getOrderItemCount(order) }} 件商品，
            实付款：<span class="total-price">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <el-button
                v-if="order.status === 'unpaid'"
                type="primary"
                @click="payOrder(order)"
            >立即付款</el-button>
            <el-button
                v-if="order.status === 'delivered'"
                type="success"
                @click="confirmReceive(order)"
            >确认收货</el-button>
            <el-button
                v-if="order.status === 'completed'"
                @click="viewOrderDetail(order)"
            >查看详情</el-button>
          </div>
        </div>
      </el-card>

      <el-empty
          v-if="orders.length === 0"
          description="暂无相关订单"
          :image-size="200"
      >
        <el-button type="primary" @click="$router.push('/user/shop')">
          去购物
        </el-button>
      </el-empty>
    </div>

    <div class="pagination-container" v-if="orders.length > 0">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'Orders',
  setup() {
    const loading = ref(false)
    const orderStatus = ref('all')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    // 模拟订单数据
    const orders = ref([
      {
        id: 1,
        orderNumber: 'ORDER202403150001',
        createTime: '2024-03-15 10:00:00',
        status: 'unpaid',
        totalAmount: 297.00,
        items: [
          {
            id: 1,
            name: '商品1',
            price: 99.00,
            quantity: 1,
            image: 'https://via.placeholder.com/80x80'
          },
          {
            id: 2,
            name: '商品2',
            price: 199.00,
            quantity: 1,
            image: 'https://via.placeholder.com/80x80'
          }
        ]
      },
      {
        id: 2,
        orderNumber: 'ORDER202403150002',
        createTime: '2024-03-15 11:00:00',
        status: 'completed',
        totalAmount: 99.00,
        items: [
          {
            id: 1,
            name: '商品1',
            price: 99.00,
            quantity: 1,
            image: 'https://via.placeholder.com/80x80'
          }
        ]
      }
    ])

    // 获取状态标签类型
    const getStatusType = (status) => {
      const types = {
        unpaid: 'warning',
        undelivered: 'info',
        delivered: 'primary',
        completed: 'success'
      }
      return types[status] || 'info'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const texts = {
        unpaid: '待付款',
        undelivered: '待发货',
        delivered: '待收货',
        completed: '已完成'
      }
      return texts[status] || status
    }

    // 获取订单商品总数
    const getOrderItemCount = (order) => {
      return order.items.reduce((total, item) => total + item.quantity, 0)
    }

    // 筛选订单
    const filterOrders = (status) => {
      loading.value = true
      // TODO: 调用后端API获取订单列表
      setTimeout(() => {
        loading.value = false
      }, 500)
    }

    // 支付订单
    const payOrder = (order) => {
      ElMessageBox.confirm(
          `确定要支付订单 ${order.orderNumber} 吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        // TODO: 调用支付接口
        ElMessage.success('支付成功')
      })
    }

    // 确认收货
    const confirmReceive = (order) => {
      ElMessageBox.confirm(
          '确认已收到商品？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        // TODO: 调用确认收货接口
        ElMessage.success('确认收货成功')
      })
    }

    // 查看订单详情
    const viewOrderDetail = (order) => {
      // TODO: 跳转到订单详情页或打开详情弹窗
      ElMessage.info('查看订单详情')
    }

    // 处理分页
    const handleSizeChange = (val) => {
      pageSize.value = val
      filterOrders(orderStatus.value)
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      filterOrders(orderStatus.value)
    }

    return {
      loading,
      orderStatus,
      orders,
      currentPage,
      pageSize,
      total,
      getStatusType,
      getStatusText,
      getOrderItemCount,
      filterOrders,
      payOrder,
      confirmReceive,
      viewOrderDetail,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.orders-container {
  min-height: calc(100vh - 100px);
}

.page-header {
  margin-bottom: 20px;
}

.header-actions {
  margin-top: 20px;
}

.orders-list {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  gap: 20px;
  color: #606266;
}

.order-number {
  font-weight: bold;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-size: 14px;
  color: #333;
}

.product-price {
  display: flex;
  gap: 10px;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.quantity {
  color: #909399;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #EBEEF5;
}

.order-total {
  color: #606266;

  .total-price {
    color: #f56c6c;
    font-weight: bold;
    font-size: 18px;
    margin-left: 5px;
  }
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>