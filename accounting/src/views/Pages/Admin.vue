<template>
  <div class="common-layout">
    <el-container>
      <!-- 头部区域 -->
      <el-header class="admin-header">
        <div class="header-content">
          <div class="header-title">
            <img class="admin-logo" src="../../assets/logo.png" alt="系统logo">
            <h1>记账管理系统 - 管理员面板</h1>
          </div>
          <el-button @click="loginOut" type="danger" class="logout-btn">退出系统</el-button>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边导航 -->
        <el-aside width="220px" class="admin-aside">
          <el-menu
              :default-active="activeMenu"
              class="el-menu-vertical"
              :collapse="isCollapse"
              background-color="#2d3a4b"
              text-color="#fff"
              active-text-color="#ffd04b"
              router
          >
            <el-menu-item index="/admin/hello">
              <el-icon><House /></el-icon>
              <span class="menu-title">主页</span>
            </el-menu-item>
            <el-sub-menu index="1">
              <template #title>
                <el-icon><DocumentCopy /></el-icon>
                <span class="menu-title">账单管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/admin/allbills" class="menu-item">所有账单</el-menu-item>
                <el-menu-item index="/admin/categories" class="menu-item">类别管理</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>

            <el-sub-menu index="2">
              <template #title>
                <el-icon><User /></el-icon>
                <span class="menu-title">用户管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/admin/users" class="menu-item">用户列表</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>

            <el-sub-menu index="3">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span class="menu-title">系统设置</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/admin/settings" class="menu-item">系统配置</el-menu-item>
                <el-menu-item index="/admin/notifications" class="menu-item">通知管理</el-menu-item>
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
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { DocumentCopy, Setting, User, House } from '@element-plus/icons-vue'

export default {
  name: "admin",
  setup() {
    const router = useRouter()
    const store = useStore()

    const loginOut = () => {
      localStorage.removeItem("loginData")
      store.commit("setUserInfo", {})
      router.push({ path: '/host' })
    }

    return {
      loginOut,
      DocumentCopy,
      Setting,
      User,
      House
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
.admin-header {
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

.admin-logo {
  height: 40px;
  width: 40px;
}

/* 侧边栏样式 */
.admin-aside {
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
