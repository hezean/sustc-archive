import {createApp} from 'vue'
import ElementUI from 'element-plus'
import 'element-plus/theme-chalk/index.css'

import App from './App'
import router from './router'

createApp(App)
    .use(router)
    .use(ElementUI)
    .mount('#app')
