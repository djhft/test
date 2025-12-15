import axios from 'axios';
import router from '../router';

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000
});

request.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

// 响应拦截：遇到 401 跳转到登录页；可根据需要扩展错误处理
request.interceptors.response.use(
    res => res,
    err => {
        if (err.response) {
            if (err.response.status === 401) {
                localStorage.removeItem('token');
                // 防止循环跳转
                if (router && router.currentRoute.value.path !== '/auth') {
                    router.push('/auth');
                }
            }
        }
        return Promise.reject(err);
    }
);

export default request;