import request from './request';

export function listTransactions(params) {
    // params: { start, end, page, pageSize, type, categoryId }
    return request.get('/api/transactions', { params });
}

export function createTransaction(data) {
    return request.post('/api/transactions', data);
}

export function getTransaction(id) {
    return request.get(`/api/transactions/${id}`);
}

export function updateTransaction(id, data) {
    return request.put(`/api/transactions/${id}`, data);
}

export function deleteTransaction(id) {
    return request.delete(`/api/transactions/${id}`);
}