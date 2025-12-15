import request from './request';

// 获取当前用户
export function getMe() {
    return request.get('/api/users/me');
}

// 更新当前用户（不包含密码）
export function updateMe(payload) {
    return request.put('/api/users/me', payload);
}