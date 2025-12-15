import request from './request';

export function getSavings(params) {
    return request.get('/api/savings_accounts', { params });
}

export function createSavings(data) {
    return request.post('/api/savings_accounts', data);
}

export function updateSavings(id, data) {
    return request.put(`/api/savings_accounts/${id}`, data);
}

export function deleteSavings(id) {
    return request.delete(`/api/savings_accounts/${id}`);
}