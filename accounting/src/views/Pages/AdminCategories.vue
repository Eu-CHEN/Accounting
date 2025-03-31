<template>
  <div class="admin-categories-container">
    <div class="page-header">
      <h2>账单类别管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="addCategory">
          <el-icon><Plus /></el-icon>
          添加类别
        </el-button>
      </div>
    </div>

    <el-card class="categories-card">
      <el-table
          :data="categoriesList"
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="billfication" label="类别名称" />
        <el-table-column prop="username" label="添加人" width="120" />
        <el-table-column prop="addTime" label="添加时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editCategory(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑类别对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑类别' : '添加类别'"
        width="500px"
    >
      <el-form
          ref="categoryFormRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
      >
        <el-form-item label="类别名称" prop="billfication">
          <el-input v-model="formData.billfication" placeholder="请输入类别名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory">确定</el-button>
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
import { Plus } from '@element-plus/icons-vue'
import {
  GetAllCategoriesApi,
  AddCategoryApi,
  UpdateCategoryApi,
  DeleteCategoryApi
} from '@/util/request'

export default {
  name: 'AdminCategories',
  setup() {
    const store = useStore()
    const router = useRouter()
    const categoryFormRef = ref(null)

    const state = reactive({
      loading: false,
      categoriesList: [],
      dialogVisible: false,
      isEdit: false,
      formData: {
        id: '',
        billfication: '',
        username: store.state.uInfo.userInfo.username
      },
      formRules: {
        billfication: [
          { required: true, message: '请输入类别名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ]
      }
    })

    // 检查登录状态和管理员权限
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

    // 获取所有类别
    const getCategories = async () => {
      if (!checkLogin()) return
      
      state.loading = true
      try {
        const res = await GetAllCategoriesApi()
        state.loading = false
        
        if (res.code === 200) {
          state.categoriesList = res.data || []
        } else {
          ElMessage.error(res.message || '获取类别列表失败')
        }
      } catch (error) {
        state.loading = false
        console.error('获取类别列表失败:', error)
        ElMessage.error('获取类别列表失败')
      }
    }

    // 添加类别
    const addCategory = () => {
      if (!checkLogin()) return
      
      state.isEdit = false
      state.formData = {
        id: '',
        billfication: '',
        username: store.state.uInfo.userInfo.username
      }
      state.dialogVisible = true
    }

    // 编辑类别
    const editCategory = (row) => {
      if (!checkLogin()) return
      
      state.isEdit = true
      state.formData = { 
        id: row.id,
        billfication: row.billfication,
        username: store.state.uInfo.userInfo.username
      }
      state.dialogVisible = true
    }

    // 保存类别
    const saveCategory = async () => {
      if (!categoryFormRef.value) return
      
      await categoryFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const api = state.isEdit ? UpdateCategoryApi : AddCategoryApi
            const res = await api(state.formData)
            
            if (res.code === 200) {
              ElMessage.success(res.message || '操作成功')
              state.dialogVisible = false
              getCategories()
            } else {
              ElMessage.error(res.message || '操作失败')
            }
          } catch (error) {
            console.error('保存类别失败:', error)
            ElMessage.error('操作失败')
          }
        }
      })
    }

    // 删除类别
    const deleteCategory = (row) => {
      if (!checkLogin()) return
      
      ElMessageBox.confirm('确定要删除该类别吗？此操作将不可逆', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await DeleteCategoryApi(row)
          if (res.code === 200) {
            ElMessage.success(res.message || '删除成功')
            getCategories()
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除类别失败:', error)
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    onMounted(() => {
      if (checkLogin()) {
        getCategories()
      }
    })

    return {
      ...toRefs(state),
      categoryFormRef,
      addCategory,
      editCategory,
      saveCategory,
      deleteCategory,
      Plus
    }
  }
}
</script>

<style scoped>
.admin-categories-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.categories-card {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 