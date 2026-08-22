/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
import axios from 'axios'
const http=axios.create({baseURL:import.meta.env.VITE_API_BASE_URL||'/api',timeout:10000})
http.interceptors.request.use(c=>{const token=localStorage.getItem('zhuatech-agentsec-token');if(token)c.headers.Authorization=`Bearer ${token}`;return c})
export default http
