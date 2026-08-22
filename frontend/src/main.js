/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
import {createApp} from 'vue'; import {createPinia} from 'pinia'; import App from './App.vue'; import router from './router'; import './assets/main.css';
createApp(App).use(createPinia()).use(router).mount('#app')
