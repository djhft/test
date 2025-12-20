package com.project.finance.controller;


import com.project.finance.entity.AiRequests;
import com.project.finance.service.IAiRequestsService;
import com.project.finance.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private IAiRequestsService aiService;

//    @PostMapping("/analysis")
//    public Map<String, Object> analysis(@RequestBody Map<String,Object> body, HttpServletRequest request) {
//        Long uid = (Long) request.getAttribute("userId");
//        AiRequests r = aiService.createRequest(uid, body);
//        // 返回示例结果（同步）
//        return Map.of("id", r.getId(), "result", JsonUtil.fromJson(r.getResult(), Map.class));
//    }

    @GetMapping("/analysis/{id}")
    public AiRequests get(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        AiRequests r = aiService.getById(id);
        if (r == null) throw new RuntimeException("Not found");
        if (r.getUserId() != null && !r.getUserId().equals(uid)) throw new RuntimeException("Forbidden");
        return r;
    }
}
