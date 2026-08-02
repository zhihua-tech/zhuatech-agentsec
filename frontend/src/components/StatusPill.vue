<!-- Copyright 2026 上海如静知华信息科技有限公司 -->
<script setup>defineProps({text:String})</script><template><span class="status" :class="{'is-red':['报警','异常','驳回','超时','有风险','待判定'].includes(text),'is-green':['运行','正常','通过','已发布','已达成','已解决','已完成'].includes(text),'is-blue':['执行中','预测中','审批中','处理中','推进中','待确认','待发布'].includes(text)}"><i></i>{{text}}</span></template>
