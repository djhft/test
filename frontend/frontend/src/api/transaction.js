import request from './request';

export function createTransaction(payload) {
    return request.post('/api/transactions', payload);
}

export function listTransactions(userId) {
    return request.get('/api/transactions', { params: { userId } });
}
