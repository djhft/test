import request from './request';

export function login(payload) {
    return request.post('/api/auth/login', payload);
}

export function register(payload) {
    return request.post('/api/auth/register', payload);
}
