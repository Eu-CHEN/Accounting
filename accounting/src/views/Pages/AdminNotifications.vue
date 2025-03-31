<template>
  <div class="admin-notifications-container">
    <div class="page-header">
      <h2>通知管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="addNotification">
          <el-icon><Plus /></el-icon>
          发布通知
        </el-button>
      </div>
    </div>

    <el-card class="notification-table-card">
      <el-table
          :data="notificationList"
          stripe
          style="width: 100%"
          v-loading="loading"
          :height="tableHeight"
          :max-height="tableMaxHeight"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'important' ? 'danger' : 'info'">
              {{ scope.row.type === 'important' ? '重要' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
              {{ scope.row.status === 'active' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="editNotification(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="danger"
                @click="deleteNotification(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="searchParams.pagenum"
            v-model:page-size="searchParams.pagesize"
            :page-sizes="[8, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 通知表单对话框 -->
    <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑通知' : '发布通知'" width="600px">
      <el-form :model="formData" label-width="100px" :rules="formRules" ref="notificationFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="选择通知类型">
            <el-option label="普通通知" value="normal" />
            <el-option label="重要通知" value="important" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="选择通知状态">
            <el-option label="草稿" value="draft" />
            <el-option label="发布" value="active" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveNotification">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, toRefs, onMounted, ref, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { debounce } from 'lodash-es'
import { GetNotificationsApi, AddNotificationApi, UpdateNotificationApi, DeleteNotificationApi } from '@/util/request'

export default {
  name: 'AdminNotifications',
  setup() {
    const store = useStore()
    const router = useRouter()
    const notificationFormRef = ref(null)
    const tableHeight = ref('auto')
    const tableMaxHeight = ref(500)

    const state = reactive({
      loading: false,
      searchParams: {
        query:'',
        pagesize: 8,
        pagenum: 1,
        username:store.state.uInfo.userInfo.usertype
      },
      notificationList: [],
      total: 0,
      dialogFormVisible: false,
      formData: {
        id: '',
        title: '',
        content: '',
        type: 'normal',
        status: 'draft'
      },
      isEdit: false,
      formRules: {
        title: [
          { required: true, message: '请输入通知标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入通知内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择通知类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择通知状态', trigger: 'change' }
        ]
      }
    })

    // 处理窗口大小变化
    const handleResize = debounce(() => {
      const windowHeight = window.innerHeight
      const headerHeight = 60
      const pageHeaderHeight = 80
      const paginationHeight = 60
      const padding = 40
      
      tableMaxHeight.value = windowHeight - headerHeight - pageHeaderHeight - paginationHeight - padding
      tableHeight.value = tableMaxHeight.value
    }, 100)

    onMounted(() => {
      handleResize()
      window.addEventListener('resize', handleResize)
      if (checkLogin()) {
        getNotifications()
      }
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
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
      
      if (userInfo.usertype !== 'admin') {
        ElMessage.error('您没有管理员权限')
        router.push('/login/bill')
        return false
      }
      
      return true
    }

    // 获取通知列表
    const getNotifications = async () => {
      if (!checkLogin()) return
      
      state.loading = true
      try {
        const res = await GetNotificationsApi(state.searchParams)
        
        state.loading = false
        
        if (res.code === 200) {
          if (res.data) {
            state.notificationList = res.data.records || []
            state.total = res.data.total || 0
          }
        } else {
          ElMessage.error(res.message || '获取数据失败')
        }
      } catch (error) {
        state.loading = false
        console.error("请求失败:", error)
        ElMessage.error('获取数据失败')
      }
    }

    // 处理页码变化
    const handlePageChange = (newPage) => {
      state.searchParams.pagenum = newPage
      getNotifications()
    }

    // 处理每页条数变化
    const handleSizeChange = (newSize) => {
      state.searchParams.pagesize = newSize
      getNotifications()
    }

    // 添加通知
    const addNotification = () => {
      if (!checkLogin()) return
      
      state.isEdit = false
      state.formData = {
        id: '',
        title: '',
        content: '',
        type: 'normal',
        status: 'draft'
      }
      state.dialogFormVisible = true
    }

    // 编辑通知
    const editNotification = (row) => {
      if (!checkLogin()) return
      
      state.formData = { ...row }
      state.isEdit = true
      state.dialogFormVisible = true
    }

    // 保存通知
    const saveNotification = async () => {
      if (!checkLogin()) return
      
      if (!notificationFormRef.value) return
      
      notificationFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            let res
            if (state.isEdit) {
              // 更新通知
              res = await UpdateNotificationApi(state.formData)
            } else {
              // 添加通知
              res = await AddNotificationApi(state.formData)
            }
            
            if (res.code === 200) {
              state.dialogFormVisible = false
              ElMessage.success(res.message || '操作成功')
              await getNotifications()
            } else {
              ElMessage.error(res.message || '操作失败')
            }
          } catch (error) {
            console.error("请求失败:", error)
            ElMessage.error('操作失败')
          }
        }
      })
    }

    // 删除通知
    const deleteNotification = (row) => {
      if (!checkLogin()) return
      
      ElMessageBox.confirm('确定要删除该通知吗？此操作将不可逆', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await DeleteNotificationApi(row.id)
          
          if (res.code === 200) {
            ElMessage.success(res.message || '删除成功')
            await getNotifications()
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error("删除失败:", error)
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    return {
      ...toRefs(state),
      notificationFormRef,
      handlePageChange,
      handleSizeChange,
      addNotification,
      editNotification,
      saveNotification,
      deleteNotification,
      Plus,
      tableHeight,
      tableMaxHeight
    }
  }
}
</script>

<style scoped>
.admin-notifications-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  height: 60px;
  flex-shrink: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 40px;
}

.notification-table-card {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}

:deep(.el-table) {
  flex: 1;
  overflow: hidden;
}

:deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow: hidden;
}
</style> 