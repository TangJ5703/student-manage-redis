import axios from 'axios'
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";


export let isRelogin = { show: false };


const serverConfig = {
    baseURL: "http://localhost:8080",
    useTokenAuthorization: true
}


// 创建 axios 请求实例
const Axios = axios.create({
    baseURL: serverConfig.baseURL, // 基础请求地址
    timeout: 10000, // 请求超时设置
    withCredentials: false, // 跨域请求是否需要携带 cookie
});


// 创建请求拦截
Axios.interceptors.request.use(
    (config) => {
        // 如果开启 token 认证
        if (serverConfig.useTokenAuthorization) {
            config.headers["Authorization"] = localStorage.getItem("token"); // 请求头携带 token
        }
        return config;
    },
    (error) => {
        Promise.reject(error);
    }
);


// 创建响应拦截
Axios.interceptors.response.use(res => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg =  res.data.msg
        // 二进制数据则直接返回
        if (res.request.responseType ===  'blob' || res.request.responseType ===  'arraybuffer') {
            return res.data
        }
        if (code === 401) {
            if (!isRelogin.show) {
                isRelogin.show = true;
                ElMessageBox.confirm('您未登录或登录状态已过期', '系统提示', { confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning' }).then(() => {
                    isRelogin.show = false;
                    location.href = '/login';
                }).catch(() => {
                    isRelogin.show = false;
                });
            }
            return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
        } else if (code === 500) {
            ElMessage({ message: msg, type: 'error' })
            return Promise.reject(new Error(msg))
        } else if (code === 601) {
            ElMessage({ message: msg, type: 'warning' })
            return Promise.reject('error')
        } else if (code !== 200) {
            ElNotification.error({ title: msg })
            return Promise.reject('error')
        } else {
            return res.data
        }
    },
    error => {
        console.log('err' + error)
        let { message } = error;
        if (message === "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        ElMessage({ message: message, type: 'error', duration: 5 * 1000 })
        return Promise.reject(error)
    }
)
export default Axios;