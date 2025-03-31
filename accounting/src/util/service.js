import axios from "axios";
import {ElLoading} from 'element-plus'
import { ElMessage } from 'element-plus'
let loadingObj = null
const Service = axios.create({
    timeout:3000,
    baseURL: "http://localhost:8080",
    headers:{
        "Content-Type": "application/json; charset=UTF-8",
        "token": JSON.parse(localStorage.getItem("loginData")).token
    }
})
//请求之前
Service.interceptors.request.use(config=>{
    loadingObj=ElLoading.service({
        lock: true,
        text:'Loading',
        background:'rgba (0,0,0,0.7)'
    })
    return config
})
//响应拦截
Service.interceptors.response.use(res=>{
    loadingObj.close()
    const data = res.data
    if (!data.data){
        ElMessage({
            message: data.message||"服务器错误",
            type: 'error',
            plain: true,
        })
        return data
    }
    return data
},err=>{
    loadingObj.close()
    ElMessage({
        message: "服务器错误",
        type: "error",
        duration:2000,
    })
})

//post请求
export const post=config=>{
    return Service({
        ...config,
        data:config.data,
        method:'post'
    })
}
//get请求
export const get=config=>{
    return Service({
        ...config,
        params:config.params,
        method:'get'
    })
}