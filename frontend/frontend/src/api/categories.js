import request from './request';

export function getCategories(params) {
    // params: { type }
    return request.get('/api/categories', { params });
}

export function createCategory(data) {
    return request.post('/api/categories', data);
}

export function updateCategory(id, data) {
    return request.put(`/api/categories/${id}`, data);
}

export function deleteCategory(id) {
    return request.delete(`/api/categories/${id}`);
}