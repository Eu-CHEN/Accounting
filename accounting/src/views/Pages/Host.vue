<template>
  <div class="host-container">
    <div class="login-card">
      <h2 class="login-title">系统登录</h2>
      <el-form
          ref="formRef"
          :model="loginData"
          :rules="formRules"
          label-width="80px"
          label-position="top"
          class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="loginData.username"
              placeholder="请输入用户名"
              prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              type="password"
              v-model="loginData.password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
          />
        </el-form-item>

        <el-button
            type="primary"
            class="login-btn"
            @click="handlogin"
        >
          立即登录
        </el-button>
        <el-button
            type="primary"
            class="login-btn buttontwo"
            @click="enroll"
        >
          立即注册
        </el-button>
      </el-form>
    </div>
    <el-dialog v-model="dialogFormVisible" width="500" class="enroll-dialog">
      <h2 class="enroll-title">用户注册</h2>
      <el-form :model="formData" label-width="80px" label-position="top">
        <el-form-item label="用户名">
          <el-input
              v-model="formData.username"
              placeholder="输入用户名"
              class="enroll-input"
              prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
              v-model="formData.password"
              placeholder="输入密码"
              type="password"
              class="enroll-input"
              prefix-icon="Lock"
              show-password
          />
        </el-form-item>
        <el-form-item label="邮箱地址">
          <el-input
              v-model="formData.email"
              placeholder="输入邮箱地址"
              class="enroll-input"
              prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input
              v-model="formData.phone"
              placeholder="输入手机号码"
              class="enroll-input"
              prefix-icon="Phone"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false" class="enroll-btn">取消</el-button>
          <el-button type="primary" class="enroll-btn" @click="handleEnroll">立即注册</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive,toRefs } from "vue";
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { loginApi,AddUserApi} from "@/util/request";
import { User, Lock } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

export default {
  name: "LoginPage",
  setup() {
    const store = useStore()
    const router = useRouter()

    const data = reactive({
      dialogFormVisible:false,
      formData:{
        username:'',
        password:'',
        email:'',
        phone:''
      },
    })

    const state = reactive({
      loginData: {
        id:'',
        username: '',
        password: '',
        email:'',
        phone:'',
        usertype:''
      },

      formRules: {
        username: [
          { required: true, message: '用户名不能为空', trigger: 'blur' },
          { min: 3, max: 12, message: '长度在3到12个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
          { min: 6, max: 18, message: '长度在6到18个字符', trigger: 'blur' }
        ]
      },
    })

    const handlogin = () => {
      loginApi(state.loginData).then(res => {
        if (res.data) {
          state.loginData = res.data
          if (res.data.usertype == 'user'){
            store.commit('setUserInfo', res.data)
            localStorage.setItem('loginData', JSON.stringify(state.loginData))
            router.push('/login/hello')
            ElMessage({
              type: 'success',
              message: '登录成功',
            })
          } else if (res.data.usertype == 'admin'){
            store.commit('setUserInfo', res.data)
            localStorage.setItem('loginData', JSON.stringify(state.loginData))
            router.push('/admin')
            ElMessage({
              type: 'success',
              message: '欢迎管理员',
            })
          }else {
            store.commit('setUserInfo', res.data)
            localStorage.setItem('loginData', JSON.stringify(state.loginData))
            router.push('/user')
            ElMessage({
              type: 'success',
              message: '欢迎来到购物端',
            })
          }
        }else {
          ElMessage({
            type: 'error',
            message: '登录失败',
          })
        }
      })
    }

    const enroll = () =>{
      data.dialogFormVisible = true
    }

    const handleEnroll =()=>{
      AddUserApi(data.formData).then(res => {
        if (res.data) {
          data.dialogFormVisible = true
          console.log(res.data)
          ElMessage({
            type: 'success',
            message: res.message,
          })
          data.formData = { username:'', password:'', email:'', phone:'' };
          data.dialogFormVisible = false
        }
      })
    }

    return {
      ...toRefs(data),
      ...state,
      handlogin,
      enroll,
      User,
      Lock,
      handleEnroll
    }
  }
}
</script>

<style scoped>
.host-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #2d3a4b 0%, #42b983 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 35px;
  background: rgba(255,255,255,0.96);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.login-title {
  margin: 0 0 30px;
  color: #2d3a4b;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
  letter-spacing: 1px;
}

.login-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
    padding-bottom: 8px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 6px;
    padding: 0 15px;
    box-shadow: 0 0 0 1px #dcdfe6;

    &.is-focus {
      box-shadow: 0 0 0 1px #409eff;
    }
  }
}

.login-btn {
  width: 100%;
  height: 42px;
  margin-top: 20px;
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 6px;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.3);
}

@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 30px 25px;
  }

  .login-title {
    font-size: 20px;
  }
}

/* 注册弹窗样式 */
:deep(.enroll-dialog) {
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}

:deep(.enroll-dialog .el-dialog__header) {
  display: none; /* 隐藏默认标题 */
}

.enroll-title {
  margin: 0 0 25px;
  color: #2d3a4b;
  font-size: 22px;
  font-weight: 600;
  text-align: center;
  letter-spacing: 1px;
}

:deep(.enroll-dialog .el-form-item__label) {
  font-weight: 500;
  color: #606266;
  padding-bottom: 8px;
}

.enroll-input :deep(.el-input__wrapper) {
  width: 100%;
  border-radius: 6px;
  padding: 0 15px;
  box-shadow: 0 0 0 1px #dcdfe6;
  transition: all 0.3s;
}

.enroll-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.enroll-btn {
  width: 120px;
  height: 38px;
  border-radius: 6px;
  margin: 10px 0 0;
  transition: all 0.3s;
}

.enroll-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.3);
}

/* 移动端适配 */
@media (max-width: 768px) {
  :deep(.enroll-dialog) {
    width: 90% !important;
    max-width: 350px;
  }

  .enroll-btn {
    width: 100px;
    height: 36px;
  }
}
.buttontwo{
  margin-left:0;
}
</style>