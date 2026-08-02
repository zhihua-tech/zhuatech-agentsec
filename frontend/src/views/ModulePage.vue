<!-- Copyright 2026 上海如静知华信息科技有限公司 -->
<script setup>
import AppShell from '../components/AppShell.vue'
import StatusPill from '../components/StatusPill.vue'
import { domain, records, resources, reviews } from '../config/domain'
const props=defineProps({mode:String,module:String})
const meta=domain.moduleTitles[props.module]||['协同中心','统一处理业务记录与过程状态']
const data=['resources','equipment','devices'].includes(props.module)?resources:props.module==='reviews'?reviews:records
</script>
<template><AppShell :mode="mode"><div class="page-head"><div><span class="breadcrumb">{{ mode==='shopfloor'?domain.fieldTitle:domain.adminTitle }} / {{ meta[0] }}</span><h1>{{ meta[0] }}</h1><p>{{ meta[1] }}</p></div><button class="primary">+ 新建记录</button></div><section class="panel list-panel"><div class="toolbar"><div class="tabs"><button class="active">全部 <em>{{ data.length }}</em></button><button>待处理</button><button>已完成</button></div><div class="filters"><input placeholder="输入编号或关键词"/><select><option>全部状态</option></select><button class="secondary">查询</button></div></div><table><thead><tr><th>业务编号</th><th>业务内容</th><th>所属范围</th><th>关键数据</th><th>状态</th><th></th></tr></thead><tbody><tr v-for="(item,i) in data" :key="i"><td><b>{{item.no||item.code}}</b></td><td>{{item.name||item.title}}</td><td>{{item.unit||item.type||item.group}}</td><td>{{item.plan!=null?item.done+' / '+item.plan:item.health!=null?item.health+'% 健康度':item.detail}}</td><td><StatusPill :text="item.status||item.result"/></td><td><button class="link-button">查看详情</button></td></tr></tbody></table></section></AppShell></template>
