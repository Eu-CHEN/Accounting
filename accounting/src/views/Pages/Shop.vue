<template>
  <div class="shop-container">
    <div class="page-header">
      <h2>商品列表</h2>
      <div class="header-actions">
        <el-input
            v-model="data.searchParams.query"
            placeholder="搜索商品"
            class="search-input"
            clearable
            @clear="getProducts"
            @keyup.enter="searchProducts"
        >
          <template #append>
            <el-button @click="searchProducts">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div v-loading="loading">
      <el-row :gutter="20" class="product-list">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id">
          <el-card :body-style="{ padding: '0px' }" class="product-card" @click="showDetail(product)">
            <div class="product-image-container">
              <img :src="product.image || defaultImage" class="product-image">
            </div>
            <div class="product-info">
              <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
              <div class="product-description">{{ product.description }}</div>
              <div class="product-price-actions">
                <span class="price">¥{{ product.price.toFixed(2) }}</span>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click.stop="addToCart(product)"
                  :loading="addingToCart === product.id"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty 
        v-if="!loading && products.length === 0" 
        description="暂无商品" 
      />

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="data.searchParams.pagenum"
          v-model:page-size="data.searchParams.pagesize"
          :page-sizes="[12, 24, 36, 48]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="商品详情"
      width="600px"
      destroy-on-close
    >
      <div class="product-detail" v-if="currentProduct">
        <div class="detail-image">
          <img :src="currentProduct.image || defaultImage" :alt="currentProduct.name">
        </div>
        <div class="detail-info">
          <h2>{{ currentProduct.name }}</h2>
          <div class="detail-price">¥{{ currentProduct.price.toFixed(2) }}</div>
          <div class="detail-description">{{ currentProduct.description }}</div>
          <div class="detail-actions">
            <el-input-number 
              v-model="purchaseQuantity" 
              :min="1" 
              :max="99"
              size="large"
            />
            <el-button 
              type="primary" 
              size="large"
              :loading="addingToCart === currentProduct.id"
              @click="addToCart(currentProduct, purchaseQuantity)"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ShoppingCart } from '@element-plus/icons-vue'
import { GetProductsApi, AddToCartApi } from '@/util/request'

export default {
  name: 'Shop',
  setup() {
    const loading = ref(false)
    const products = ref([])
    const total = ref(0)
    const addingToCart = ref(null)
    const dialogVisible = ref(false)
    const currentProduct = ref(null)
    const purchaseQuantity = ref(1)
    const defaultImage = null

    const data = reactive({
      searchParams:{
        query: '',
        pagenum: 1,
        pagesize: 12,
        username: ''
      }
    })

    // 获取商品列表
    const getProducts = async () => {
      loading.value = true
      try {
        const res = await GetProductsApi(data.searchParams)
        if (res.code === 200) {
          products.value = res.data.records || []
          total.value = res.data.total || 0
        } else {
          ElMessage.error(res.message || '获取商品列表失败')
        }
      } catch (error) {
        console.error('获取商品列表失败:', error)
        ElMessage.error('获取商品列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索商品
    const searchProducts = async () => {
      data.searchParams.pagenum = 1
      await getProducts()
    }

    // 显示商品详情
    const showDetail = (product) => {
      currentProduct.value = product
      purchaseQuantity.value = 1
      dialogVisible.value = true
    }

    // 添加到购物车
    const addToCart = async (product, quantity = 1) => {
      if (addingToCart.value) return

      const cartData = {
        productId: product.id,
        quantity: quantity,
      }
      console.log('发送的购物车数据:', cartData)

      addingToCart.value = product.id
      try {
        const res = await AddToCartApi(cartData)
        if (res.code === 200) {
          ElMessage.success('已添加到购物车')
          dialogVisible.value = false
        } else {
          ElMessage.error(res.message || '添加到购物车失败')
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
        ElMessage.error('添加到购物车失败')
      } finally {
        addingToCart.value = null
      }
    }

    // 处理分页
    const handleSizeChange = (val) => {
      data.searchParams.pagesize = val
      data.searchParams.pagenum = 1
      getProducts()
    }

    const handleCurrentChange = (val) => {
      data.searchParams.pagenum = val
      getProducts()
    }

    onMounted(() => {
      getProducts()
    })

    return {
      loading,
      products,
      total,
      data,
      addingToCart,
      dialogVisible,
      currentProduct,
      purchaseQuantity,
      defaultImage,
      getProducts,
      searchProducts,
      showDetail,
      addToCart,
      handleSizeChange,
      handleCurrentChange,
      Search,
      ShoppingCart
    }
  }
}
</script>

<style scoped>
.shop-container {
  min-height: calc(100vh - 100px);
}

.page-header {
  margin-bottom: 20px;
}

.header-actions {
  margin-top: 16px;
  display: flex;
  gap: 16px;
}

.search-input {
  width: 300px;
}

.product-list {
  margin-bottom: 20px;
}

.product-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.product-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-info {
  padding: 14px;
}

.product-name {
  margin: 0;
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 40px;
}

.product-price-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-bottom: 20px;
}

/* 商品详情样式 */
.product-detail {
  display: flex;
  gap: 20px;
}

.detail-image {
  width: 200px;
  height: 200px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
  }
}

.detail-info {
  flex: 1;
  
  h2 {
    margin: 0 0 16px 0;
    font-size: 20px;
    color: #333;
  }
}

.detail-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 16px;
}

.detail-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.detail-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .header-actions {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .product-detail {
    flex-direction: column;
    align-items: center;
  }

  .detail-image {
    width: 100%;
    max-width: 300px;
    height: auto;
    aspect-ratio: 1;
  }
}
</style> 