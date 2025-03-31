import {post,get} from "./service"

//登录方法
export const loginApi=data=>{
    return post({
        url:'/host',
        data
    })
}

//获取账单列表
export const BillListApi=params=>{
    return get({
        url:'/login/bill/findbill',
        params
    })
}

//获取账单类别列表
export const GetBillCategoriesApi = () => {
    return get({
        url: '/login/bill/categories',
    })
}

//获取资金流向列表
export const GetBillTypesApi = () => {
    return get({
        url: '/login/bill/types',
    })
}

export const AddBillApi=data=>{
    return post({
        url:'/login/bill/addbill',
        data
    })
}

//修改账单数据
export const UpdateBillApi = (data) => {
    return post({
        url: '/login/bill/update',
        method: 'post',
        data
    })
}

//删除账单
export const DeleteBillApi = (data) => {
    return post({
        url: `/login/bill/delete`,
        method: 'post',
        data
    })
}

//注册用户
export const AddUserApi=data=>{
    return post({
        url:'/host/adduser',
        data
    })
}

//修改用户信息
export const UserUpdata=(data)=>{
    return post({
        url:'/login/user/userupdata',
        data
    })
}

// 管理员 API

// 获取所有账单
export const AdminBillListApi = (params) => {
    return get({
        url: '/admin/allbills',
        params
    })
}

// 获取所有用户
export const GetUsersApi = (params) => {
    return get({
        url: '/admin/users',
        params
    })
}

// 更新用户
export const UpdateUserApi = (data) => {
    return post({
        url: '/admin/users/update',
        data
    })
}

// 删除用户
export const DeleteUserApi = (data) => {
    return post({
        url: '/admin/users/delete',
        data
    })
}

// 获取系统设置
export const GetSettingsApi = () => {
    return get({
        url: '/admin/settings'
    })
}

// 更新系统设置
export const UpdateSettingsApi = (data) => {
    return post({
        url: '/admin/settings/update',
        data
    })
}

// 通知管理相关接口
// 获取通知列表
export const GetNotificationsApi = (params) => {
  return get({
    url: '/admin/notifications',
    params
  })
}

// 添加通知
export const AddNotificationApi = (data) => {
  return post({
    url: '/admin/addNotifications',
    data
  })
}

// 更新通知
export const UpdateNotificationApi = (data) => {
  return post({
      url: `/admin/updateNotifications`,
      data
  })
}

// 删除通知
export const DeleteNotificationApi = (data) => {
  return post({
    url: `/admin/deleteNotifications`,
    data
  })
}

// 获取所有账单类别
export const GetAllCategoriesApi = () => {
    return get({
        url: '/admin/categories'
    })
}

// 添加账单类别
export const AddCategoryApi = (data) => {
    return post({
        url: '/admin/categories/add',
        data
    })
}

// 更新账单类别
export const UpdateCategoryApi = (data) => {
    return post({
        url: '/admin/categories/update',
        data
    })
}

// 删除账单类别
export const DeleteCategoryApi = (data) => {
    return post({
        url: '/admin/categories/delete',
        data
    })
}

// 用户端通知相关接口
// 获取用户通知列表
export const GetUserNotificationsApi = (params) => {
    return get({
        url: '/login/notice/list',
        params
    })
}

// 账单统计相关接口
// 获取总收支统计
export const GetBillSummaryApi = (params) => {
  return get({
    url: '/login/bill/summary',
    params
  })
}

// 获取分类统计
export const GetBillCategoryStatsApi = (params) => {
  return get({
    url: '/login/bill/category-stats',
    params
  })
}

// 获取收支趋势
export const GetBillTrendApi = (params) => {
  return get({
    url: '/login/bill/trend',
    params
  })
}

// 商品相关接口
// 获取商品列表
export const GetProductsApi = (params) => {
  return get({
    url: '/product/products',
    params
  })
}

// 搜索商品
export const SearchProductsApi = (params) => {
  return get({
    url: '/products/search',
    params
  })
}

// 获取商品详情
export const GetProductDetailApi = (id) => {
  return get({
    url: `/product/products/${id}`
  })
}

// 添加到购物车
export const AddToCartApi = (data) => {
  return post({
    url: '/product/cart/add',
    data
  })
}

// 购物车相关接口
// 获取购物车列表
export const GetCartListApi = () => {
  return get({
    url: '/product/cart/list'
  })
}

// 更新购物车商品数量
export const UpdateCartQuantityApi = (data) => {
  return post({
    url: '/product/cart/update',
    data
  })
}

// 删除购物车商品
export const DeleteCartItemApi = (data) => {
  return post({
    url: '/product/cart/delete',
    data
  })
}

// 批量删除购物车商品
export const BatchDeleteCartItemsApi = (data) => {
  return post({
    url: '/product/cart/batch-delete',
    data
  })
}

