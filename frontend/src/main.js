import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 语言包用中文（默认英文，日期选择器、分页器都会是英文）
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
