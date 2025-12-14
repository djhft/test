import request from './request';

export function aiAnalysis(data) {
    return request.post('/api/ai/analysis', data);
}
