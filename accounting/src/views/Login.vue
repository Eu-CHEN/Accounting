<template>
  <div class="common-layout">
    <el-container>
      <!-- 头部区域 -->
      <el-header class="login-header">
        <div class="header-content">
          <div class="header-title">
            <img class="login-logo" src="../assets/logo.png" alt="系统logo">
            <h1>记账管理系统</h1>
          </div>
          <el-button @click="loginOut" type="danger" class="logout-btn">退出系统</el-button>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边导航 -->
        <el-aside width="220px" class="login-aside">
          <el-menu
              active-text-color="#ffd04b"
              background-color="#2d3a4b"
              text-color="#fff"
              :router="true"
              class="side-menu"
          >
            <el-menu-item index="/login/hello">
              <el-icon><House /></el-icon>
              <span class="menu-title">主页</span>
            </el-menu-item>
            <el-sub-menu index="1">
              <template #title>
                <el-icon><DocumentCopy /></el-icon>
                <span class="menu-title">记账管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/login/bill" class="menu-item">账单管理</el-menu-item>
                <el-menu-item index="/login/order" class="menu-item">订单管理</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>

            <el-sub-menu index="2">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span class="menu-title">系统管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/login/notice" class="menu-item">系统通知</el-menu-item>
                <el-menu-item index="/login/user" class="menu-item">信息修改</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { House } from '@element-plus/icons-vue'

export default {
  name: "MainLayout",
  components: {
    House
  },
  setup() {
    const store = useStore()
    const router = useRouter()

    const loginOut = () => {
      localStorage.removeItem("loginData")
      store.commit("setUserInfo", {})
      router.push({ path: '/host' })
    }

    return { loginOut }
  }
}
</script>

<style scoped>
/* 全局容器 */
.common-layout {
  height: 100vh;
}

/* 头部样式 */
.login-header {
  background: #2d3a4b;
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

.login-logo {
  height: 40px;
  width: 40px;
}

/* 侧边栏样式 */
.login-aside {
  background: #2d3a4b;
  transition: width 0.3s;
}

.side-menu {
  border-right: none;
}

.menu-title {
  font-size: 15px;
  font-weight: 500;
  margin-left: 8px;
}

.menu-item {
  font-size: 14px;
  padding-left: 50px !important;
  transition: all 0.3s;
}

.menu-item:hover {
  background-color: rgba(255,255,255,0.1) !important;
}

/* 主内容区 */
.main-content {
  background: #f5f7fa;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

/* 退出按钮样式 */
.logout-btn {
  padding: 8px 20px;
  border-radius: 4px;
  font-weight: 500;
}

/* 全局滚动条优化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}
</style>