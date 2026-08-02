<!-- Copyright 2026 上海如静知华信息科技有限公司 -->
<script setup>defineProps({items:Array})</script><template><div class="metric-grid"><div v-for="m in items" :key="m[0]" class="metric"><div class="metric-top"><span>{{m[0]}}</span><i :class="m[3]"></i></div><strong>{{m[1]}}</strong><small>{{m[2]}}</small></div></div></template>
