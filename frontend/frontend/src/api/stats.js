import request from './request';

// 获取统计数据汇总
// @param {Object} params - 请求参数
// @param {string} params.start - 开始时间，必需
// @param {string} params.end - 结束时间，必需
export function getSummary(params) {
    // 参数校验
    if (!params.start || !params.end) {
        throw new Error('start and end parameters are required');
    }
    return request({
        url: '/api/stats/summary',
        method: 'get',
        params
    });
}

// 获取统计数据序列
// @param {Object} params - 请求参数
// @param {string} params.start - 开始时间，必需
// @param {string} params.end - 结束时间，必需
// @param {string} params.period - 时间周期，可选值为 daily 或 monthly
export function getSeries(params) {
    // 参数校验
    if (!params.start || !params.end) {
        throw new Error('start and end parameters are required');
    }
    if (params.period && !['daily', 'monthly'].includes(params.period)) {
        throw new Error('period must be either daily or monthly');
    }
    return request({
        url: '/api/stats/series',
        method: 'get',
        params
    });
}
