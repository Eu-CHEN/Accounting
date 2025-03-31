<template>
  <div class="common-layout">
    <el-container>
      <!-- 头部区域 -->
      <el-header class="user-header">
        <div class="header-content">
          <div class="header-title">
            <img class="logo" src="../../assets/logo.png" alt="系统logo">
            <h1>购物商城</h1>
          </div>
          <div class="header-actions">
            <el-button-group>
              <el-button :icon="Shop" @click="$router.push('/user/shop')">商城</el-button>
              <el-button :icon="ShoppingCart" @click="$router.push('/user/cart')">购物车</el-button>
              <el-button :icon="List" @click="$router.push('/user/orders')">我的订单</el-button>
            </el-button-group>
            <el-button @click="loginOut" type="danger">退出系统</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ShoppingCart, List, Shop } from '@element-plus/icons-vue'

export default {
  name: "User",
  setup() {
    const router = useRouter()
    const store = useStore()

    const loginOut = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('loginData')
      store.commit('uInfo/setUserInfo', {})
      router.push('/host')
    }

    return {
      loginOut,
      ShoppingCart,
      List,
      Shop
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.common-layout {
  height: 100vh;
}

/* 头部样式 */
.user-header {
  background: #409eff;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.header-title h1 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 1px;
}

.logo {
  height: 40px;
  width: 40px;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

/* 主内容区 */
.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>