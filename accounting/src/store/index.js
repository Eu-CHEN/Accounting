import { createStore } from 'vuex'
import number from '../store/state/num.state.js'
import uInfo from '../store/state/userinfo.state'
export default createStore({

  modules: {
    number,
    uInfo
  }
})
