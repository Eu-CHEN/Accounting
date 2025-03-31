<template>
  <div class="bill-container">
    <div class="page-header">
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/login/hello' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>账单管理</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="header-actions">
        <div class="search-box">
          <el-input
              v-model="keyword"
              placeholder="输入关键字搜索"
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
        <el-button type="primary" @click="addBill">
          <el-icon><Plus /></el-icon>
          新建账单
        </el-button>
      </div>
    </div>

    <el-card class="bill-table-card" v-loading="loading">
      <el-table 
          :data="billList" 
          style="width: 100%"
          :stripe="true"
          :border="true"
      >
        <el-table-column label="序号" width="80" fixed>
          <template #default="scope">
            {{ (searchParams.pagenum - 1) * searchParams.pagesize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="billfication" label="账单类别" width="140" />
        <el-table-column prop="billtype" label="资金流向" width="120">
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
        <el-table-column prop="remark" label="录入说明" min-width="200" show-overflow-tooltip />
        <el-table-column prop="addTime" label="录入时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="updatebill(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deletebill(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
            background
        />
      </div>
    </el-card>

    <el-dialog 
        v-model="dialogFormVisible" 
        :title="isEdit ? '编辑账单' : '新建账单'" 
        width="500px"
        destroy-on-close
    >
      <el-form 
          v-model="formData"
          label-width="100px"
          class="bill-form"
      >
        <el-form-item label="账单类别">
          <el-select
              v-model="formData.billfication"
              placeholder="请选择账单类别"
              class="form-select"
          >
            <el-option
                v-for="item in Billfaction"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资金流向">
          <el-select
              v-model="formData.billtype"
              placeholder="请选择资金流向"
              class="form-select"
          >
            <el-option
                v-for="item in Billtype"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账单金额">
          <el-input 
              v-model="formData.amount" 
              placeholder="请输入账单金额" 
              class="form-input"
              type="number"
          />
        </el-form-item>
        <el-form-item label="账单备注">
          <el-input 
              v-model="formData.remark" 
              placeholder="请输入录入账单的备注"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="billout">取消</el-button>
          <el-button type="primary" @click="billadd">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, toRefs, onMounted } from "vue"
import {
  BillListApi,
  AddBillApi,
  UpdateBillApi,
  DeleteBillApi,
  GetBillCategoriesApi,
  GetBillTypesApi
} from "@/util/request.js"
import { ElMessage, ElMessageBox } from "element-plus"
import { useStore } from 'vuex'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

export default {
  name: "bill",
  components: {
    Search,
    Plus,
    Edit,
    Delete
  },
  setup(){
    const store = useStore()
    const data = reactive({
      loading: false,
      keyword:'',
      searchParams:{
        query:'',
        pagesize:8,
        pagenum:1,
        username:store.state.uInfo.userInfo.username
      },
      billList:[],
      dialogFormVisible:false,
      formData:{
        billfication:'',
        billtype:'',
        amount:'',
        remark:'',
        username:store.state.uInfo.userInfo.username
      },
      isEdit: false,
      Billfaction: [],
      Billtype: [],
      total:0,
    })

    // 获取账单类别
    const getBillCategories = async () => {
      try {
        const res = await GetBillCategoriesApi()
        if (res.code === 200 && res.data) {
          data.Billfaction = res.data
        }
      } catch (error) {
        console.error("获取账单类别失败:", error)
        ElMessage.error('获取账单类别失败')
      }
    }

    // 获取资金流向
    const getBillTypes = async () => {
      try {
        const res = await GetBillTypesApi()
        if (res.code === 200 && res.data) {
          data.Billtype = res.data
        }
      } catch (error) {
        console.error("获取资金流向失败:", error)
        ElMessage.error('获取资金流向失败')
      }
    }

    const searchList = async () => {
      data.loading = true;
      data.searchParams.query = data.keyword;
      try {
        const res = await BillListApi(data.searchParams);
        if (res.data) {
          data.billList = res.data.records || [];
          data.total = res.data.total || 0;
        }
      } catch (error) {
        console.error("请求失败:", error);
        ElMessage.error('获取数据失败');
      } finally {
        data.loading = false;
      }
    }

    const addBill=()=>{
      data.isEdit = false;
      data.formData = { billfication: '', billtype: '', amount: '', remark: '' };
      data.dialogFormVisible = true;
    }
    const billout=()=>{
      data.dialogFormVisible = false
    }
    const billadd=()=>{
      data.formData.username = store.state.uInfo.userInfo.username
      const api = data.isEdit ? UpdateBillApi : AddBillApi;
      api(data.formData).then(res=>{
        if (res.data){
          data.dialogFormVisible = false
          ElMessage({
            type: 'success',
            message: res.message,
          })
          searchList();
          data.formData = { billfication: '', billtype: '', amount: '', remark: '' };
        }
      }).catch(error => {
        console.error("请求失败:")
      })
    }
    const handlePageChange = (newPage) => {
      data.searchParams.pagenum = newPage;
      searchList();
    }

    // 处理每页显示数量变化
    const handleSizeChange = (newSize) => {
      data.searchParams.pagesize = newSize;
      data.searchParams.pagenum = 1;
      searchList();
    }

    const updatebill = (row) =>{
      console.log(row)
      data.formData = { ...row };
      data.isEdit = true;
      data.dialogFormVisible = true;
    }
    const deletebill = (row) =>{
      console.log(row)
      ElMessageBox.confirm('确定要删除该账单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        DeleteBillApi(row).then(res => {
          ElMessage.success(res.message);
          searchList();
        }).catch(error => {
        });
      }).catch(() => {});
    }

    // 在组件挂载时获取所有需要的数据
    onMounted(async () => {
      await Promise.all([
        getBillCategories(),
        getBillTypes()
      ])
      searchList()
    })

    return{
      ... toRefs(data),
      searchList,
      addBill,
      billout,
      billadd,
      ArrowRight:"这是什么",
      handlePageChange,
      handleSizeChange,
      updatebill,
      deletebill,
      getBillCategories,
      getBillTypes
    }
  }
}
</script>

<style scoped>
.bill-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
}

.page-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  height: 60px;
  flex-shrink: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 40px;
  margin-top: 10px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin-right: 16px;
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

.bill-table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.bill-table-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  padding: 0;
}

/* 设置表格容器样式 */
:deep(.el-table) {
  width: 100%;
}

:deep(.el-table__body-wrapper) {
  max-height: calc(100vh - 250px);
  overflow-y: auto;
}

:deep(.el-table__fixed-right) {
  bottom: 0 !important;
}

:deep(.el-table__fixed-right .el-table__fixed-body-wrapper) {
  overflow-y: auto;
}

.pagination-container {
  padding: 12px 8px;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #EBEEF5;
}

/* 表格内的金额样式 */
.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}

/* 表单样式 */
.bill-form {
  padding: 20px;
}

.form-select {
  width: 100%;
}

.form-input {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #dcdfe6;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    gap: 10px;
    height: auto;
  }

  .search-box {
    max-width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>