import request from './request';


export function listTransactions(params) {
    // params: { start, end, page, pageSize, type, categoryId }
    return request.get('/api/transactions', { params });
}

export function createTransaction(data) {
    return request.post('/api/transactions', data);
}

export function getTransactions(params) {
    return request({
        url: '/api/transactions',
        method: 'get',
        params
    });
}

export function updateTransaction(id, data) {
    return request.put(`/api/transactions/${id}`, data);
}

export function deleteTransaction(id) {
    return request.delete(`/api/transactions/${id}`);
}

export function   getSpending(params) {
    // 参数说明：
    // params: {
    //   start: 开始日期 (可选),
    //   end: 结束日期 (可选)
    // }
    return request.get('/api/transactions/spending', {params})
}

export function  getExpenseSummary(params) {
    // 参数说明：
    // params: {
    //   start: 开始日期 (可选),
    //   end: 结束日期 (可选)
    // }
    return request.get('/api/transactions/spending/expense_summary', { params });
}

export function  getIncomeSummary(params) {
    // 参数说明：
    // params: {
    //   start: 开始日期 (可选),
    //   end: 结束日期 (可选)
    // }
    return request.get('/api/transactions/spending/income_summary', { params });
}