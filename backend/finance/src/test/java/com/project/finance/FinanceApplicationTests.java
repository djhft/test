package com.project.finance;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.project.finance.mapper.SavingsAccountsMapper;
import com.project.finance.mapper.UsersMapper;
import com.project.finance.service.impl.AiRequestsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {
    @Autowired
    AiRequestsServiceImpl aiRequestsService;
    @Test
    void contextLoads() throws NoApiKeyException, UploadFileException {
//        aiRequestsService.getAnswer("你好");
    }

}
