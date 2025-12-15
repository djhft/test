package com.project.finance.service;

import com.project.finance.entity.AiRequests;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
public interface IAiRequestsService extends IService<AiRequests> {
    AiRequests createRequest(Long userId, Map<String,Object> input);
    AiRequests getById(Long id);
}
