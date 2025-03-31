<template>
  登录
  密码：<input type="text" placeholder="请输入密码" v-model="ps"><br/>
  账号：<input type="text" placeholder="请输入账号" v-model="user1"><br/>
  号码：<input type="text" placeholder="请输入手机号码" v-model="phone" @input="phoneinput" @focus="phonefocus">
  <p>{{ps}}--{{user1}}</p>
  <button @click="loginclick">登录</button>
</template>

<script>
import {reactive,toRefs} from 'vue'
export default {
  name: "home",
  setup(){
    const date = reactive({
      ps:'',
      user1:'',
      phone:''
    })
    const loginclick =()=>{
      alert(`${date.ps}+${date.user1}`)
    }
    const phoneinput =()=>{
      if (!/^[1][3,5,6,8,9][0-9]{9}$/.test(date.phone)){
        console.log('手机号错误')
      }}
    const phonefocus =()=>{
      console.log('获取焦点')
    }
    return{
      ...toRefs(date),
      loginclick,
      phoneinput,
      phonefocus
    }
  }
}
</script>
