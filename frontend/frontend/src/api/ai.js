// src/api/ai.js
import request from './request';

/**
 * AI 财务分析接口
 * @param {Object} data - 请求数据
 * @param {string} data.message - 用户消息内容
 * @param {Object} data.period - 统计周期
 * @param {string} data.period.start - 开始日期 (YYYY-MM-DD)
 * @param {string} data.period.end - 结束日期 (YYYY-MM-DD)
 * @returns {Promise} 包含分析结果的 Promise
 *
 * Controller 端点: POST /api/ai/analysis
 * 响应格式: { success: boolean, reply: string, error?: string }
 */
export function aiAnalysis(data) {
    return request({
        url: '/api/ai/analysis',
        method: 'post',
        data,
        // 添加超时设置，AI 分析可能需要较长时间
        timeout: 400000,
        // 添加请求头标识
        headers: {
            'X-Request-Type': 'ai-analysis'
        }
    })
}

/**
 * 清空指定周期的对话历史
 * @param {Object} data - 请求数据
 * @param {string} data.start - 开始日期 (YYYY-MM-DD)
 * @param {string} data.end - 结束日期 (YYYY-MM-DD)
 * @returns {Promise} 操作结果 Promise
 *
 * Controller 端点: POST /api/ai/clear-history
 * 响应格式: { success: boolean, message?: string, error?: string }
 */
export function clearChatHistory(data) {
    // 参数验证
    if (!data.start || !data.end) {
        return Promise.reject(new Error('日期范围参数缺失'))
    }

    // 日期格式验证 (YYYY-MM-DD)
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/
    if (!dateRegex.test(data.start) || !dateRegex.test(data.end)) {
        return Promise.reject(new Error('日期格式错误，应为 YYYY-MM-DD'))
    }

    return request({
        url: '/api/ai/clear',
        method: 'post',
        data,
        headers: {
            'X-Request-Type': 'clear-history'
        }
    })
}

