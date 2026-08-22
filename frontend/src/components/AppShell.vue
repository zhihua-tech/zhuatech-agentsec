<script setup>
/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
import AppIcon from './AppIcon.vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { domain } from '../config/domain'

const props = defineProps({ mode: { type: String, default: 'admin' } })
const auth = useAuthStore()
const router = useRouter()
const menus = props.mode === 'shopfloor' ? domain.fieldMenus : domain.adminMenus
const theme = { '--green': domain.theme.primary, '--dark': domain.theme.dark, '--orange': domain.theme.accent }
function logout() { auth.logout(); router.push('/login') }
</script>

<template>
  <div class="app-shell" :class="`mode-${mode}`" :style="theme">
    <aside>
      <div class="brand"><div class="brand-mark">ZH</div><div><b>知华 {{ domain.code }}</b><small>{{ domain.systemName }}</small></div></div>
      <div class="line-card" v-if="mode === 'shopfloor'"><span>{{ domain.fieldContextLabel }}</span><b>{{ domain.fieldWorkspace }}</b><small>{{ domain.fieldContext }}</small></div>
      <nav><router-link v-for="m in menus" :key="m[0]" :to="m[0]"><AppIcon :name="m[1]"/><span>{{ m[2] }}</span><em v-if="m[3]">{{ m[3] }}</em></router-link></nav>
      <div class="aside-foot"><span>社区源码版 · v1.0.0</span><a href="https://www.zhuatech.cn/" target="_blank">www.zhuatech.cn</a></div>
    </aside>
    <main>
      <header><div><span class="workspace">{{ mode === 'shopfloor' ? domain.fieldWorkspace : domain.workspace }}</span><span class="period">{{ domain.period }}</span></div><div class="header-actions"><span class="shift-live"><i></i> {{ domain.liveText }}</span><button class="icon-button"><AppIcon name="bell"/><i></i></button><div class="avatar">{{ (auth.user?.fullName || (mode === 'shopfloor' ? domain.fieldUser : domain.adminUser)).slice(0,1) }}</div><div class="user"><b>{{ auth.user?.fullName || (mode === 'shopfloor' ? domain.fieldUser : domain.adminUser) }}</b><small>{{ mode === 'shopfloor' ? domain.fieldRole : domain.adminRole }}</small></div><button class="logout" @click="logout">退出</button></div></header>
      <section class="page"><slot/></section>
    </main>
  </div>
</template>
