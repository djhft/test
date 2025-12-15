import request from './request';

// 登录：body { usernameOrEmailOrPhone, password }
export function login(payload) {
    return request.post('/api/auth/login', payload);
}

// 注册：body { email, phone?, username?, password }
export function register(payload) {
    return request.post('/api/auth/register', payload);
}