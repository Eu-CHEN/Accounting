<template>
  <div class="admin-settings-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>

    <el-card class="settings-card">
      <el-form :model="basicSettings" label-width="120px" ref="basicFormRef">
        <el-form-item label="系统名称">
          <el-input v-model="basicSettings.systemName" />
        </el-form-item>
        <el-form-item label="系统描述">
          <el-input v-model="basicSettings.systemDesc" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="管理员邮箱">
          <el-input v-model="basicSettings.adminEmail" />
        </el-form-item>
        <el-form-item label="系统版本">
          <el-input v-model="basicSettings.version" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { reactive, toRefs, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetSettingsApi, UpdateSettingsApi } from '@/util/request'

export default {
  name: 'AdminSettings',
  setup() {
    const store = useStore()
    const router = useRouter()
    const basicFormRef = ref(null)

    const state = reactive({
      basicSettings: {
        id: '1',
        systemName: '',
        systemDesc: '',
        adminEmail: '',
        version: ''
      }
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

    // 获取设置
    const getSettings = async () => {
      if (!checkLogin()) return
      
      try {
        const res = await GetSettingsApi()
        if (res.code === 200 && res.data) {
          // 更新基本设置
          if (res.data) {
            state.basicSettings = res.data
          }
        }
      } catch (error) {
        console.error("获取设置失败:", error)
        ElMessage.error('获取设置失败')
      }
    }

    // 保存基本设置
    const saveBasicSettings = async () => {
      if (!checkLogin()) return
      
      try {
        const res = await UpdateSettingsApi(state.basicSettings)
        
        if (res.code === 200) {
          // 更新 Vuex store 中的系统设置
          store.dispatch('uInfo/updateSystemSettings', state.basicSettings)
          ElMessage.success('保存成功')
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (error) {
        console.error("保存失败:", error)
        ElMessage.error('保存失败')
      }
    }

    onMounted(() => {
      if (checkLogin()) {
        getSettings()
      }
    })

    return {
      ...toRefs(state),
      basicFormRef,
      saveBasicSettings
    }
  }
}
</script>

<style scoped>
.admin-settings-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.settings-card {
  max-width: 800px;
  margin: 0 auto;
}

.form-help {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}
</style>