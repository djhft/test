import request from './request';

export function getBudgets(params) {
    return request.get('/api/budgets', { params });
}

export function createBudget(data) {
    return request.post('/api/budgets', data);
}

export function updateBudget(id, data) {
    return request.put(`/api/budgets/${id}`, data);
}

export function deleteBudget(id) {
    return request.delete(`/api/budgets/${id}`);
}