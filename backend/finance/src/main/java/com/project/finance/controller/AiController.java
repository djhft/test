package com.project.finance.controller;

import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.project.finance.service.IAiRequestsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private IAiRequestsService aiService;

    /**
     * 处理AI对话请求
     */
    @PostMapping("/analysis")
    @ResponseBody
    public Map<String, Object> analyzeFinance(
            @RequestBody Map<String, Object> request,
            HttpSession session,
            HttpServletRequest requests) {

        Long uid = (Long) requests.getAttribute("userId");
        Map<String, Object> response = new HashMap<>();

        try {
            String message = (String) request.get("message");
            Map<String, String> period = (Map<String, String>) request.get("period");

            if (period == null || period.get("start") == null || period.get("end") == null) {
                response.put("success", false);
                response.put("error", "缺少时间段参数");
                return response;
            }

            String startDate = period.get("start");
            String endDate = period.get("end");

            // 1. 从session获取或初始化聊天历史
            @SuppressWarnings("unchecked")
            List<ChatMessage> chatHistory = (List<ChatMessage>) session.getAttribute("aiChatHistory_" + startDate + "_" + endDate);
            System.out.println("1111111");
            System.out.println(chatHistory);


            // 3. 调用AI服务
            String aiReply = aiService.getAnswer(uid,chatHistory, startDate, endDate);

            // 4. 构建响应
            response.put("success", true);
            response.put("reply", aiReply);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "AI分析失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 清除对话历史
     */
    @PostMapping("/clear")
    @ResponseBody
    public Map<String, Object> clearChatHistory(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String startDate = request.get("start");
            String endDate = request.get("end");

            if (startDate != null && endDate != null) {
                session.removeAttribute("aiChatHistory_" + startDate + "_" + endDate);
            } else {
                // 清除所有AI相关的session属性
                for (String key : Collections.list(session.getAttributeNames())) {
                    if (key.startsWith("aiChatHistory_")) {
                        session.removeAttribute(key);
                    }
                }
            }

            response.put("success", true);
            response.put("message", "对话历史已清除");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "清除对话历史失败: " + e.getMessage());
        }

        return response;
    }
}