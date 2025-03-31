import { createRouter, createWebHistory } from 'vue-router'
import store from '../store/index.js'
import unInfo from "@/store/state/userinfo.state";
import uInfo from "@/store/state/userinfo.state";

const routes = [
    {
        path: '/',
        redirect: '/host',
    },
    {
        path: '/host',
        name: 'host',
        component:()=>import('../views/Pages/Host.vue'),
    },
    {
        path: '/admin',
        name: 'admin',
        component:()=>import('../views/Pages/Admin.vue'),
        redirect:'/admin/hello',
        children: [
            {
                path: '/admin/hello',
                name: 'Admin-hello',
                component:()=>import('../views/Pages/Hello.vue'),
            },
            {
                path: '/admin/allbills',
                name: 'allbills',
                component:()=>import('../views/Pages/AdminAllBills.vue'),
            },
            {
                path: '/admin/users',
                name: 'users',
                component:()=>import('../views/Pages/AdminUsers.vue'),
            },
            {
                path: '/admin/settings',
                name: 'settings',
                component:()=>import('../views/Pages/AdminSettings.vue'),
            },
            {
                path: '/admin/notifications',
                name: 'notifications',
                component:()=>import('../views/Pages/AdminNotifications.vue'),
            },
            {
                path: '/admin/categories',
                name: 'categories',
                component:()=>import('../views/Pages/AdminCategories.vue'),
            }
        ]
    },
    {
        path:'/login',
        name:'Login',
        component:()=>import( '../views/Login.vue'),
        redirect:'/login/hello',
        children:[
            {
                path: '/login/hello',
                name:'hello',
                component:()=>import('../views/Pages/Hello.vue')
            },
            {
                path:'/login/bill',
                name:'bill',
                component:()=>import( '../views/Pages/Bill.vue'),
            },
            {
                path:'/login/order',
                name:'order',
                component:()=>import( '../views/Pages/Order.vue'),
            },
            {
                path:'/login/notice',
                name:'notice',
                component:()=>import( '../views/Pages/Notice.vue'),
            },
            {
                path:'/login/user',
                name:'userupdata',
                component:()=>import( '../views/Pages/UserUpdata.vue'),
            }
        ]
    },
    {
        path:'/user',
        name:'user',
        component:()=>import( '../views/Pages/User.vue'),
        redirect:'/user/shop',
        children:[
            {
                path: '/user/shop',
                name: 'userShop',
                component:()=>import('../views/Pages/Shop.vue'),
            },
            {
                path: '/user/cart',
                name: 'userCart',
                component:()=>import('../views/Pages/Cart.vue'),
            },
            {
                path: '/user/orders',
                name: 'userOrders',
                component:()=>import('../views/Pages/Orders.vue'),
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/host'
    }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to, from, next) => {
    console.log('[路由守卫] 目标路径:', to.path);
    console.log("store", store.state.uInfo)
    const unIfo = store.state.uInfo.userInfo
    if (!unIfo.username){
        if (to.path === "/host") {
            next()
            return
        }
        next("/host")
    }else{
        next()
    }
})
export default router
