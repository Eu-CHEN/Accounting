<template>
  <div class="cart-container">
    <div class="page-header">
      <h2>购物车</h2>
    </div>

    <div v-loading="loading">
      <!-- 购物车为空时显示 -->
      <el-empty 
        v-if="!loading && cartItems.length === 0" 
        description="购物车是空的"
      >
        <el-button type="primary" @click="$router.push('/shop')">
          去购物
        </el-button>
      </el-empty>

      <!-- 购物车列表 -->
      <div v-else class="cart-content">
        <el-table 
          :data="cartItems" 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column label="商品" min-width="400">
            <template #default="{ row }">
              <div class="product-info">
                <img :src="row.image || defaultImage" class="product-image">
                <div class="product-details">
                  <h3>{{ row.name }}</h3>
                  <p class="description">{{ row.description }}</p>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price.toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="数量" width="200">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.quantity" 
                :min="1" 
                :max="99"
                size="small"
                @change="(value) => updateQuantity(row, value)"
              />
            </template>
          </el-table-column>

          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                size="small"
                :loading="deletingId === row.id"
                @click="removeFromCart(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 购物车底部 -->
        <div class="cart-footer">
          <div class="left">
            <el-checkbox v-model="selectAll" @change="handleSelectAll">
              全选
            </el-checkbox>
            <el-button 
              type="danger" 
              size="small" 
              :disabled="!selectedItems.length"
              @click="batchRemove"
            >
              删除选中
            </el-button>
          </div>
          
          <div class="right">
            <div class="total-info">
              <span>已选择 {{ selectedItems.length }} 件商品</span>
              <span class="total">
                合计：<span class="price">¥{{ totalAmount.toFixed(2) }}</span>
              </span>
            </div>
            <el-button 
              type="primary" 
              size="large" 
              :disabled="!selectedItems.length"
              @click="checkout"
            >
              结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

export default {
  name: 'Cart',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const cartItems = ref([])
    const selectedItems = ref([])
    const deletingId = ref(null)
    const defaultImage = 'https://via.placeholder.com/100x100?text=商品图片'

    // 计算属性
    const selectAll = computed({
      get: () => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length,
      set: (val) => handleSelectAll(val)
    })

    const totalAmount = computed(() => {
      return selectedItems.value.reduce((sum, item) => {
        return sum + item.price * item.quantity
      }, 0)
    })

    // 获取购物车列表
    const getCartList = async () => {
      loading.value = true
      try {
        const res = await GetCartListApi()
        if (res.code === 200) {
          cartItems.value = res.data || []
        } else {
          ElMessage.error(res.message || '获取购物车列表失败')
        }
      } catch (error) {
        console.error('获取购物车列表失败:', error)
        ElMessage.error('获取购物车列表失败')
      } finally {
        loading.value = false
      }
    }

    // 更新商品数量
    const updateQuantity = async (item, value) => {
      try {
        const res = await UpdateCartQuantityApi({
          id: item.id,
          quantity: value
        })
        if (res.code === 200) {
          ElMessage.success('数量已更新')
        } else {
          item.quantity = value // 回滚数量
          ElMessage.error(res.message || '更新数量失败')
        }
      } catch (error) {
        console.error('更新数量失败:', error)
        item.quantity = value // 回滚数量
        ElMessage.error('更新数量失败')
      }
    }

    // 从购物车中删除
    const removeFromCart = async (item) => {
      try {
        await ElMessageBox.confirm(
          '确定要从购物车中删除该商品吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        deletingId.value = item.id
        const res = await DeleteCartItemApi({ id: item.id })
        if (res.code === 200) {
          ElMessage.success('已从购物车中删除')
          cartItems.value = cartItems.value.filter(i => i.id !== item.id)
          selectedItems.value = selectedItems.value.filter(i => i.id !== item.id)
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      } finally {
        deletingId.value = null
      }
    }

    // 批量删除
    const batchRemove = async () => {
      if (!selectedItems.value.length) return
      
      try {
        await ElMessageBox.confirm(
          '确定要删除选中的商品吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const ids = selectedItems.value.map(item => item.id)
        const res = await BatchDeleteCartItemsApi({ ids })
        if (res.code === 200) {
          ElMessage.success('已删除选中的商品')
          cartItems.value = cartItems.value.filter(item => !ids.includes(item.id))
          selectedItems.value = []
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 全选/取消全选
    const handleSelectAll = (val) => {
      selectedItems.value = val ? [...cartItems.value] : []
    }

    // 选择变化
    const handleSelectionChange = (val) => {
      selectedItems.value = val
    }

    // 结算
    const checkout = () => {
      if (!selectedItems.value.length) return
      
      // TODO: 实现结算逻辑
      ElMessage.success('结算功能待实现')
    }

    onMounted(() => {
      getCartList()
    })

    return {
      loading,
      cartItems,
      selectedItems,
      selectAll,
      totalAmount,
      deletingId,
      defaultImage,
      handleSelectionChange,
      handleSelectAll,
      updateQuantity,
      removeFromCart,
      batchRemove,
      checkout
    }
  }
}
</script>

<style scoped>
.cart-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details {
  flex: 1;
}

.product-details h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.description {
  margin: 0;
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.cart-footer {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-footer .left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-footer .right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-info {
  text-align: right;
}

.total-info .total {
  margin-left: 20px;
  font-size: 16px;
}

.total-info .price {
  font-size: 20px;
  margin-left: 5px;
}
</style> 