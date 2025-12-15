package com.project.finance.service.impl;

import com.project.finance.entity.AiRequests;
import com.project.finance.mapper.AiRequestsMapper;
import com.project.finance.service.IAiRequestsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.finance.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@Service
public class AiRequestsServiceImpl extends ServiceImpl<AiRequestsMapper, AiRequests> implements IAiRequestsService {
    @Autowired
    private AiRequestsMapper aiRequestMapper;

    @Override
    public AiRequests createRequest(Long userId, Map<String, Object> input) {
        AiRequests r = new AiRequests();
        r.setUserId(userId);
        r.setInput(JsonUtil.toJson(input));
        r.setStatus("pending");
        aiRequestMapper.insert(r);
        // 简单同步示例分析：生成 fake result，更新表
        Map<String, Object> fake = Map.of(
                "summary", "示例分析：你的支出集中在餐饮与购物。",
                "suggestions", new String[]{"减少外出就餐", "制定购物预算"}
        );
        r.setResult(JsonUtil.toJson(fake));
        r.setStatus("done");
        aiRequestMapper.updateById(r);
        return r;
    }

    @Override
    public AiRequests getById(Long id) {
        return aiRequestMapper.selectById(id);
    }
}
