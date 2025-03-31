export default {
    namespaced:true,
    //全局状态初始化
    state: {
        count: 1,
    },
    getters: {
        countStatus(state){
            return state.count>=1
        }
    },
    mutations: {
        SetCount(state, num) {
            state.count = num
        }
    },
    actions: {
        setCountPromise(context, num) {
            return new Promise((resolve, reject) => {
                if (num == 2){
                    context.commit('SetCount',num)
                    resolve()
                }else {
                    reject('登录失败')
                }
            })
        }
    }
}