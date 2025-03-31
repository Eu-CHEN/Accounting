<template>
  <div class="admin-bills-container">
    <div class="page-header">
      <h2>所有用户账单管理</h2>
      <div class="header-actions">
        <el-input
            v-model="searchParams.query"
            placeholder="搜索账单"
            class="search-input"
            clearable
            @clear="searchList"
        >
          <template #append>
            <el-button @click="searchList">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-card class="bill-table-card">
      <el-table
          :data="billList"
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="billfication" label="账单分类" width="120" />
        <el-table-column prop="billtype" label="账单类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.billtype === '收入' ? 'success' : 'danger'">
              {{ scope.row.billtype }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            <span :class="scope.row.billtype === '收入' ? 'income-amount' : 'expense-amount'">
              {{ scope.row.billtype === '收入' ? '+' : '-' }}{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="addTime" label="添加时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="updateBill(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="danger"
                @click="deleteBill(scope.row)"
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

    <!-- 编辑账单对话框 -->
    <el-dialog v-model="dialogFormVisible" title="编辑账单" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" disabled />
        </el-form-item>
        <el-form-item label="账单分类">
          <el-select v-model="formData.billfication" placeholder="选择分类">
            <el-option
                v-for="item in billCategories"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账单类型">
          <el-select v-model="formData.billtype" placeholder="选择类型">
            <el-option
                v-for="item in billTypes"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="金额">
          <el-input v-model="formData.amount" type="number" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBill">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, toRefs, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { AdminBillListApi, UpdateBillApi, DeleteBillApi } from '@/util/request'

export default {
  name: 'AdminAllBills',
  setup() {
    const store = useStore()
    const router = useRouter()

    const state = reactive({
      loading: false,
      searchParams: {
        query: '',
        pagesize: 8,
        pagenum: 1,
        username: ''
      },
      billList: [],
      total: 0,
      dialogFormVisible: false,
      formData: {
        id: '',
        username: '',
        billfication: '',
        billtype: '',
        amount: '',
        remark: ''
      },
      billCategories: ['生活', '学习', '工作', '娱乐', '其他'],
      billTypes: ['收入', '支出'],
      isEdit: false
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

    // 获取账单列表
    const searchList = async () => {
      if (!checkLogin()) return
      
      state.loading = true
      try {
        state.searchParams.username = store.state.uInfo.userInfo.usertype
        const res = await AdminBillListApi(state.searchParams)
        state.loading = false
        
        if (res.code === 200) {
          if (res.data) {
            state.billList = res.data.records || []
            state.total = res.data.total || 0
          } else {
            state.billList = []
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

    // 编辑账单
    const updateBill = (row) => {
      if (!checkLogin()) return
      
      state.formData = { ...row }
      state.isEdit = true
      state.dialogFormVisible = true
    }

    // 保存账单
    const saveBill = async () => {
      if (!checkLogin()) return
      
      if (!state.formData.billfication || !state.formData.billtype || !state.formData.amount) {
        ElMessage.error('请填写完整信息')
        return
      }
      
      try {
        const res = await UpdateBillApi(state.formData)
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
    }

    // 删除账单
    const deleteBill = (row) => {
      if (!checkLogin()) return
      
      ElMessageBox.confirm('确定要删除该账单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await DeleteBillApi(row)
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

    onMounted(() => {
      if (checkLogin()) {
        searchList()
      }
    })

    return {
      ...toRefs(state),
      searchList,
      handlePageChange,
      handleSizeChange,
      updateBill,
      saveBill,
      deleteBill,
      Search
    }
  }
}
</script>

<style scoped>
.admin-bills-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.bill-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}
</style>