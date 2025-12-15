import request from './request';

// start, end required
export function getSummary(params) {
    return request.get('/api/stats/summary', { params });
}

// start, end, period=daily|monthly
export function getSeries(params) {
    return request.get('/api/stats/series', { params });
}