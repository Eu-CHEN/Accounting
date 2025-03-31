<template>
  <div class="form-container">
    <h1 class="form-title">信息修改</h1>
    <el-form
        :model="formData"
        label-width="80px"
        label-position="top"
        class="custom-form"
    >
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="用户名">
            <el-input
                v-model="formData.username"
                placeholder="输入用户名"
                class="enroll-input"
                prefix-icon="User"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱地址">
            <el-input
                v-model="formData.email"
                placeholder="输入邮箱地址"
                class="enroll-input"
                prefix-icon="Message"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码">
            <el-input
                v-model="formData.phone"
                placeholder="输入手机号码"
                class="enroll-input"
                prefix-icon="Phone"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <div class="form-actions">
        <el-button
            type="primary"
            @click="useruppdata"
            class="submit-btn"
        >
          立即修改
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { reactive, toRefs } from "vue";
import { useStore } from "vuex";
import { UserUpdata } from "@/util/request";
import { ElMessage } from "element-plus";

export default {
  name: "UserUpdata",
  setup() {
    const store = useStore();

    const data = reactive({
      formData: {
        username: '',
        password: '',
        email: '',
        phone: '',
        ...store.state.uInfo.userInfo
      }
    });

    const useruppdata = () => {
      UserUpdata(data.formData).then(res => {
        if (res.data) {
          localStorage.setItem('loginData', JSON.stringify(res.data))
          ElMessage({
            type: 'success',
            message: res.message,
          })
        }
      })
    }

    return {
      ...toRefs(data),
      useruppdata
    };
  }
}
</script>

<style scoped>
.form-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.custom-form {
  padding: 0 20px;
}

.enroll-input {
  width: 100%;
}

.form-actions {
  margin-top: 30px;
  text-align: right;
}

.submit-btn {
  width: 150px;
  letter-spacing: 2px;
}
</style>