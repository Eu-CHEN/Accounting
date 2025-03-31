<template>
  <div class="notice-container">
    <div class="page-header">
      <h2>系统通知</h2>
    </div>

    <el-card class="notice-list" v-loading="loading">
      <template v-if="notificationList.length > 0">
        <div
            v-for="item in notificationList"
            :key="item.id"
            class="notice-item"
            :class="{ 'unread': !item.isRead }"
        >
          <div class="notice-header">
            <div class="notice-title">
              <el-tag
                  :type="item.type === 'important' ? 'danger' : 'info'"
                  size="small"
                  class="notice-tag"
              >
                {{ item.type === 'important' ? '重要' : '普通' }}
              </el-tag>
              <h3>{{ item.title }}</h3>
            </div>
            <span class="notice-time">{{ item.createTime }}</span>
          </div>
          <div class="notice-content">{{ item.content }}</div>
          <div class="notice-footer">
            <el-button
                type="primary"
                size="small"
                @click="showNoticeDetail(item)"
            >查看</el-button>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无通知" />

      <!-- 分页 -->
      <div class="pagination-container" v-if="notificationList.length > 0">
        <el-pagination
            v-model:current-page="searchParams.pagenum"
            v-model:page-size="searchParams.pagesize"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 通知详情对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="currentNotice.title"
        width="50%"
        :close-on-click-modal="false"
    >
      <div class="notice-detail">
        <div class="notice-detail-header">
          <el-tag
              :type="currentNotice.type === 'important' ? 'danger' : 'info'"
              size="small"
          >
            {{ currentNotice.type === 'important' ? '重要' : '普通' }}
          </el-tag>
          <span class="notice-detail-time">{{ currentNotice.createTime }}</span>
        </div>
        <div class="notice-detail-content">
          {{ currentNotice.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, toRefs, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetUserNotificationsApi } from '@/util/request'

export default {
  name: 'Notice',
  setup() {
    const store = useStore()
    const router = useRouter()

    const state = reactive({
      loading: false,
      searchParams: {
        pagenum: 1,
        pagesize: 10,
        username: store.state.uInfo.userInfo.username
      },
      notificationList: [],
      total: 0,
      dialogVisible: false,
      currentNotice: {}
    })

    // 检查登录状态
    const checkLogin = () => {
      const userInfo = store.state.uInfo.userInfo
      const token = userInfo.token
      
      if (!userInfo?.username || !token) {
        ElMessage.error('请先登录')
        router.push('/host')
        return false
      }
      
      return true
    }

    // 获取通知列表
    const getNotifications = async () => {
      if (!checkLogin()) return
      
      state.loading = true
      try {
        const res = await GetUserNotificationsApi(state.searchParams)
        state.loading = false
        
        if (res.code === 200) {
          if (res.data) {
            state.notificationList = res.data.records || []
            state.total = res.data.total || 0
          }
        } else {
          ElMessage.error(res.message || '获取通知失败')
        }
      } catch (error) {
        state.loading = false
        console.error('获取通知失败:', error)
        ElMessage.error('获取通知失败')
      }
    }

    // 显示通知详情
    const showNoticeDetail = (notice) => {
      state.currentNotice = notice
      state.dialogVisible = true
    }

    // 处理页码变化
    const handlePageChange = (newPage) => {
      state.searchParams.pagenum = newPage
      getNotifications()
    }

    // 处理每页条数变化
    const handleSizeChange = (newSize) => {
      state.searchParams.pagesize = newSize
      state.searchParams.pagenum = 1 // 重置到第一页
      getNotifications()
    }

    onMounted(() => {
      if (checkLogin()) {
        getNotifications()
      }
    })

    return {
      ...toRefs(state),
      showNoticeDetail,
      handlePageChange,
      handleSizeChange
    }
  }
}
</script>

<style scoped>
.notice-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.notice-list {
  min-height: 300px;
}

.notice-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item.unread {
  background-color: #fafafa;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notice-title h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.notice-tag {
  flex-shrink: 0;
}

.notice-time {
  color: #909399;
  font-size: 14px;
}

.notice-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
  /* 限制内容显示行数 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 通知详情样式 */
.notice-detail {
  padding: 20px;
}

.notice-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notice-detail-time {
  color: #909399;
  font-size: 14px;
}

.notice-detail-content {
  color: #606266;
  line-height: 1.8;
  font-size: 16px;
  white-space: pre-wrap;
}
</style>