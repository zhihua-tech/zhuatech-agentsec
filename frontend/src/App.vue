<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import AiPracticalWorkbench from './components/AiPracticalWorkbench.vue'
const route = useRoute()
const auth = useAuthStore()
</script>
<template><router-view/><AiPracticalWorkbench v-if="route.path !== '/login' && auth.user"/></template>
