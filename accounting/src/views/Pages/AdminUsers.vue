<template>
  <div class="admin-users-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-input
            v-model="searchParams.query"
            placeholder="搜索用户"
            class="search-input"
            clearable
            @clear="debouncedSearch"
        >
          <template #append>
            <el-button @click="debouncedSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button type="primary" @click="addUser">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>
    </div>

    <el-card class="user-table-card">
      <el-table
          :data="userList"
          stripe
          style="width: 100%"
          v-loading="loading"
          :height="tableHeight"
          :max-height="tableMaxHeight"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="usertype" label="用户类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.usertype === 'admin' ? 'danger' : 'success'">
              {{ scope.row.usertype === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="addTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="updateUser(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="danger"
                @click="deleteUser(scope.row)"
                :disabled="scope.row.usertype === 'admin'"
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

    <!-- 用户表单对话框 -->
    <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="formData" label-width="100px" :rules="formRules" ref="userFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="用户类型" prop="usertype">
          <el-select v-model="formData.usertype" placeholder="选择用户类型">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
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
import { Search, Plus } from '@element-plus/icons-vue'
import { GetUsersApi, AddUserApi, UpdateUserApi, DeleteUserApi } from '@/util/request'
import { debounce } from 'lodash-es'

export default {
  name: 'AdminUsers',
  setup() {
    const store = useStore()
    const router = useRouter()
    const userFormRef = ref(null)
    const tableHeight = ref('auto')
    const tableMaxHeight = ref(500)

    const state = reactive({
      loading: false,
      searchParams: {
        query: '',
        pagesize: 8,
        pagenum: 1,
        username: ''
      },
      userList: [],
      total: 0,
      dialogFormVisible: false,
      formData: {
        id: '',
        username: '',
        password: '',
        usertype: '0',
        email: '',
        phone: ''
      },
      isEdit: false,
      formRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        usertype: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    })

    // 处理窗口大小变化
    const handleResize = debounce(() => {
      const windowHeight = window.innerHeight
      const headerHeight = 60 // 头部高度
      const pageHeaderHeight = 80 // 页面头部高度
      const paginationHeight = 60 // 分页高度
      const padding = 40 // 内边距
      
      // 计算表格最大高度
      tableMaxHeight.value = windowHeight - headerHeight - pageHeaderHeight - paginationHeight - padding
      
      // 设置表格高度
      tableHeight.value = tableMaxHeight.value
    }, 100)

    // 监听窗口大小变化
    onMounted(() => {
      handleResize()
      window.addEventListener('resize', handleResize)
    })

    // 移除监听器
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
      
      // 检查是否是管理员
      if (userInfo.usertype !== 'admin') {
        ElMessage.error('您没有管理员权限')
        router.push('/login/bill')
        return false
      }
      
      return true
    }

    // 获取用户列表
    const searchList = async () => {
      if (!checkLogin()) return
      
      state.loading = true
      try {
        state.searchParams.username = store.state.uInfo.userInfo.usertype
        const res = await GetUsersApi(state.searchParams)
        state.loading = false
        
        if (res.code === 200) {
          if (res.data) {
            state.userList = res.data.records || []
            state.total = res.data.total || 0
          } else {
            state.userList = []
            state.total = 0
          }
        } else {
          ElMessage.error(res.message || '获取数据失败')
        }
      } catch (error) {
        state.loading = false
        console.error("请求失败:", error)
        if (error.response?.status === 401) {
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('loginData')
          store.commit('uInfo/setUserInfo', {})
          router.push('/host')
        } else {
          ElMessage.error(error.message || '获取数据失败')
        }
      }
    }

    // 处理页码变化
    const handlePageChange = (newPage) => {
      state.searchParams.pagenum = newPage
      searchList()
    }

    // 处理每页条数变化
    const handleSizeChange = (newSize) => {
      state.searchParams.pagesize = newSize
      searchList()
    }

    // 添加用户
    const addUser = () => {
      if (!checkLogin()) return
      
      state.isEdit = false
      state.formData = {
        id: '',
        username: '',
        password: '',
        usertype: '0',
        email: '',
        phone: ''
      }
      state.dialogFormVisible = true
    }

    // 编辑用户
    const updateUser = (row) => {
      if (!checkLogin()) return
      
      state.formData = { ...row, password: store.state.uInfo.userInfo.password }
      state.isEdit = true
      state.dialogFormVisible = true
    }

    // 保存用户
    const saveUser = async () => {
      if (!checkLogin()) return
      
      if (!userFormRef.value) return
      
      userFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const api = state.isEdit ? UpdateUserApi : AddUserApi
            const res = await api(state.formData)
            if (res.code === 200) {
              state.dialogFormVisible = false
              ElMessage.success(res.message || '操作成功')
              await searchList()
            } else {
              ElMessage.error(res.message || '操作失败')
            }
          } catch (error) {
            console.error("请求失败:", error)
            if (error.response?.status === 401) {
              ElMessage.error('登录已过期，请重新登录')
              localStorage.removeItem('token')
              localStorage.removeItem('loginData')
              store.commit('uInfo/setUserInfo', {})
              router.push('/host')
            } else {
              ElMessage.error('操作失败')
            }
          }
        } else {
          return false
        }
      })
    }

    // 删除用户
    const deleteUser = (row) => {
      if (!checkLogin()) return
      
      // 不允许删除管理员
      if (row.usertype === '1') {
        ElMessage.warning('不能删除管理员账户')
        return
      }
      
      ElMessageBox.confirm('确定要删除该用户吗？此操作将不可逆', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await DeleteUserApi(row)
          if (res.code === 200) {
            ElMessage.success(res.message || '删除成功')
            await searchList()
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error("删除失败:", error)
          if (error.response?.status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            localStorage.removeItem('loginData')
            store.commit('uInfo/setUserInfo', {})
            router.push('/host')
          } else {
            ElMessage.error('删除失败')
          }
        }
      }).catch(() => {})
    }

    // 防抖搜索
    const debouncedSearch = debounce(() => {
      searchList()
    }, 500)

    onMounted(() => {
      if (checkLogin()) {
        searchList()
      }
    })

    return {
      ...toRefs(state),
      userFormRef,
      debouncedSearch,
      handlePageChange,
      handleSizeChange,
      addUser,
      updateUser,
      saveUser,
      deleteUser,
      Search,
      Plus,
      tableHeight,
      tableMaxHeight
    }
  }
}
</script>

<style scoped>
.admin-users-container {
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

.search-input {
  width: 300px;
}

.user-table-card {
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