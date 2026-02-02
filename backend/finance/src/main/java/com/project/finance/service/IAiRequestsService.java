package com.project.finance.service;

import ai.z.openapi.service.model.ChatMessage;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.project.finance.entity.AiRequests;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
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

    String getAnswer(Long uid, List<ChatMessage> chatHistory, String startDate, String endDate);
}
